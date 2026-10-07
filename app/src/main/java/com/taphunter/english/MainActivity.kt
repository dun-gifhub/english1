package com.taphunter.english

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.lifecycleScope
import com.taphunter.english.data.models.TeacherAssignment
import com.taphunter.english.data.models.UnitTopic
import com.taphunter.english.data.models.UserRole
import com.taphunter.english.data.repository.AuthRepository
import com.taphunter.english.data.repository.EnglishRepository
import com.taphunter.english.data.repository.FriendsRepository
import com.taphunter.english.ui.components.TeacherPasscodeDialog
import com.taphunter.english.ui.screens.*
import com.taphunter.english.ui.theme.*

sealed class AppScreen {
    data object Home : AppScreen()
    data class TapGame(val unit: UnitTopic) : AppScreen()
    data class TakeAssignment(val assignment: TeacherAssignment) : AppScreen()
    data object Dictionary : AppScreen()
    data object TeacherPortal : AppScreen()
    data object Friends : AppScreen()
    data object CallRoom : AppScreen()
    data object Leaderboard : AppScreen()
}

class MainActivity : ComponentActivity() {

    private lateinit var authRepository: AuthRepository
    private lateinit var englishRepository: EnglishRepository
    private val friendsRepository = FriendsRepository()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        authRepository = AuthRepository(applicationContext)
        englishRepository = EnglishRepository(applicationContext)

        // Attempt silent background sign-in on cold start if previously authorized
        authRepository.attemptAutoSignIn(this, lifecycleScope)

        setContent {
            TapHunterTheme {
                val currentUser by authRepository.currentUser.collectAsState()

                if (currentUser == null) {
                    AuthScreen(authRepository = authRepository)
                } else {
                    MainAppScreen(
                        authRepository = authRepository,
                        englishRepository = englishRepository,
                        friendsRepository = friendsRepository
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainAppScreen(
    authRepository: AuthRepository,
    englishRepository: EnglishRepository,
    friendsRepository: FriendsRepository
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val currentUser by authRepository.currentUser.collectAsState()
    val friends by friendsRepository.friends.collectAsState()
    val activeRoom by friendsRepository.activeRoom.collectAsState()
    val friendMessage by friendsRepository.friendMessage.collectAsState()

    var currentScreen by remember { mutableStateOf<AppScreen>(AppScreen.Home) }
    var showTeacherPasscodeDialog by remember { mutableStateOf(false) }

    val selectedGrade = currentUser?.selectedGrade ?: 10

    // Collect custom words and assignments so subjects update reactively when teacher authors new items
    val customWords by englishRepository.customWords.collectAsState()
    val customGrammar by englishRepository.customGrammar.collectAsState()
    val assignmentsList by englishRepository.assignments.collectAsState()
    val registeredUsers by englishRepository.registeredUsers.collectAsState()

    val subjects = remember(selectedGrade, customWords, customGrammar) {
        englishRepository.getSubjectsForGrade(selectedGrade)
    }
    val grammarLessons = remember(selectedGrade, customGrammar) {
        englishRepository.getGrammarLessonsForGrade(selectedGrade)
    }
    val assignments = remember(selectedGrade, assignmentsList) {
        englishRepository.getAssignmentsForGrade(selectedGrade)
    }
    val monthlyLeaderboard = remember(registeredUsers, currentUser) {
        englishRepository.getLeaderboard(currentUser, isMonthly = true)
    }
    val allTimeLeaderboard = remember(registeredUsers, currentUser) {
        englishRepository.getLeaderboard(currentUser, isMonthly = false)
    }

    // Intercept back button if in a sub screen
    if (currentScreen !is AppScreen.Home) {
        BackHandler {
            currentScreen = AppScreen.Home
        }
    }

    Scaffold(
        topBar = {
            if (currentScreen !is AppScreen.TapGame && currentScreen !is AppScreen.TakeAssignment) {
                TopAppBar(
                    title = {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(Color.White),
                                contentAlignment = Alignment.Center
                            ) {
                                Image(
                                    painter = painterResource(id = R.drawable.ic_app_logo),
                                    contentDescription = "Logo Trường THPT Lương Phú",
                                    modifier = Modifier.size(34.dp),
                                    contentScale = ContentScale.Fit
                                )
                            }
                            Column {
                                Text(
                                    text = "THPT Lương Phú",
                                    color = TextPrimary,
                                    fontWeight = FontWeight.ExtraBold,
                                    fontSize = 16.sp
                                )
                                Text(
                                    text = if (currentUser?.role == UserRole.TEACHER) "Giao diện Giáo Viên" else "${currentUser?.displayName ?: "Học Sinh"} • ${currentUser?.getDisplayClassName() ?: "Lớp $selectedGrade"}",
                                    color = if (currentUser?.role == UserRole.TEACHER) PurpleNeon else CyanAccent,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }
                    },
                    actions = {
                        // Role toggle button requiring giaovien2026 passcode to become TEACHER
                        FilterChip(
                            selected = currentUser?.role == UserRole.TEACHER,
                            onClick = {
                                if (currentUser?.role == UserRole.TEACHER) {
                                    authRepository.setRole(UserRole.STUDENT)
                                    currentScreen = AppScreen.Home
                                } else {
                                    showTeacherPasscodeDialog = true
                                }
                            },
                            label = {
                                Text(
                                    text = if (currentUser?.role == UserRole.TEACHER) "Góc GV" else "Góc HS",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = PurpleNeon,
                                selectedLabelColor = TextPrimary,
                                containerColor = Navy700,
                                labelColor = CyanAccent
                            ),
                            modifier = Modifier.testTag("toggle_role_chip")
                        )

                        Spacer(modifier = Modifier.width(6.dp))

                        // Streak badge
                        Row(
                            modifier = Modifier
                                .clip(RoundedCornerShape(10.dp))
                                .background(Navy700)
                                .padding(horizontal = 8.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.LocalFireDepartment,
                                contentDescription = "Chuỗi ngày",
                                tint = GoldYellow,
                                modifier = Modifier.size(16.dp)
                            )
                            Text(
                                text = "${currentUser?.streakDays ?: 1}d",
                                color = GoldYellow,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Spacer(modifier = Modifier.width(6.dp))

                        // Logout IconButton
                        IconButton(
                            onClick = {
                                val activity = context as? android.app.Activity
                                if (activity != null) {
                                    authRepository.signOut(activity, coroutineScope)
                                }
                            },
                            modifier = Modifier.testTag("auth_logout_button")
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.Logout,
                                contentDescription = "Đăng xuất",
                                tint = SlateBlue
                            )
                        }
                        Spacer(modifier = Modifier.width(4.dp))
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Navy900
                    )
                )
            }
        },
        bottomBar = {
            if (currentScreen !is AppScreen.TapGame && currentScreen !is AppScreen.TakeAssignment) {
                NavigationBar(
                    containerColor = Navy800,
                    contentColor = TextPrimary
                ) {
                    val navItems = listOf(
                        NavTabItem("Học Tập", Icons.Default.MenuBook, AppScreen.Home, "nav_home"),
                        NavTabItem("Tra Từ", Icons.Default.Translate, AppScreen.Dictionary, "nav_dict"),
                        NavTabItem("Giáo Viên", Icons.Default.School, AppScreen.TeacherPortal, "nav_teacher"),
                        NavTabItem("Bạn Bè", Icons.Default.People, AppScreen.Friends, "nav_friends"),
                        NavTabItem("Bảng Vàng", Icons.Default.EmojiEvents, AppScreen.Leaderboard, "nav_leaderboard")
                    )

                    navItems.forEach { item ->
                        val isSelected = when (item.screen) {
                            AppScreen.Home -> currentScreen is AppScreen.Home
                            AppScreen.Dictionary -> currentScreen is AppScreen.Dictionary
                            AppScreen.TeacherPortal -> currentScreen is AppScreen.TeacherPortal
                            AppScreen.Friends -> currentScreen is AppScreen.Friends
                            AppScreen.Leaderboard -> currentScreen is AppScreen.Leaderboard
                            else -> false
                        }

                        NavigationBarItem(
                            selected = isSelected,
                            onClick = {
                                if (item.screen == AppScreen.TeacherPortal && currentUser?.role != UserRole.TEACHER) {
                                    showTeacherPasscodeDialog = true
                                } else {
                                    currentScreen = item.screen
                                }
                            },
                            icon = {
                                Icon(
                                    imageVector = item.icon,
                                    contentDescription = item.label
                                )
                            },
                            label = {
                                Text(
                                    text = item.label,
                                    fontSize = 10.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                )
                            },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = Navy900,
                                selectedTextColor = CyanAccent,
                                indicatorColor = CyanAccent,
                                unselectedIconColor = SlateBlue,
                                unselectedTextColor = SlateBlue
                            ),
                            modifier = Modifier.testTag(item.tag)
                        )
                    }
                }
            }
        },
        containerColor = Navy900
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (val screen = currentScreen) {
                is AppScreen.Home -> {
                    HomeScreen(
                        user = currentUser,
                        selectedGrade = selectedGrade,
                        subjects = subjects,
                        grammarLessons = grammarLessons,
                        assignments = assignments,
                        onSelectGrade = { grade ->
                            authRepository.setSelectedGrade(grade)
                        },
                        onSelectUnitToPlay = { unit ->
                            currentScreen = AppScreen.TapGame(unit)
                        },
                        onSelectAssignmentToTake = { assignment ->
                            currentScreen = AppScreen.TakeAssignment(assignment)
                        },
                        onOpenTeacherPortal = {
                            if (currentUser?.role == UserRole.TEACHER) {
                                currentScreen = AppScreen.TeacherPortal
                            } else {
                                showTeacherPasscodeDialog = true
                            }
                        },
                        onOpenAuth = {
                            val activity = context as? android.app.Activity
                            if (activity != null) {
                                authRepository.signOut(activity, coroutineScope)
                            }
                        }
                    )
                }

                is AppScreen.TapGame -> {
                    TapGameScreen(
                        unit = screen.unit,
                        onGameOver = { score, xp ->
                            authRepository.addXpAndScore(xp)
                        },
                        onBack = { currentScreen = AppScreen.Home }
                    )
                }

                is AppScreen.TakeAssignment -> {
                    AssignmentQuizScreen(
                        assignment = screen.assignment,
                        onFinish = { xpPoints ->
                            authRepository.addXpAndScore(xpPoints)
                        },
                        onBack = { currentScreen = AppScreen.Home }
                    )
                }

                is AppScreen.Dictionary -> {
                    DictionaryScreen(
                        englishRepository = englishRepository,
                        onWordSelectedForPractice = { word ->
                            // Word selected
                        }
                    )
                }

                is AppScreen.TeacherPortal -> {
                    TeacherPortalScreen(
                        englishRepository = englishRepository,
                        teacherName = currentUser?.displayName ?: "Thầy Cô",
                        onSwitchToStudentRole = {
                            authRepository.setRole(UserRole.STUDENT)
                            currentScreen = AppScreen.Home
                        }
                    )
                }

                is AppScreen.Friends -> {
                    FriendsScreen(
                        user = currentUser,
                        friends = friends,
                        activeRoom = activeRoom,
                        statusMessage = friendMessage,
                        onAddFriend = { code ->
                            friendsRepository.addFriendByCode(code)
                        },
                        onCreateRoom = {
                            val hostName = currentUser?.displayName ?: "Thợ Săn HSG"
                            friendsRepository.createRoom(hostName)
                        },
                        onJoinRoom = { code ->
                            val userName = currentUser?.displayName ?: "Thợ Săn HSG"
                            friendsRepository.joinRoom(code, userName)
                        },
                        onOpenCallRoom = {
                            currentScreen = AppScreen.CallRoom
                        },
                        onClearMessage = {
                            friendsRepository.clearMessage()
                        }
                    )
                }

                is AppScreen.CallRoom -> {
                    CallRoomScreen(
                        room = activeRoom,
                        onLeaveRoom = {
                            friendsRepository.leaveRoom()
                            currentScreen = AppScreen.Friends
                        }
                    )
                }

                is AppScreen.Leaderboard -> {
                    LeaderboardScreen(
                        monthlyEntries = monthlyLeaderboard,
                        allTimeEntries = allTimeLeaderboard,
                        currentUser = currentUser
                    )
                }
            }
        }
    }

    if (showTeacherPasscodeDialog) {
        TeacherPasscodeDialog(
            onDismiss = { showTeacherPasscodeDialog = false },
            onSuccess = {
                authRepository.setRole(UserRole.TEACHER)
                showTeacherPasscodeDialog = false
                currentScreen = AppScreen.TeacherPortal
            }
        )
    }
}

data class NavTabItem(
    val label: String,
    val icon: ImageVector,
    val screen: AppScreen,
    val tag: String
)
