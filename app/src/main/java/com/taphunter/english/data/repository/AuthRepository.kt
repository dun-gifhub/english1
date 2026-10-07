package com.taphunter.english.data.repository

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import androidx.credentials.ClearCredentialStateRequest
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialCancellationException
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GetSignInWithGoogleOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential.Companion.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.auth
import com.taphunter.english.R
import com.taphunter.english.data.models.UserProfile
import com.taphunter.english.data.models.UserRole
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class AuthRepository(
    private val context: Context,
    private val db: FirebaseFirestore
) {
    constructor(context: Context) : this(
        context,
        FirebaseFirestore.getInstance(
            context.applicationContext.getString(R.string.firestore_database_id)
        )
    )

    private val auth: FirebaseAuth = Firebase.auth
    private val prefs: SharedPreferences = context.getSharedPreferences("tap_hunter_auth", Context.MODE_PRIVATE)

    private val _currentUser = MutableStateFlow<UserProfile?>(null)
    val currentUser: StateFlow<UserProfile?> = _currentUser.asStateFlow()

    private val _authError = MutableStateFlow<String?>(null)
    val authError: StateFlow<String?> = _authError.asStateFlow()

    private val _isAuthenticating = MutableStateFlow<Boolean>(false)
    val isAuthenticating: StateFlow<Boolean> = _isAuthenticating.asStateFlow()

    private var profileListener: ListenerRegistration? = null

    init {
        auth.addAuthStateListener { firebaseAuth ->
            val firebaseUser = firebaseAuth.currentUser
            if (firebaseUser != null) {
                attachProfileListener(firebaseUser.uid, firebaseUser.email ?: "", firebaseUser.displayName ?: "Hunter")
            } else {
                profileListener?.remove()
                profileListener = null
                _currentUser.value = null
            }
        }
    }

    private fun attachProfileListener(uid: String, email: String, name: String) {
        profileListener?.remove()
        val userDocRef = db.collection("users").document(uid)

        profileListener = userDocRef.addSnapshotListener { snapshot, error ->
            if (error != null) {
                handleFirestoreError(error, OperationType.GET, userDocRef.path)
                return@addSnapshotListener
            }

            if (snapshot != null && snapshot.exists()) {
                val roleStr = snapshot.getString("role") ?: UserRole.STUDENT.name
                val role = try { UserRole.valueOf(roleStr) } catch (_: Exception) { UserRole.STUDENT }
                val grade = snapshot.getLong("selectedGrade")?.toInt() ?: 10
                val level = snapshot.getLong("level")?.toInt() ?: 1
                val xp = snapshot.getLong("xp")?.toInt() ?: 100
                val highestScore = snapshot.getLong("highestScore")?.toInt() ?: 1200
                val streakDays = snapshot.getLong("streakDays")?.toInt() ?: 1
                val friendCode = snapshot.getString("friendCode") ?: "TAP-${uid.take(4).uppercase()}"
                val displayName = snapshot.getString("displayName") ?: name
                val status = snapshot.getString("status") ?: "Sẵn sàng săn từ vựng!"

                val profile = UserProfile(
                    uid = uid,
                    email = email,
                    displayName = displayName,
                    friendCode = friendCode,
                    role = role,
                    selectedGrade = grade,
                    level = level,
                    xp = xp,
                    highestScore = highestScore,
                    streakDays = streakDays,
                    status = status
                )
                _currentUser.value = profile
            } else {
                // Initialize default profile in Firestore on first sign in
                val friendCode = "TAP-" + (1000..9999).random()
                val initialData = hashMapOf(
                    "userId" to uid,
                    "email" to email,
                    "displayName" to name,
                    "friendCode" to friendCode,
                    "role" to UserRole.STUDENT.name,
                    "selectedGrade" to 10,
                    "level" to 1,
                    "xp" to 100,
                    "highestScore" to 1200,
                    "streakDays" to 1,
                    "status" to "Sẵn sàng săn từ vựng!",
                    "updatedAt" to FieldValue.serverTimestamp()
                )
                userDocRef.set(initialData, SetOptions.merge())
                    .addOnFailureListener { e ->
                        handleFirestoreError(e, OperationType.CREATE, userDocRef.path)
                    }
            }
        }
    }

    fun setRole(newRole: UserRole) {
        val user = _currentUser.value ?: return
        val uid = user.uid
        _currentUser.value = user.copy(role = newRole)

        val docRef = db.collection("users").document(uid)
        docRef.set(
            mapOf("role" to newRole.name, "updatedAt" to FieldValue.serverTimestamp()),
            SetOptions.merge()
        ).addOnFailureListener { e ->
            handleFirestoreError(e, OperationType.UPDATE, docRef.path)
        }
    }

    fun setSelectedGrade(grade: Int) {
        val user = _currentUser.value ?: return
        val uid = user.uid
        val clamped = grade.coerceIn(6, 12)
        _currentUser.value = user.copy(selectedGrade = clamped)

        val docRef = db.collection("users").document(uid)
        docRef.set(
            mapOf("selectedGrade" to clamped, "updatedAt" to FieldValue.serverTimestamp()),
            SetOptions.merge()
        ).addOnFailureListener { e ->
            handleFirestoreError(e, OperationType.UPDATE, docRef.path)
        }
    }

    fun addXpAndScore(pointsEarned: Int) {
        val user = _currentUser.value ?: return
        val uid = user.uid
        val newXp = user.xp + pointsEarned
        val newLevel = 1 + (newXp / 500)
        val newHighScore = maxOf(user.highestScore, pointsEarned)

        _currentUser.value = user.copy(
            xp = newXp,
            level = newLevel,
            highestScore = newHighScore
        )

        val docRef = db.collection("users").document(uid)
        docRef.set(
            mapOf(
                "xp" to newXp,
                "level" to newLevel,
                "highestScore" to newHighScore,
                "updatedAt" to FieldValue.serverTimestamp()
            ),
            SetOptions.merge()
        ).addOnFailureListener { e ->
            handleFirestoreError(e, OperationType.UPDATE, docRef.path)
        }
    }

    // Interactive Google Sign-In
    fun signInWithGoogle(
        activity: Activity,
        scope: CoroutineScope,
        onSuccess: () -> Unit = {},
        onError: (String) -> Unit = {}
    ) {
        val clientId = try {
            activity.getString(R.string.default_web_client_id)
        } catch (e: Exception) {
            _authError.value = "Chưa cấu hình Google Client ID."
            onError("Chưa cấu hình Google Client ID.")
            return
        }

        _isAuthenticating.value = true
        _authError.value = null

        val credentialManager = CredentialManager.create(activity)
        val signInOption = GetSignInWithGoogleOption.Builder(serverClientId = clientId).build()
        val request = GetCredentialRequest.Builder().addCredentialOption(signInOption).build()

        scope.launch {
            try {
                val result = credentialManager.getCredential(activity, request)
                val credential = result.credential
                if (credential is CustomCredential && credential.type == TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
                    val googleIdToken = GoogleIdTokenCredential.createFrom(credential.data).idToken
                    val authCredential = GoogleAuthProvider.getCredential(googleIdToken, null)
                    auth.signInWithCredential(authCredential).await()
                    _isAuthenticating.value = false
                    onSuccess()
                } else {
                    _isAuthenticating.value = false
                    _authError.value = "Loại thông tin xác thực không đúng."
                    onError("Loại thông tin xác thực không đúng.")
                }
            } catch (e: GetCredentialCancellationException) {
                Log.w("Auth", "Google Sign-In flow cancelled: ${e.message}", e)
                _isAuthenticating.value = false
            } catch (e: Exception) {
                Log.e("Auth", "Google Sign-In failed", e)
                _isAuthenticating.value = false
                val msg = e.localizedMessage ?: "Đăng nhập thất bại"
                _authError.value = msg
                onError(msg)
            }
        }
    }

    // Silent Auto Sign-In on App Startup
    fun attemptAutoSignIn(
        context: Context,
        scope: CoroutineScope
    ) {
        if (auth.currentUser != null) return

        val clientId = try {
            context.getString(R.string.default_web_client_id)
        } catch (_: Exception) {
            return
        }

        val credentialManager = CredentialManager.create(context)
        val googleIdOption = GetGoogleIdOption.Builder()
            .setFilterByAuthorizedAccounts(true)
            .setServerClientId(clientId)
            .setAutoSelectEnabled(true)
            .build()

        val request = GetCredentialRequest.Builder().addCredentialOption(googleIdOption).build()

        scope.launch {
            try {
                val result = credentialManager.getCredential(context, request)
                val credential = result.credential
                if (credential is CustomCredential && credential.type == TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
                    val googleIdToken = GoogleIdTokenCredential.createFrom(credential.data).idToken
                    val authCredential = GoogleAuthProvider.getCredential(googleIdToken, null)
                    auth.signInWithCredential(authCredential).await()
                }
            } catch (_: Exception) {
                // Ignore silent auth failure on cold start
            }
        }
    }

    fun signOut(activity: Activity, scope: CoroutineScope) {
        profileListener?.remove()
        profileListener = null
        auth.signOut()
        _currentUser.value = null
        val credentialManager = CredentialManager.create(activity)
        scope.launch {
            try {
                credentialManager.clearCredentialState(ClearCredentialStateRequest())
            } catch (e: Exception) {
                Log.e("Auth", "Failed to clear credential state", e)
            }
        }
    }
}
