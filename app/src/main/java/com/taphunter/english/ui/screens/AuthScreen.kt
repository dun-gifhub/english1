package com.taphunter.english.ui.screens

import android.app.Activity
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(Navy900, Navy800, Color(0xFF0F2027))
                )
            )
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .testTag("auth_card"),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = DarkCard),
            border = CardDefaults.outlinedCardBorder().copy(brush = Brush.horizontalGradient(listOf(CyanAccent, PurpleNeon)))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Logo Icon
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.linearGradient(listOf(CyanAccent, PurpleNeon))
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Bolt,
                        contentDescription = "Logo",
                        tint = Navy900,
                        modifier = Modifier.size(38.dp)
                    )
                }

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "Tap Hunter English",
                        color = TextPrimary,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.ExtraBold,
                        letterSpacing = 0.5.sp
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Đấu Phản Xạ & Hệ Thống Học Tiếng Anh Lớp 6 - 12",
                        color = SlateBlue,
                        fontSize = 13.sp,
                        textAlign = TextAlign.Center
                    )
                }

                HorizontalDivider(color = DarkBorder, modifier = Modifier.padding(vertical = 4.dp))

                // Feature Highlights
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    FeatureRow(
                        icon = Icons.Default.CloudSync,
                        title = "Đồng Bộ Đa Thiết Bị",
                        desc = "Dữ liệu điểm số, XP, cấp độ lưu trên Cloud Firestore"
                    )
                    FeatureRow(
                        icon = Icons.Default.School,
                        title = "Phân Nhánh Học Sinh & Giáo Viên",
                        desc = "Thầy cô ra đề, thêm từ mới & ngữ pháp (mã: giaovien2026)"
                    )
                    FeatureRow(
                        icon = Icons.Default.Translate,
                        title = "Tra Từ Điển Anh - Việt & Việt - Anh",
                        desc = "Tra cứu hai chiều mọi từ vựng trong chương trình GDPT"
                    )
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

                Spacer(modifier = Modifier.height(6.dp))

                // Google Sign-In Button (Jetpack Credential Manager)
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
                        .height(52.dp)
                        .testTag("google_sign_in_button"),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = CyanAccent
                    ),
                    shape = RoundedCornerShape(14.dp)
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
                                fontSize = 15.sp
                            )
                        }
                    }
                }

                Text(
                    text = "Đăng nhập an toàn qua Google Sign-In & Firebase Auth để đồng bộ trên mọi thiết bị.",
                    color = SlateBlue,
                    fontSize = 11.sp,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Composable
private fun FeatureRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    desc: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(34.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(Navy800),
            contentAlignment = Alignment.Center
        ) {
            Icon(imageVector = icon, contentDescription = null, tint = CyanAccent, modifier = Modifier.size(18.dp))
        }
        Column {
            Text(title, color = TextPrimary, fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
            Text(desc, color = SlateBlue, fontSize = 11.sp)
        }
    }
}
