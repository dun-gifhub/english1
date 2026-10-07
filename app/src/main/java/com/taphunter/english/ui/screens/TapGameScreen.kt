package com.taphunter.english.ui.screens

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import androidx.activity.compose.BackHandler
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.taphunter.english.data.models.UnitTopic
import com.taphunter.english.data.models.WordItem
import com.taphunter.english.ui.theme.*
import kotlinx.coroutines.delay

@Composable
fun TapGameScreen(
    unit: UnitTopic,
    onGameOver: (score: Int, xpEarned: Int) -> Unit,
    onBack: () -> Unit
) {
    BackHandler { onBack() }

    val context = LocalContext.current
    val vibrator = remember {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
        } else {
            @Suppress("DEPRECATION")
            context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
        }
    }

    val words = remember { unit.words.shuffled() }

    if (words.isEmpty()) {
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
                    imageVector = Icons.Default.Info,
                    contentDescription = null,
                    tint = GoldYellow,
                    modifier = Modifier.size(56.dp)
                )
                Text(
                    text = "Bài học chưa có từ vựng!",
                    color = TextPrimary,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Thầy cô có thể soạn từ mới cho khối lớp này tại góc Giáo viên để bắt đầu săn từ.",
                    color = SlateBlue,
                    fontSize = 13.sp,
                    textAlign = TextAlign.Center
                )
                Button(
                    onClick = onBack,
                    colors = ButtonDefaults.buttonColors(containerColor = CyanAccent),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Quay Lại", color = Navy900, fontWeight = FontWeight.Bold)
                }
            }
        }
        return
    }

    var currentWordIndex by remember { mutableStateOf(0) }
    var score by remember { mutableStateOf(0) }
    var combo by remember { mutableStateOf(1) }
    var maxCombo by remember { mutableStateOf(1) }
    var lives by remember { mutableStateOf(3) }
    var correctCount by remember { mutableStateOf(0) }
    var wrongCount by remember { mutableStateOf(0) }

    var timeRemaining by remember { mutableStateOf(10f) }
    var isTimerFrozen by remember { mutableStateOf(false) }
    var usedFiftyFifty by remember { mutableStateOf(false) }
    var usedFreeze by remember { mutableStateOf(false) }

    var selectedOption by remember { mutableStateOf<String?>(null) }
    var answerState by remember { mutableStateOf<AnswerResult?>(null) } // null, Correct, Wrong
    var isGameFinished by remember { mutableStateOf(false) }

    val currentWord = words.getOrNull(currentWordIndex)

    // Generate options for current word with robust fallback for custom teacher words
    val defaultDistractorPool = remember {
        listOf(
            "Có mặt ở khắp nơi, phổ biến", "Sự kiên cường, phục hồi", "Tỉ mỉ, cẩn thận",
            "Phù du, ngắn ngủi", "Trụ cột gia đình", "Người làm nội trợ",
            "Thân thiện môi trường", "Dấu chân carbon", "Cống hiến, hy sinh",
            "Kiệt xuất, lỗi lạc", "Kiên trì vượt khó", "Đa dạng sinh học",
            "Thảm họa tàn khốc", "Yên bình, thanh thản", "Vụ thu hoạch mùa màng"
        )
    }

    var currentOptions by remember(currentWordIndex) {
        mutableStateOf(
            if (currentWord != null) {
                val opts = mutableListOf(currentWord.meaningVi)
                opts.addAll(currentWord.distractorsVi.filter { it.isNotBlank() && it != currentWord.meaningVi })
                if (opts.size < 4) {
                    val otherMeanings = words.map { it.meaningVi }.filter { it != currentWord.meaningVi && !opts.contains(it) }
                    opts.addAll(otherMeanings)
                }
                if (opts.size < 4) {
                    val fallbacks = defaultDistractorPool.filter { it != currentWord.meaningVi && !opts.contains(it) }
                    opts.addAll(fallbacks)
                }
                opts.distinct().take(4).shuffled()
            } else emptyList()
        )
    }

    fun triggerVibration(isCorrect: Boolean) {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val effect = if (isCorrect) {
                    VibrationEffect.createOneShot(50, VibrationEffect.DEFAULT_AMPLITUDE)
                } else {
                    VibrationEffect.createWaveform(longArrayOf(0, 80, 50, 80), -1)
                }
                vibrator?.vibrate(effect)
            } else {
                @Suppress("DEPRECATION")
                vibrator?.vibrate(if (isCorrect) 50 else 150)
            }
        } catch (_: Exception) {}
    }

    // Countdown Timer Loop
    LaunchedEffect(currentWordIndex, isTimerFrozen, isGameFinished) {
        if (isGameFinished || currentWord == null) return@LaunchedEffect
        timeRemaining = 10f
        while (timeRemaining > 0 && answerState == null && !isGameFinished) {
            delay(100)
            if (!isTimerFrozen) {
                timeRemaining -= 0.1f
            }
        }
        if (timeRemaining <= 0 && answerState == null && !isGameFinished) {
            // Time out counted as wrong
            triggerVibration(false)
            lives -= 1
            combo = 1
            wrongCount += 1
            answerState = AnswerResult.Wrong(currentWord.meaningVi)
            delay(1000)
            if (lives <= 0 || currentWordIndex >= words.size - 1) {
                isGameFinished = true
            } else {
                answerState = null
                currentWordIndex += 1
            }
        }
    }

    fun handleOptionTap(option: String) {
        if (answerState != null || isGameFinished || currentWord == null) return
        selectedOption = option

        if (option == currentWord.meaningVi) {
            triggerVibration(true)
            val points = (100 * combo) + (timeRemaining * 10).toInt()
            score += points
            correctCount += 1
            combo += 1
            if (combo > maxCombo) maxCombo = combo
            answerState = AnswerResult.Correct
        } else {
            triggerVibration(false)
            lives -= 1
            combo = 1
            wrongCount += 1
            answerState = AnswerResult.Wrong(currentWord.meaningVi)
        }
    }

    // Transition to next word after answering
    LaunchedEffect(answerState) {
        val state = answerState ?: return@LaunchedEffect
        delay(900)
        selectedOption = null
        answerState = null
        if (lives <= 0 || currentWordIndex >= words.size - 1) {
            isGameFinished = true
        } else {
            currentWordIndex += 1
        }
    }

    if (isGameFinished || currentWord == null) {
        val xpEarned = (score / 10) + (correctCount * 25)
        LaunchedEffect(Unit) {
            onGameOver(score, xpEarned)
        }

        // Summary Dialog
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Navy900)
                .padding(24.dp),
            contentAlignment = Alignment.Center
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("game_result_card"),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = DarkCard),
                border = CardDefaults.outlinedCardBorder().copy(brush = Brush.verticalGradient(listOf(CyanAccent, GoldYellow)))
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(70.dp)
                            .clip(CircleShape)
                            .background(if (lives > 0) GoldYellow.copy(alpha = 0.2f) else RedDanger.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = if (lives > 0) Icons.Default.EmojiEvents else Icons.Default.HeartBroken,
                            contentDescription = null,
                            tint = if (lives > 0) GoldYellow else RedDanger,
                            modifier = Modifier.size(40.dp)
                        )
                    }

                    Text(
                        text = if (lives > 0) "Hoàn Thành Xuất Sắc!" else "Hết Mạng - Đấu Lại Nhé!",
                        color = TextPrimary,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )

                    Text(
                        text = "${unit.title} (Unit ${unit.unitNumber})",
                        color = CyanAccent,
                        fontSize = 14.sp
                    )

                    // Stats Grid
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(Navy800)
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("Điểm số", color = TextSecondary, fontSize = 12.sp)
                            Text("$score", color = GoldYellow, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                        }
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("Combo max", color = TextSecondary, fontSize = 12.sp)
                            Text("x$maxCombo", color = CyanAccent, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                        }
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("Đúng", color = TextSecondary, fontSize = 12.sp)
                            Text("$correctCount/${words.size}", color = GreenSuccess, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                        }
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("XP Nhận", color = TextSecondary, fontSize = 12.sp)
                            Text("+$xpEarned", color = PurpleNeon, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    Button(
                        onClick = {
                            currentWordIndex = 0
                            score = 0
                            combo = 1
                            maxCombo = 1
                            lives = 3
                            correctCount = 0
                            wrongCount = 0
                            usedFiftyFifty = false
                            usedFreeze = false
                            answerState = null
                            isGameFinished = false
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .testTag("play_again_button"),
                        colors = ButtonDefaults.buttonColors(containerColor = CyanAccent),
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Icon(imageVector = Icons.Default.Replay, contentDescription = null, tint = Navy900)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Chơi Lại", color = Navy900, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    }

                    OutlinedButton(
                        onClick = onBack,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .testTag("return_home_button"),
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Text("Trở Về Menu Bài Học", color = TextPrimary)
                    }
                }
            }
        }
        return
    }

    // Active Game Screen
    Scaffold(
        topBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Navy900)
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = onBack, modifier = Modifier.testTag("game_back_button")) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "Thoát game", tint = TextPrimary)
                    }

                    // Lives Display
                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        repeat(3) { index ->
                            Icon(
                                imageVector = if (index < lives) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                                contentDescription = null,
                                tint = RedDanger,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                    }

                    // Combo Multiplier
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (combo > 1) GoldYellow else Navy700)
                            .padding(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = "COMBO x$combo",
                            color = if (combo > 1) Navy900 else TextSecondary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        )
                    }
                }

                // Progress Bar of Current Word Timer
                Spacer(modifier = Modifier.height(8.dp))
                val timerFraction = (timeRemaining / 10f).coerceIn(0f, 1f)
                LinearProgressIndicator(
                    progress = { timerFraction },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(6.dp)
                        .clip(RoundedCornerShape(3.dp)),
                    color = if (timeRemaining > 3) CyanAccent else RedDanger,
                    trackColor = Navy800
                )
            }
        },
        containerColor = Navy900
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Score and Word Counter
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Từ ${currentWordIndex + 1}/${words.size}",
                    color = SlateBlue,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = "$score PTS",
                    color = GoldYellow,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.ExtraBold
                )
            }

            // Word Hunting Target Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("target_word_card"),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = DarkCard),
                border = CardDefaults.outlinedCardBorder().copy(
                    brush = Brush.linearGradient(
                        listOf(CyanAccent.copy(alpha = 0.6f), PurpleNeon.copy(alpha = 0.6f))
                    )
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 28.dp, horizontal = 20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "SĂN NGHĨA CHÍNH XÁC",
                        color = CyanAccent,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.2.sp
                    )

                    Text(
                        text = currentWord.word,
                        color = TextPrimary,
                        fontSize = 32.sp,
                        fontWeight = FontWeight.ExtraBold,
                        textAlign = TextAlign.Center
                    )

                    Text(
                        text = currentWord.phonetic,
                        color = GoldYellow,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = "Ví dụ: \"${currentWord.exampleEn}\"",
                        color = SlateBlue,
                        fontSize = 13.sp,
                        textAlign = TextAlign.Center,
                        lineHeight = 18.sp
                    )
                }
            }

            // Power-ups Bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                AssistChip(
                    onClick = {
                        if (!usedFiftyFifty && currentOptions.size > 2) {
                            usedFiftyFifty = true
                            val wrongAnswers = currentOptions.filter { it != currentWord.meaningVi }
                            val toKeepWrong = wrongAnswers.shuffled().take(1)
                            currentOptions = (listOf(currentWord.meaningVi) + toKeepWrong).shuffled()
                        }
                    },
                    enabled = !usedFiftyFifty,
                    label = { Text("Trợ giúp 50:50") },
                    leadingIcon = {
                        Icon(imageVector = Icons.Default.FilterAlt, contentDescription = null, modifier = Modifier.size(16.dp))
                    },
                    modifier = Modifier.testTag("powerup_fifty_fifty")
                )

                Spacer(modifier = Modifier.width(12.dp))

                AssistChip(
                    onClick = {
                        if (!usedFreeze) {
                            usedFreeze = true
                            isTimerFrozen = true
                            timeRemaining = (timeRemaining + 5f).coerceAtMost(10f)
                        }
                    },
                    enabled = !usedFreeze,
                    label = { Text("Đóng băng giờ (+5s)") },
                    leadingIcon = {
                        Icon(imageVector = Icons.Default.AcUnit, contentDescription = null, modifier = Modifier.size(16.dp))
                    },
                    modifier = Modifier.testTag("powerup_freeze_time")
                )
            }

            // 4 Tap Options (Tap Hunter Grid)
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                currentOptions.forEachIndexed { index, option ->
                    val isSelected = selectedOption == option
                    val isCorrectMeaning = option == currentWord.meaningVi

                    val backgroundColor = when {
                        isSelected && answerState is AnswerResult.Correct -> GreenSuccess
                        isSelected && answerState is AnswerResult.Wrong -> RedDanger
                        answerState is AnswerResult.Wrong && isCorrectMeaning -> GreenSuccess.copy(alpha = 0.8f)
                        else -> DarkCard
                    }

                    val borderColor = when {
                        isSelected && answerState is AnswerResult.Correct -> GreenSuccess
                        isSelected && answerState is AnswerResult.Wrong -> RedDanger
                        answerState is AnswerResult.Wrong && isCorrectMeaning -> GreenSuccess
                        else -> DarkBorder
                    }

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable(enabled = answerState == null) {
                                handleOptionTap(option)
                            }
                            .testTag("option_card_$index"),
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = backgroundColor),
                        border = CardDefaults.outlinedCardBorder().copy(
                            brush = Brush.horizontalGradient(listOf(borderColor, borderColor))
                        )
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = option,
                                color = if (isSelected || (answerState is AnswerResult.Wrong && isCorrectMeaning)) Navy900 else TextPrimary,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.SemiBold,
                                modifier = Modifier.weight(1f)
                            )

                            if (isSelected && answerState is AnswerResult.Correct) {
                                Icon(imageVector = Icons.Default.CheckCircle, contentDescription = null, tint = Navy900)
                            } else if (isSelected && answerState is AnswerResult.Wrong) {
                                Icon(imageVector = Icons.Default.Cancel, contentDescription = null, tint = Navy900)
                            }
                        }
                    }
                }
            }
        }
    }
}

sealed class AnswerResult {
    data object Correct : AnswerResult()
    data class Wrong(val correctAnswer: String) : AnswerResult()
}
