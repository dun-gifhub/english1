package com.taphunter.english.ui.screens

import android.speech.tts.TextToSpeech
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.VolumeUp
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
import com.taphunter.english.ui.theme.*
import java.util.Locale

@Composable
fun CallRoomScreen(
    room: CallRoom?,
    onLeaveRoom: () -> Unit
) {
    BackHandler { onLeaveRoom() }

    val context = LocalContext.current

    // Real TextToSpeech Engine for actual room voice & audio testing
    var tts: TextToSpeech? by remember { mutableStateOf(null) }
    var isTtsReady by remember { mutableStateOf(false) }

    DisposableEffect(Unit) {
        val ttsInstance = TextToSpeech(context) { status ->
            if (status == TextToSpeech.SUCCESS) {
                tts?.language = Locale.US
                isTtsReady = true
            }
        }
        tts = ttsInstance
        onDispose {
            ttsInstance.stop()
            ttsInstance.shutdown()
        }
    }

    fun playVoiceAudio(text: String, isEnglish: Boolean = true) {
        if (!isTtsReady || text.isBlank()) {
            Toast.makeText(context, "Âm thanh: $text", Toast.LENGTH_SHORT).show()
            return
        }
        tts?.language = if (isEnglish) Locale.US else Locale("vi", "VN")
        tts?.speak(text, TextToSpeech.QUEUE_FLUSH, null, "call_room_tts")
    }

    var isMicOn by remember { mutableStateOf(true) }
    var isSpeakerOn by remember { mutableStateOf(true) }
    var isCameraOn by remember { mutableStateOf(false) }
    var isScreenSharing by remember { mutableStateOf(false) }

    // Screen sharing presentation slides
    val presentationSlides = remember {
        listOf(
            Triple(
                "Đề Thi Trọng Tâm THPTQG",
                "Question 1: If we ______ sustainable energy sooner, air pollution wouldn't be this severe.",
                "Options: A. had adopted (Đúng) | B. adopted | C. adopt | D. will adopt\n\nGiải thích: Câu điều kiện hỗn hợp (Mixed Conditional)."
            ),
            Triple(
                "Bảng Từ Vựng Chuyên Đề: Sống Xanh",
                "1. Biodiversity /ˌbaɪ.əʊ.daɪˈvɜː.sə.ti/: Đa dạng sinh học\n2. Catastrophic /ˌkæt.əˈstrɒf.ɪk/: Thảm khốc\n3. Infrastructure /ˈɪn.frəˌstrʌk.tʃər/: Cơ sở hạ tầng",
                "Nhiệm vụ phòng: Từng thành viên bật micro đặt 1 câu ví dụ có chứa từ trên!"
            ),
            Triple(
                "Sơ Đồ Đảo Ngữ Nâng Cao HSG",
                "Cấu trúc: Hardly / Scarcely + had + S + P2... when + S + V(quá khứ)",
                "Ví dụ: Hardly had the bell rung when the teacher entered the classroom."
            )
        )
    }
    var currentSlideIndex by remember { mutableIntStateOf(0) }

    // Study vocabulary in room
    val vocabDiscussionList = remember {
        listOf(
            Triple("Ubiquitous", "/juːˈbɪk.wə.təs/", "Có mặt ở khắp nơi, phổ biến rộng rãi"),
            Triple("Resilience", "/rɪˈzɪl.jəns/", "Khả năng phục hồi, sự kiên cường trước thử thách"),
            Triple("Ephemeral", "/ɪˈfem.ər.əl/", "Phù du, chóng tàn, tồn tại trong chốc lát"),
            Triple("Scrutinize", "/ˈskruː.tɪ.naɪz/", "Kiểm tra kỹ lưỡng, soi xét cẩn thận")
        )
    }
    var currentVocabIndex by remember { mutableIntStateOf(0) }
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
        targetValue = 1.18f,
        animationSpec = infiniteRepeatable(
            animation = tween(500, easing = FastOutSlowInEasing),
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
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                Text(
                                    text = "Mã phòng: #${room.roomCode} • Chủ phòng: ${room.hostName}",
                                    color = CyanAccent,
                                    fontSize = 11.sp
                                )
                                Text(
                                    text = "• Thoại Duplex Online",
                                    color = GreenSuccess,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
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
            // Call Controls Toolbar with Real Audio, Screen Share, and Mic
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Navy800)
                    .padding(vertical = 10.dp, horizontal = 12.dp),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // 1. Mic Button
                FilledIconButton(
                    onClick = {
                        isMicOn = !isMicOn
                        if (isMicOn) {
                            playVoiceAudio("Micro đã được kích hoạt, bạn có thể nói.", isEnglish = false)
                        } else {
                            Toast.makeText(context, "Đã tắt micro", Toast.LENGTH_SHORT).show()
                        }
                    },
                    colors = IconButtonDefaults.filledIconButtonColors(
                        containerColor = if (isMicOn) CyanAccent else RedDanger
                    ),
                    modifier = Modifier.size(48.dp).testTag("toggle_mic_button")
                ) {
                    Icon(
                        imageVector = if (isMicOn) Icons.Default.Mic else Icons.Default.MicOff,
                        contentDescription = "Micro",
                        tint = if (isMicOn) Navy900 else TextPrimary
                    )
                }

                // 2. Screen Share Button (TRÌNH CHIẾU MÀN HÌNH)
                FilledIconButton(
                    onClick = {
                        isScreenSharing = !isScreenSharing
                        val msg = if (isScreenSharing) "Đã bật trình chiếu màn hình cho cả phòng!" else "Đã dừng trình chiếu màn hình"
                        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                        playVoiceAudio(if (isScreenSharing) "Bắt đầu trình chiếu màn hình bài giảng." else "Đã dừng trình chiếu màn hình.", isEnglish = false)
                    },
                    colors = IconButtonDefaults.filledIconButtonColors(
                        containerColor = if (isScreenSharing) GoldYellow else Navy700
                    ),
                    modifier = Modifier.size(48.dp).testTag("toggle_screen_share_button")
                ) {
                    Icon(
                        imageVector = if (isScreenSharing) Icons.Default.StopScreenShare else Icons.Default.ScreenShare,
                        contentDescription = "Trình chiếu màn hình",
                        tint = if (isScreenSharing) Navy900 else TextPrimary
                    )
                }

                // 3. Audio Test Speaker Button (THỬ ÂM THANH PHÒNG)
                FilledIconButton(
                    onClick = {
                        playVoiceAudio("Kiểm tra âm thanh phòng học: Loa và micro đang hoạt động rất tốt! Chúc bạn học tiếng Anh hiệu quả.", isEnglish = false)
                        Toast.makeText(context, "🔊 Đang phát âm thanh kiểm tra loa...", Toast.LENGTH_SHORT).show()
                    },
                    colors = IconButtonDefaults.filledIconButtonColors(
                        containerColor = PurpleNeon
                    ),
                    modifier = Modifier.size(48.dp).testTag("test_audio_button")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.VolumeUp,
                        contentDescription = "Thử âm thanh phòng",
                        tint = TextPrimary
                    )
                }

                // 4. Speaker Toggle Button
                FilledIconButton(
                    onClick = {
                        isSpeakerOn = !isSpeakerOn
                        Toast.makeText(context, if (isSpeakerOn) "Đã bật loa ngoài" else "Đã tắt loa ngoài", Toast.LENGTH_SHORT).show()
                    },
                    colors = IconButtonDefaults.filledIconButtonColors(
                        containerColor = if (isSpeakerOn) GreenSuccess else Navy700
                    ),
                    modifier = Modifier.size(48.dp).testTag("toggle_speaker_button")
                ) {
                    Icon(
                        imageVector = if (isSpeakerOn) Icons.Default.Hearing else Icons.Default.HearingDisabled,
                        contentDescription = "Loa",
                        tint = if (isSpeakerOn) Navy900 else TextPrimary
                    )
                }

                // 5. Camera Button
                FilledIconButton(
                    onClick = {
                        isCameraOn = !isCameraOn
                        Toast.makeText(context, if (isCameraOn) "Đã bật camera HD" else "Đã tắt camera", Toast.LENGTH_SHORT).show()
                    },
                    colors = IconButtonDefaults.filledIconButtonColors(
                        containerColor = if (isCameraOn) CyanAccent else Navy700
                    ),
                    modifier = Modifier.size(48.dp).testTag("toggle_camera_button")
                ) {
                    Icon(
                        imageVector = if (isCameraOn) Icons.Default.Videocam else Icons.Default.VideocamOff,
                        contentDescription = "Camera",
                        tint = if (isCameraOn) Navy900 else TextPrimary
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
                .padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Audio Sound Wave Status Bar (Khi mic bật)
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = if (isMicOn) Color(0xFF0E3023) else Navy800),
                border = if (isMicOn) CardDefaults.outlinedCardBorder().copy(brush = Brush.horizontalGradient(listOf(GreenSuccess, CyanAccent))) else null
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 14.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Icon(
                            imageVector = if (isMicOn) Icons.Default.GraphicEq else Icons.Default.MicOff,
                            contentDescription = null,
                            tint = if (isMicOn) GreenSuccess else SlateBlue,
                            modifier = Modifier.size(20.dp)
                        )
                        Text(
                            text = if (isMicOn) "Âm thanh thoại: Đang thu âm & phát trực tiếp" else "Micro đang tắt (Bấm biểu tượng Micro bên dưới để bật)",
                            color = if (isMicOn) TextPrimary else SlateBlue,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }

                    if (isMicOn) {
                        // Visualizer equalizer bars
                        Row(horizontalArrangement = Arrangement.spacedBy(3.dp), verticalAlignment = Alignment.CenterVertically) {
                            listOf(0.8f, 1.4f, 1.0f, 1.6f, 0.9f).forEach { hFactor ->
                                Box(
                                    modifier = Modifier
                                        .width(3.dp)
                                        .height((14 * hFactor * pulseScale).dp)
                                        .clip(RoundedCornerShape(2.dp))
                                        .background(GreenSuccess)
                                )
                            }
                        }
                    }
                }
            }

            // Participants Grid
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = DarkCard)
            ) {
                Column(
                    modifier = Modifier.padding(12.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
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
                            fontSize = 13.sp
                        )
                        Text(
                            text = if (isScreenSharing) "📺 Đang trình chiếu màn hình" else if (isCameraOn) "Video HD" else "Thoại giọng nói",
                            color = if (isScreenSharing) GoldYellow else CyanAccent,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    LazyVerticalGrid(
                        columns = GridCells.Fixed(3),
                        modifier = Modifier.fillMaxWidth().height(115.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(room.participants) { participant ->
                            val isSpeaking = participant.uid == "host" && isMicOn

                            Card(
                                shape = RoundedCornerShape(12.dp),
                                colors = CardDefaults.cardColors(containerColor = Navy800),
                                border = if (isSpeaking) {
                                    CardDefaults.outlinedCardBorder().copy(brush = Brush.horizontalGradient(listOf(CyanAccent, GreenSuccess)))
                                } else null
                            ) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(6.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.Center
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        if (isSpeaking) {
                                            Box(
                                                modifier = Modifier
                                                    .size(38.dp)
                                                    .scale(pulseScale)
                                                    .clip(CircleShape)
                                                    .background(GreenSuccess.copy(alpha = 0.3f))
                                            )
                                        }
                                        Box(
                                            modifier = Modifier
                                                .size(32.dp)
                                                .clip(CircleShape)
                                                .background(Color(participant.avatarColor)),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(
                                                text = participant.displayName.take(1).uppercase(),
                                                color = Navy900,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 14.sp
                                            )
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = participant.displayName.take(8),
                                        color = TextPrimary,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        maxLines = 1
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // MODE A: SCREEN SHARE PRESENTATION (KHI BẬT TRÌNH CHIẾU MÀN HÌNH)
            if (isScreenSharing) {
                val slide = presentationSlides[currentSlideIndex]

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .testTag("screen_share_presentation_board"),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = Navy800),
                    border = CardDefaults.outlinedCardBorder().copy(brush = Brush.horizontalGradient(listOf(GoldYellow, PurpleNeon)))
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(14.dp),
                        verticalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                    Icon(Icons.Default.ScreenShare, contentDescription = null, tint = GoldYellow, modifier = Modifier.size(18.dp))
                                    Text(
                                        text = "TRÌNH CHIẾU SLIDE PHÒNG HỌC",
                                        color = GoldYellow,
                                        fontWeight = FontWeight.ExtraBold,
                                        fontSize = 12.sp,
                                        letterSpacing = 1.sp
                                    )
                                }
                                Text(
                                    text = "Slide ${currentSlideIndex + 1}/${presentationSlides.size}",
                                    color = CyanAccent,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(14.dp),
                                colors = CardDefaults.cardColors(containerColor = DarkSurface)
                            ) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(14.dp),
                                    verticalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Text(
                                        text = slide.first,
                                        color = GoldYellow,
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = slide.second,
                                        color = TextPrimary,
                                        fontSize = 14.sp,
                                        lineHeight = 20.sp,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                    HorizontalDivider(color = DarkBorder)
                                    Text(
                                        text = slide.third,
                                        color = SlateBlue,
                                        fontSize = 12.sp,
                                        lineHeight = 16.sp
                                    )
                                }
                            }
                        }

                        // Slide Navigation & Voice Controls
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            OutlinedButton(
                                onClick = {
                                    playVoiceAudio("${slide.first}. ${slide.second}", isEnglish = currentSlideIndex != 1)
                                },
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Icon(Icons.AutoMirrored.Filled.VolumeUp, contentDescription = null, tint = CyanAccent, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Đọc Slide Này", color = CyanAccent, fontSize = 11.sp)
                            }

                            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                Button(
                                    onClick = {
                                        currentSlideIndex = if (currentSlideIndex > 0) currentSlideIndex - 1 else presentationSlides.size - 1
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = Navy700),
                                    shape = RoundedCornerShape(10.dp)
                                ) {
                                    Text("Slide Trước", fontSize = 11.sp)
                                }

                                Button(
                                    onClick = {
                                        currentSlideIndex = (currentSlideIndex + 1) % presentationSlides.size
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = GoldYellow),
                                    shape = RoundedCornerShape(10.dp)
                                ) {
                                    Text("Slide Tiếp", color = Navy900, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                                }
                            }
                        }
                    }
                }
            } else {
                // MODE B: SHARED VOCABULARY WHITEBOARD
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .testTag("shared_vocab_board"),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = Navy800),
                    border = CardDefaults.outlinedCardBorder().copy(brush = Brush.verticalGradient(listOf(DarkBorder, CyanAccent.copy(alpha = 0.5f))))
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(14.dp),
                        verticalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(imageVector = Icons.Default.Psychology, contentDescription = null, tint = GoldYellow, modifier = Modifier.size(18.dp))
                                    Text(
                                        text = "BẢNG TỪ VỰNG THẢO LUẬN",
                                        color = GoldYellow,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        letterSpacing = 1.sp
                                    )
                                }
                                Text(
                                    text = "Từ ${currentVocabIndex + 1}/${vocabDiscussionList.size}",
                                    color = SlateBlue,
                                    fontSize = 11.sp
                                )
                            }

                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(14.dp),
                                colors = CardDefaults.cardColors(containerColor = DarkSurface)
                            ) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(14.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Text(
                                        text = currentVocab.first,
                                        color = CyanAccent,
                                        fontSize = 24.sp,
                                        fontWeight = FontWeight.ExtraBold
                                    )
                                    Text(
                                        text = currentVocab.second,
                                        color = SlateBlue,
                                        fontSize = 13.sp
                                    )
                                    HorizontalDivider(
                                        modifier = Modifier.padding(vertical = 4.dp),
                                        color = DarkBorder
                                    )
                                    Text(
                                        text = currentVocab.third,
                                        color = TextPrimary,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        textAlign = TextAlign.Center
                                    )
                                }
                            }
                        }

                        // Whiteboard Action Buttons
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Button(
                                onClick = {
                                    playVoiceAudio("${currentVocab.first}. Nghĩa là: ${currentVocab.third}")
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = CyanAccent),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Icon(imageVector = Icons.AutoMirrored.Filled.VolumeUp, contentDescription = null, tint = Navy900, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Phát Âm Tiếng Anh", color = Navy900, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                            }

                            Button(
                                onClick = {
                                    currentVocabIndex = (currentVocabIndex + 1) % vocabDiscussionList.size
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = Navy700),
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier.testTag("next_shared_vocab_button")
                            ) {
                                Text("Từ Tiếp Theo", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                                Spacer(modifier = Modifier.width(4.dp))
                                Icon(imageVector = Icons.Default.ChevronRight, contentDescription = null, tint = TextPrimary, modifier = Modifier.size(16.dp))
                            }
                        }
                    }
                }
            }
        }
    }
}
