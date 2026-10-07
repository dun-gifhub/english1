package com.taphunter.english.ui.screens

import android.app.Activity
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.taphunter.english.R
import com.taphunter.english.data.models.UserRole
import com.taphunter.english.data.repository.AuthRepository
import com.taphunter.english.ui.theme.*

@Composable
fun AuthScreen(
    authRepository: AuthRepository
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val isAuthenticating by authRepository.isAuthenticating.collectAsState()
    val authError by authRepository.authError.collectAsState()

    var isRegisterMode by remember { mutableStateOf(true) }
    var accountName by remember { mutableStateOf("") }
    var className by remember { mutableStateOf("10A1") }
    var selectedGrade by remember { mutableIntStateOf(10) }
    var academicYear by remember { mutableIntStateOf(2026) }
    var formError by remember { mutableStateOf<String?>(null) }

    val currentYear = remember { java.util.Calendar.getInstance().get(java.util.Calendar.YEAR) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(Navy900, Color(0xFF0F2B1D), Navy900)
                )
            )
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .widthIn(max = 520.dp)
                .testTag("auth_card"),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = DarkCard),
            border = CardDefaults.outlinedCardBorder().copy(
                brush = Brush.horizontalGradient(listOf(Color(0xFF2E7D32), CyanAccent))
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // School Logo
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .clip(CircleShape)
                        .border(2.dp, Color(0xFFFFD700), CircleShape)
                        .background(Color.White),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.ic_app_logo),
                        contentDescription = "Logo Trường THPT Lương Phú",
                        modifier = Modifier.size(76.dp),
                        contentScale = ContentScale.Fit
                    )
                }

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "TRƯỜNG THPT LƯƠNG PHÚ",
                        color = GoldYellow,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.ExtraBold,
                        letterSpacing = 1.sp
                    )
                    Text(
                        text = "Tap Hunter English",
                        color = TextPrimary,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Black
                    )
                    Text(
                        text = "Hệ Thống Đấu Từ Phản Xạ & Luyện Thi Tiếng Anh",
                        color = SlateBlue,
                        fontSize = 12.sp,
                        textAlign = TextAlign.Center
                    )
                }

                // Tab Switcher: Đăng Ký vs Đăng Nhập
                TabRow(
                    selectedTabIndex = if (isRegisterMode) 0 else 1,
                    containerColor = Navy800,
                    contentColor = CyanAccent,
                    modifier = Modifier.clip(RoundedCornerShape(12.dp))
                ) {
                    Tab(
                        selected = isRegisterMode,
                        onClick = { isRegisterMode = true },
                        text = {
                            Text(
                                "Đăng Ký Tài Khoản",
                                fontWeight = if (isRegisterMode) FontWeight.Bold else FontWeight.Normal,
                                color = if (isRegisterMode) CyanAccent else SlateBlue,
                                fontSize = 13.sp
                            )
                        }
                    )
                    Tab(
                        selected = !isRegisterMode,
                        onClick = { isRegisterMode = false },
                        text = {
                            Text(
                                "Đăng Nhập Nhanh",
                                fontWeight = if (!isRegisterMode) FontWeight.Bold else FontWeight.Normal,
                                color = if (!isRegisterMode) CyanAccent else SlateBlue,
                                fontSize = 13.sp
                            )
                        }
                    )
                }

                if (isRegisterMode) {
                    // Registration Form
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        // Account Name
                        OutlinedTextField(
                            value = accountName,
                            onValueChange = {
                                accountName = it
                                formError = null
                            },
                            label = { Text("Tên tài khoản / Họ tên học sinh *") },
                            placeholder = { Text("Ví dụ: Nguyễn Văn An, Hunter_LP...") },
                            leadingIcon = {
                                Icon(Icons.Default.Person, contentDescription = null, tint = CyanAccent)
                            },
                            singleLine = true,
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("register_name_field"),
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = CyanAccent,
                                unfocusedBorderColor = DarkBorder,
                                focusedContainerColor = DarkSurface,
                                unfocusedContainerColor = DarkSurface
                            )
                        )

                        // Class Name
                        OutlinedTextField(
                            value = className,
                            onValueChange = {
                                className = it
                                formError = null
                            },
                            label = { Text("Tên lớp học * (VD: 10A1, 11A3, 12A2)") },
                            placeholder = { Text("Ví dụ: 10A1") },
                            leadingIcon = {
                                Icon(Icons.Default.School, contentDescription = null, tint = GoldYellow)
                            },
                            singleLine = true,
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("register_class_field"),
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = GoldYellow,
                                unfocusedBorderColor = DarkBorder,
                                focusedContainerColor = DarkSurface,
                                unfocusedContainerColor = DarkSurface
                            )
                        )

                        // Grade Selection
                        Text(
                            text = "Khối lớp ban đầu:",
                            color = TextPrimary,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            (6..12).forEach { grade ->
                                val isSelected = selectedGrade == grade
                                FilterChip(
                                    selected = isSelected,
                                    onClick = {
                                        selectedGrade = grade
                                        if (className.matches(Regex("^\\d+.*"))) {
                                            className = className.replaceFirst(Regex("^\\d+"), grade.toString())
                                        }
                                    },
                                    label = {
                                        Text(
                                            "K$grade",
                                            fontSize = 11.sp,
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                        )
                                    },
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = if (grade >= 10) Color(0xFF2E7D32) else CyanAccent,
                                        selectedLabelColor = if (grade >= 10) TextPrimary else Navy900
                                    ),
                                    modifier = Modifier.weight(1f)
                                )
                            }
                        }

                        // Auto-increment Info Notice
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = Navy800),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(10.dp),
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.AutoMode,
                                    contentDescription = null,
                                    tint = CyanAccent,
                                    modifier = Modifier.size(20.dp)
                                )
                                Text(
                                    text = "⚡ Niên khóa đăng ký: $academicYear - Khối lớp sẽ tự động tăng theo năm học mới (vào tháng 9 hàng năm). Ví dụ: Lớp 10A1 sẽ tự động lên 11A1, 12A1!",
                                    color = SlateBlue,
                                    fontSize = 11.sp,
                                    lineHeight = 15.sp
                                )
                            }
                        }

                        if (formError != null) {
                            Text(
                                text = formError ?: "",
                                color = RedDanger,
                                fontSize = 12.sp,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }

                        // Submit Button
                        Button(
                            onClick = {
                                if (accountName.trim().isBlank()) {
                                    formError = "Vui lòng nhập tên tài khoản của bạn!"
                                    return@Button
                                }
                                authRepository.registerOrUpdateAccount(
                                    displayName = accountName,
                                    customClassName = className,
                                    baseGrade = selectedGrade,
                                    registeredYear = academicYear,
                                    role = UserRole.STUDENT
                                )
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp)
                                .testTag("create_account_button"),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E7D32)),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(Icons.Default.CheckCircle, contentDescription = null, tint = TextPrimary)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                "Tạo Tài Khoản & Bắt Đầu",
                                color = TextPrimary,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                        }
                    }
                } else {
                    // Login Mode
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            text = "Đăng nhập để lưu tiến độ và đồng bộ bảng xếp hạng THPT Lương Phú trên mọi thiết bị.",
                            color = SlateBlue,
                            fontSize = 12.sp,
                            textAlign = TextAlign.Center
                        )

                        // Google Sign-In Button
                        Button(
                            onClick = {
                                val activity = context as? Activity
                                if (activity != null) {
                                    authRepository.signInWithGoogle(activity, coroutineScope)
                                }
                            },
                            enabled = !isAuthenticating,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp)
                                .testTag("google_sign_in_button"),
                            colors = ButtonDefaults.buttonColors(containerColor = CyanAccent),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            if (isAuthenticating) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(24.dp),
                                    color = Navy900,
                                    strokeWidth = 2.5.dp
                                )
                            } else {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.AccountCircle,
                                        contentDescription = null,
                                        tint = Navy900,
                                        modifier = Modifier.size(22.dp)
                                    )
                                    Text(
                                        text = "Đăng Nhập Bằng Google",
                                        color = Navy900,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.sp
                                    )
                                }
                            }
                        }

                        // Quick Guest / Local Access Button
                        OutlinedButton(
                            onClick = {
                                isRegisterMode = true
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(46.dp),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text("Chưa có tài khoản? Đăng ký ngay", color = TextPrimary, fontSize = 13.sp)
                        }
                    }
                }

                if (authError != null) {
                    Text(
                        text = authError ?: "",
                        color = RedDanger,
                        fontSize = 12.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    }
}

