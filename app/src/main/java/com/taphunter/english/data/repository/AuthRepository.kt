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

    private fun getCurrentMonthKey(): String {
        val cal = java.util.Calendar.getInstance()
        val y = cal.get(java.util.Calendar.YEAR)
        val m = cal.get(java.util.Calendar.MONTH) + 1
        return String.format(java.util.Locale.US, "%04d-%02d", y, m)
    }

    init {
        // Load local profile if saved
        loadLocalProfile()

        auth.addAuthStateListener { firebaseAuth ->
            val firebaseUser = firebaseAuth.currentUser
            if (firebaseUser != null) {
                attachProfileListener(firebaseUser.uid, firebaseUser.email ?: "", firebaseUser.displayName ?: "Hunter")
            } else {
                profileListener?.remove()
                profileListener = null
                // If there is a local account, keep it active
                if (_currentUser.value?.uid?.startsWith("local_") != true) {
                    _currentUser.value = null
                }
            }
        }
    }

    private fun loadLocalProfile() {
        val uid = prefs.getString("local_uid", null) ?: return
        val name = prefs.getString("local_name", "Học Sinh Lương Phú") ?: "Học Sinh Lương Phú"
        val className = prefs.getString("local_class", "10A1") ?: "10A1"
        val baseGrade = prefs.getInt("local_base_grade", 10)
        val regYear = prefs.getInt("local_reg_year", 2026)
        val roleStr = prefs.getString("local_role", UserRole.STUDENT.name) ?: UserRole.STUDENT.name
        val role = try { UserRole.valueOf(roleStr) } catch (_: Exception) { UserRole.STUDENT }
        val xp = prefs.getInt("local_xp", 0)
        val level = prefs.getInt("local_level", 1)
        val highScore = prefs.getInt("local_high_score", 0)
        val monthlyScore = prefs.getInt("local_monthly_score", 0)
        val lastMonth = prefs.getString("local_last_month", "") ?: ""
        val currentMonth = getCurrentMonthKey()

        val actualMonthly = if (lastMonth == currentMonth) monthlyScore else 0

        _currentUser.value = UserProfile(
            uid = uid,
            email = "$uid@luongphu.edu.vn",
            displayName = name,
            customClassName = className,
            baseGrade = baseGrade,
            registeredAcademicYear = regYear,
            friendCode = "LP-${uid.takeLast(4).uppercase()}",
            role = role,
            selectedGrade = baseGrade,
            level = level,
            xp = xp,
            highestScore = highScore,
            monthlyScore = actualMonthly,
            lastScoreMonthKey = currentMonth,
            streakDays = 1,
            status = "Sẵn sàng săn từ vựng THPT Lương Phú!"
        )
    }

    private fun saveLocalProfile(profile: UserProfile) {
        prefs.edit()
            .putString("local_uid", profile.uid)
            .putString("local_name", profile.displayName)
            .putString("local_class", profile.customClassName)
            .putInt("local_base_grade", profile.baseGrade)
            .putInt("local_reg_year", profile.registeredAcademicYear)
            .putString("local_role", profile.role.name)
            .putInt("local_xp", profile.xp)
            .putInt("local_level", profile.level)
            .putInt("local_high_score", profile.highestScore)
            .putInt("local_monthly_score", profile.monthlyScore)
            .putString("local_last_month", profile.lastScoreMonthKey)
            .apply()
    }

    private fun attachProfileListener(uid: String, email: String, name: String) {
        profileListener?.remove()
        val userDocRef = db.collection("users").document(uid)

        profileListener = userDocRef.addSnapshotListener { snapshot, error ->
            if (error != null) {
                handleFirestoreError(error, OperationType.GET, userDocRef.path)
                return@addSnapshotListener
            }

            val currentMonth = getCurrentMonthKey()

            if (snapshot != null && snapshot.exists()) {
                val roleStr = snapshot.getString("role") ?: UserRole.STUDENT.name
                val role = try { UserRole.valueOf(roleStr) } catch (_: Exception) { UserRole.STUDENT }
                val grade = snapshot.getLong("selectedGrade")?.toInt() ?: 10
                val customClassName = snapshot.getString("customClassName") ?: "10A1"
                val baseGrade = snapshot.getLong("baseGrade")?.toInt() ?: grade
                val registeredAcademicYear = snapshot.getLong("registeredAcademicYear")?.toInt() ?: 2026
                val level = snapshot.getLong("level")?.toInt() ?: 1
                val xp = snapshot.getLong("xp")?.toInt() ?: 0
                val highestScore = snapshot.getLong("highestScore")?.toInt() ?: 0
                val monthlyScore = snapshot.getLong("monthlyScore")?.toInt() ?: 0
                val lastScoreMonthKey = snapshot.getString("lastScoreMonthKey") ?: ""
                val streakDays = snapshot.getLong("streakDays")?.toInt() ?: 1
                val friendCode = snapshot.getString("friendCode") ?: "TAP-${uid.take(4).uppercase()}"
                val displayName = snapshot.getString("displayName") ?: name
                val status = snapshot.getString("status") ?: "Sẵn sàng săn từ vựng!"

                // Check monthly reset
                val activeMonthlyScore = if (lastScoreMonthKey == currentMonth) monthlyScore else 0

                val profile = UserProfile(
                    uid = uid,
                    email = email,
                    displayName = displayName,
                    customClassName = customClassName,
                    baseGrade = baseGrade,
                    registeredAcademicYear = registeredAcademicYear,
                    friendCode = friendCode,
                    role = role,
                    selectedGrade = grade,
                    level = level,
                    xp = xp,
                    highestScore = highestScore,
                    monthlyScore = activeMonthlyScore,
                    lastScoreMonthKey = currentMonth,
                    streakDays = streakDays,
                    status = status
                )
                _currentUser.value = profile
                saveLocalProfile(profile)
            } else {
                // Initialize default profile in Firestore on first sign in
                val friendCode = "LP-" + (1000..9999).random()
                val initialData = hashMapOf(
                    "userId" to uid,
                    "email" to email,
                    "displayName" to name,
                    "customClassName" to "10A1",
                    "baseGrade" to 10,
                    "registeredAcademicYear" to 2026,
                    "friendCode" to friendCode,
                    "role" to UserRole.STUDENT.name,
                    "selectedGrade" to 10,
                    "level" to 1,
                    "xp" to 0,
                    "highestScore" to 0,
                    "monthlyScore" to 0,
                    "lastScoreMonthKey" to currentMonth,
                    "streakDays" to 1,
                    "status" to "Sẵn sàng săn từ vựng THPT Lương Phú!",
                    "updatedAt" to FieldValue.serverTimestamp()
                )
                userDocRef.set(initialData, SetOptions.merge())
                    .addOnFailureListener { e ->
                        handleFirestoreError(e, OperationType.CREATE, userDocRef.path)
                    }
            }
        }
    }

    /**
     * Đăng ký hoặc cập nhật tài khoản học sinh / giáo viên với Tên, Lớp, Khối và Năm học.
     */
    fun registerOrUpdateAccount(
        displayName: String,
        customClassName: String,
        baseGrade: Int,
        registeredYear: Int = 2026,
        role: UserRole = UserRole.STUDENT
    ) {
        val current = _currentUser.value
        val uid = current?.uid ?: "local_${System.currentTimeMillis()}"
        val email = current?.email.takeIf { !it.isNullOrBlank() } ?: "$uid@luongphu.edu.vn"
        val currentMonth = getCurrentMonthKey()

        val clampedGrade = baseGrade.coerceIn(6, 12)
        val validClassName = customClassName.trim().ifBlank { "${clampedGrade}A1" }

        val newProfile = UserProfile(
            uid = uid,
            email = email,
            displayName = displayName.trim().ifBlank { "Học Sinh Lương Phú" },
            customClassName = validClassName,
            baseGrade = clampedGrade,
            registeredAcademicYear = registeredYear,
            friendCode = current?.friendCode.takeIf { !it.isNullOrBlank() } ?: "LP-${(1000..9999).random()}",
            role = role,
            selectedGrade = clampedGrade,
            level = current?.level ?: 1,
            xp = current?.xp ?: 0,
            highestScore = current?.highestScore ?: 0,
            monthlyScore = if (current?.lastScoreMonthKey == currentMonth) current.monthlyScore else 0,
            lastScoreMonthKey = currentMonth,
            streakDays = current?.streakDays ?: 1,
            status = "Học sinh Trường THPT Lương Phú"
        )

        _currentUser.value = newProfile
        saveLocalProfile(newProfile)

        // Sync to Firestore if authenticated or valid doc
        val docRef = db.collection("users").document(uid)
        val payload = hashMapOf(
            "userId" to uid,
            "email" to email,
            "displayName" to newProfile.displayName,
            "customClassName" to newProfile.customClassName,
            "baseGrade" to newProfile.baseGrade,
            "registeredAcademicYear" to newProfile.registeredAcademicYear,
            "friendCode" to newProfile.friendCode,
            "role" to newProfile.role.name,
            "selectedGrade" to newProfile.selectedGrade,
            "level" to newProfile.level,
            "xp" to newProfile.xp,
            "highestScore" to newProfile.highestScore,
            "monthlyScore" to newProfile.monthlyScore,
            "lastScoreMonthKey" to newProfile.lastScoreMonthKey,
            "updatedAt" to FieldValue.serverTimestamp()
        )
        docRef.set(payload, SetOptions.merge())
            .addOnFailureListener { e ->
                // Will fail gracefully if offline or unauthenticated
                Log.w("Auth", "Local profile saved, remote sync queued: ${e.message}")
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
        val currentMonth = getCurrentMonthKey()

        val newMonthlyScore = if (user.lastScoreMonthKey == currentMonth) {
            user.monthlyScore + pointsEarned
        } else {
            pointsEarned // Reset for the new month!
        }

        val newXp = user.xp + pointsEarned
        val newLevel = 1 + (newXp / 500)
        val newHighScore = maxOf(user.highestScore, pointsEarned)

        val updated = user.copy(
            xp = newXp,
            level = newLevel,
            highestScore = newHighScore,
            monthlyScore = newMonthlyScore,
            lastScoreMonthKey = currentMonth
        )
        _currentUser.value = updated
        saveLocalProfile(updated)

        val docRef = db.collection("users").document(uid)
        docRef.set(
            mapOf(
                "xp" to newXp,
                "level" to newLevel,
                "highestScore" to newHighScore,
                "monthlyScore" to newMonthlyScore,
                "lastScoreMonthKey" to currentMonth,
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
        prefs.edit().clear().apply()
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
