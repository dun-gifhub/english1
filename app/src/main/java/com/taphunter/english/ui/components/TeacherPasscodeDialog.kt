package com.taphunter.english.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.taphunter.english.ui.theme.*

@Composable
fun TeacherPasscodeDialog(
    onDismiss: () -> Unit,
    onSuccess: () -> Unit
) {
    var passcodeInput by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var isPasswordVisible by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            Button(
                onClick = {
                    if (passcodeInput.trim() == "giaovien2026") {
                        onSuccess()
                    } else {
                        errorMessage = "Mã xác thực không chính xác! Vui lòng nhập đúng mã giáo viên."
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = PurpleNeon),
                modifier = Modifier.testTag("submit_teacher_passcode_button")
            ) {
                Text("Xác Nhận Quyền GV", color = TextPrimary, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Hủy", color = SlateBlue)
            }
        },
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(Icons.Default.Lock, contentDescription = null, tint = PurpleNeon)
                Text(
                    text = "Xác Thực Quyền Giáo Viên",
                    color = TextPrimary,
                    fontWeight = FontWeight.Bold,
                    fontSize = 17.sp
                )
            }
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(
                    text = "Để chuyển sang nhánh Giáo viên (ra đề, soạn từ mới và ngữ pháp), bạn cần nhập mã bảo mật được cấp.",
                    color = SlateBlue,
                    fontSize = 13.sp,
                    lineHeight = 18.sp
                )

                OutlinedTextField(
                    value = passcodeInput,
                    onValueChange = {
                        passcodeInput = it
                        errorMessage = null
                    },
                    label = { Text("Mã xác thực giáo viên") },
                    placeholder = { Text("Nhập mã bí mật...") },
                    singleLine = true,
                    visualTransformation = if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    trailingIcon = {
                        IconButton(onClick = { isPasswordVisible = !isPasswordVisible }) {
                            Icon(
                                imageVector = if (isPasswordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                contentDescription = "Hiện/Ẩn mã",
                                tint = SlateBlue
                            )
                        }
                    },
                    isError = errorMessage != null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("teacher_passcode_field"),
                    shape = RoundedCornerShape(12.dp)
                )

                if (errorMessage != null) {
                    Text(
                        text = errorMessage ?: "",
                        color = RedDanger,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        },
        containerColor = DarkCard
    )
}
