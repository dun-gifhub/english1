package com.taphunter.english.ui.screens

import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.taphunter.english.data.models.CallRoom
import com.taphunter.english.data.models.Friend
import com.taphunter.english.ui.theme.*

@Composable
fun CallRoomScreen(
    room: CallRoom?,
    onLeaveRoom: () -> Unit
) {
    BackHandler { onLeaveRoom() }

    val context = LocalContext.current
    var isMicOn by remember { mutableStateOf(true) }
    var isSpeakerOn by remember { mutableStateOf(true) }
    var isCameraOn by remember { mutableStateOf(false) }

    // Sample study vocabulary words in room
    val vocabDiscussionList = remember {
        listOf(
            Triple("Ubiquitous", "/juːˈbɪk.wə.təs/", "Có mặt ở khắp nơi, phổ biến rộng rãi"),
            Triple("Resilience", "/rɪˈzɪl.jəns/", "Khả năng phục hồi, sự kiên cường trước thử thách"),
            Triple("Ephemeral", "/ɪˈfem.ər.əl/", "Phù du, chóng tàn, tồn tại trong chốc lát"),
            Triple("Scrutinize", "/ˈskruː.tɪ.naɪz/", "Kiểm tra kỹ lưỡng, soi xét cẩn thận")
        )
    }
    var currentVocabIndex by remember { mutableStateOf(0) }
    val currentVocab = vocabDiscussionList[currentVocabIndex]

    if (room == null) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Navy900)
                .padding(24.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.MeetingRoom,
                    contentDescription = null,
                    tint = SlateBlue,
                    modifier = Modifier.size(64.dp)
                )
                Text(
                    text = "Chưa có phòng học nào đang mở",
                    color = TextPrimary,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Hãy tạo hoặc nhập mã phòng trong tab Bạn Bè để tham gia phòng học trực tuyến.",
                    color = SlateBlue,
                    fontSize = 13.sp,
                    textAlign = TextAlign.Center
                )
                Button(
                    onClick = onLeaveRoom,
                    colors = ButtonDefaults.buttonColors(containerColor = CyanAccent),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Quay Lại", color = Navy900, fontWeight = FontWeight.Bold)
                }
            }
        }
        return
    }

    // Audio speaking pulsing animation
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.15f,
        animationSpec = infiniteRepeatable(
            animation = tween(600, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scale"
    )

    Scaffold(
        topBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Navy900)
                    .padding(horizontal = 16.dp, vertical = 10.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(10.dp)
                                .clip(CircleShape)
                                .background(GreenSuccess)
                        )
                        Column {
                            Text(
                                text = room.title,
                                color = TextPrimary,
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp
                            )
                            Text(
                                text = "Mã phòng: #${room.roomCode} • Chủ phòng: ${room.hostName}",
                                color = CyanAccent,
                                fontSize = 12.sp
                            )
                        }
                    }

                    Button(
                        onClick = onLeaveRoom,
                        colors = ButtonDefaults.buttonColors(containerColor = RedDanger),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.testTag("leave_room_button")
                    ) {
                        Icon(imageVector = Icons.Default.CallEnd, contentDescription = null, tint = TextPrimary, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Rời Phòng", color = TextPrimary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        },
        bottomBar = {
            // Call Controls Toolbar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Navy800)
                    .padding(vertical = 12.dp, horizontal = 24.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Mic Button
                FilledIconButton(
                    onClick = {
                        isMicOn = !isMicOn
                        Toast.makeText(context, if (isMicOn) "Đã bật micro" else "Đã tắt micro", Toast.LENGTH_SHORT).show()
                    },
                    colors = IconButtonDefaults.filledIconButtonColors(
                        containerColor = if (isMicOn) CyanAccent else RedDanger
                    ),
                    modifier = Modifier.size(52.dp).testTag("toggle_mic_button")
                ) {
                    Icon(
                        imageVector = if (isMicOn) Icons.Default.Mic else Icons.Default.MicOff,
                        contentDescription = "Micro",
                        tint = if (isMicOn) Navy900 else TextPrimary
                    )
                }

                // Camera Button
                FilledIconButton(
                    onClick = {
                        isCameraOn = !isCameraOn
                        Toast.makeText(context, if (isCameraOn) "Đã bật camera" else "Đã tắt camera", Toast.LENGTH_SHORT).show()
                    },
                    colors = IconButtonDefaults.filledIconButtonColors(
                        containerColor = if (isCameraOn) CyanAccent else Navy700
                    ),
                    modifier = Modifier.size(52.dp).testTag("toggle_camera_button")
                ) {
                    Icon(
                        imageVector = if (isCameraOn) Icons.Default.Videocam else Icons.Default.VideocamOff,
                        contentDescription = "Camera",
                        tint = if (isCameraOn) Navy900 else TextPrimary
                    )
                }

                // Speaker Button
                FilledIconButton(
                    onClick = {
                        isSpeakerOn = !isSpeakerOn
                        Toast.makeText(context, if (isSpeakerOn) "Đã bật loa ngoài" else "Đã tắt loa", Toast.LENGTH_SHORT).show()
                    },
                    colors = IconButtonDefaults.filledIconButtonColors(
                        containerColor = if (isSpeakerOn) GoldYellow else Navy700
                    ),
                    modifier = Modifier.size(52.dp).testTag("toggle_speaker_button")
                ) {
                    Icon(
                        imageVector = if (isSpeakerOn) Icons.Default.VolumeUp else Icons.Default.VolumeOff,
                        contentDescription = "Loa",
                        tint = if (isSpeakerOn) Navy900 else TextPrimary
                    )
                }
            }
        },
        containerColor = Navy900
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Participant Avatars Grid (WebRTC Call Simulator)
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = DarkCard)
            ) {
                Column(
                    modifier = Modifier.padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Thành viên trong phòng (${room.participants.size})",
                            color = TextPrimary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                        Text(
                            text = if (isCameraOn) "Video HD Đang bật" else "Chỉ thoại âm thanh",
                            color = CyanAccent,
                            fontSize = 12.sp
                        )
                    }

                    LazyVerticalGrid(
                        columns = GridCells.Fixed(3),
                        modifier = Modifier.fillMaxWidth().height(160.dp),
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        items(room.participants) { participant ->
                            val isSpeaking = participant.uid == "f1" || (participant.uid == "me" && isMicOn)

                            Card(
                                shape = RoundedCornerShape(14.dp),
                                colors = CardDefaults.cardColors(containerColor = Navy800),
                                border = if (isSpeaking) {
                                    CardDefaults.outlinedCardBorder().copy(brush = Brush.horizontalGradient(listOf(CyanAccent, GreenSuccess)))
                                } else null
                            ) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(10.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.Center
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        if (isSpeaking) {
                                            Box(
                                                modifier = Modifier
                                                    .size(44.dp)
                                                    .scale(pulseScale)
                                                    .clip(CircleShape)
                                                    .background(CyanAccent.copy(alpha = 0.25f))
                                            )
                                        }
                                        Box(
                                            modifier = Modifier
                                                .size(38.dp)
                                                .clip(CircleShape)
                                                .background(Color(participant.avatarColor)),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(
                                                text = participant.displayName.take(1).uppercase(),
                                                color = Navy900,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 16.sp
                                            )
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = participant.displayName.take(8),
                                        color = TextPrimary,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        maxLines = 1
                                    )
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(2.dp)
                                    ) {
                                        Icon(
                                            imageVector = if (isSpeaking) Icons.Default.GraphicEq else Icons.Default.MicOff,
                                            contentDescription = null,
                                            tint = if (isSpeaking) GreenSuccess else SlateBlue,
                                            modifier = Modifier.size(12.dp)
                                        )
                                        Text(
                                            text = if (isSpeaking) "Nói..." else "Tắt mic",
                                            color = if (isSpeaking) GreenSuccess else SlateBlue,
                                            fontSize = 9.sp
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Shared Study Whiteboard (Bảng học từ vựng chung trong phòng)
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .testTag("shared_vocab_board"),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Navy800),
                border = CardDefaults.outlinedCardBorder().copy(brush = Brush.verticalGradient(listOf(DarkBorder, CyanAccent.copy(alpha = 0.5f))))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(18.dp),
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(imageVector = Icons.Default.Psychology, contentDescription = null, tint = GoldYellow)
                                Text(
                                    text = "BẢNG TỪ VỰNG CHUNG",
                                    color = GoldYellow,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 1.sp
                                )
                            }
                            Text(
                                text = "Từ ${currentVocabIndex + 1}/${vocabDiscussionList.size}",
                                color = SlateBlue,
                                fontSize = 12.sp
                            )
                        }

                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = DarkSurface)
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Text(
                                    text = currentVocab.first,
                                    color = CyanAccent,
                                    fontSize = 26.sp,
                                    fontWeight = FontWeight.ExtraBold
                                )
                                Text(
                                    text = currentVocab.second,
                                    color = SlateBlue,
                                    fontSize = 14.sp
                                )
                                Divider(
                                    modifier = Modifier.padding(vertical = 6.dp),
                                    color = DarkBorder
                                )
                                Text(
                                    text = currentVocab.third,
                                    color = TextPrimary,
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    }

                    // Action buttons on whiteboard
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedButton(
                            onClick = {
                                Toast.makeText(context, "Đã phát âm mẫu từ ${currentVocab.first}", Toast.LENGTH_SHORT).show()
                            },
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Icon(imageVector = Icons.Default.VolumeUp, contentDescription = null, tint = CyanAccent, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Phát Âm Mẫu", color = CyanAccent, fontSize = 12.sp)
                        }

                        Button(
                            onClick = {
                                currentVocabIndex = (currentVocabIndex + 1) % vocabDiscussionList.size
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = CyanAccent),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.testTag("next_shared_vocab_button")
                        ) {
                            Text("Từ Tiếp Theo", color = Navy900, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            Spacer(modifier = Modifier.width(4.dp))
                            Icon(imageVector = Icons.Default.ChevronRight, contentDescription = null, tint = Navy900, modifier = Modifier.size(16.dp))
                        }
                    }
                }
            }
        }
    }
}
