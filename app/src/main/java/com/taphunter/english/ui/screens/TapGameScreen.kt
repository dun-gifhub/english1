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
import java.text.Normalizer
import java.util.regex.Pattern

enum class GameDifficulty(val label: String, val fallDurationMs: Long, val description: String) {
    EASY("Dễ (Tân Binh)", 11000L, "Tốc độ chậm, 4 thẻ gợi ý từ bên dưới"),
    MEDIUM("Trung Bình (Chiến Binh)", 7000L, "Tốc độ vừa, gợi ý từ đảo xáo & gõ phím"),
    HARD("Khó (Thợ Săn THPT)", 4500L, "Tốc độ rơi cực nhanh, gõ từ trực tiếp để phá huỷ")
}

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

    var difficulty by remember { mutableStateOf(GameDifficulty.EASY) }
    var isEnglishFalling by remember { mutableStateOf(true) } // English falling vs Vietnamese falling

    var currentWordIndex by remember { mutableStateOf(0) }
    var score by remember { mutableStateOf(0) }
    var combo by remember { mutableStateOf(1) }
    var maxCombo by remember { mutableStateOf(1) }
    var lives by remember { mutableStateOf(3) }
    var destroyedCount by remember { mutableStateOf(0) }
    var missedCount by remember { mutableStateOf(0) }

    var fallingProgress by remember { mutableFloatStateOf(0f) } // 0f (top) -> 1.0f (bottom baseline)
    var isWordDestroyed by remember { mutableStateOf(false) }
    var isWordMissed by remember { mutableStateOf(false) }
    var destructionMessage by remember { mutableStateOf<String?>(null) }
    var isGameFinished by remember { mutableStateOf(false) }
    var typedInput by remember { mutableStateOf("") }

    val currentWord = words.getOrNull(currentWordIndex)

    fun triggerVibration(isDestroyed: Boolean) {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val effect = if (isDestroyed) {
                    VibrationEffect.createOneShot(70, VibrationEffect.DEFAULT_AMPLITUDE)
                } else {
                    VibrationEffect.createWaveform(longArrayOf(0, 100, 60, 100), -1)
                }
                vibrator?.vibrate(effect)
            } else {
                @Suppress("DEPRECATION")
                vibrator?.vibrate(if (isDestroyed) 70 else 200)
            }
        } catch (_: Exception) {}
    }

    fun cleanText(text: String): String {
        val normalized = Normalizer.normalize(text.trim().lowercase(), Normalizer.Form.NFD)
        val pattern = Pattern.compile("\\p{InCombiningDiacriticalMarks}+")
        return pattern.matcher(normalized).replaceAll("").replace("đ", "d").replace("Đ", "D")
    }

    // Generate suggestions based on mode
    val currentSuggestions = remember(currentWordIndex, isEnglishFalling, difficulty) {
        if (currentWord == null) emptyList()
        else {
            val targetCorrect = if (isEnglishFalling) currentWord.meaningVi else currentWord.word
            val pool = words.filter { it.id != currentWord.id }.map { if (isEnglishFalling) it.meaningVi else it.word }
            val distractors = currentWord.distractorsVi.filter { it.isNotBlank() && it != targetCorrect }
            (listOf(targetCorrect) + (distractors + pool).shuffled()).distinct().take(4).shuffled()
        }
    }

    // Dynamic speed factor: Starts slow and increases with each word and destroyed count
    val currentSpeedFactor = remember(destroyedCount, currentWordIndex) {
        1.0f + (destroyedCount * 0.12f) + (currentWordIndex * 0.08f)
    }

    // Destroy action logic
    fun executeDestroyWord(bonusFactor: Float = 1f) {
        if (isWordDestroyed || isWordMissed || isGameFinished || currentWord == null) return
        isWordDestroyed = true
        triggerVibration(true)

        val basePoints = 100
        val comboBonus = combo * 25
        val speedBonus = ((1f - fallingProgress) * 50 * currentSpeedFactor).toInt()
        val earned = ((basePoints + comboBonus + speedBonus) * bonusFactor).toInt()

        score += earned
        destroyedCount += 1
        combo += 1
        if (combo > maxCombo) maxCombo = combo
        destructionMessage = "+$earned PTS! COMBO x$combo! 💥 PHÁ HUỶ TỪ!"
        typedInput = ""
    }

    // Handle check from typed input or clicked suggestion
    fun checkAndDestroy(input: String, isExplicitSubmit: Boolean = false) {
        if (currentWord == null || isWordDestroyed || isWordMissed || isGameFinished) return
        val target = if (isEnglishFalling) currentWord.meaningVi else currentWord.word

        val isMatch = cleanText(input) == cleanText(target) ||
                cleanText(input) == cleanText(currentWord.word) ||
                cleanText(input) == cleanText(currentWord.meaningVi) ||
                (input.length >= 3 && cleanText(target).contains(cleanText(input)))

        if (isMatch) {
            executeDestroyWord(bonusFactor = 1.2f)
        } else if (isExplicitSubmit && input.isNotBlank()) {
            // User submitted an incorrect answer: LOSE 1 HEART!
            triggerVibration(false)
            lives -= 1
            combo = 1
            if (lives <= 0) {
                isGameFinished = true
                destructionMessage = "☠️ Hết 3 mạng! Sai 3 lần => BẠN ĐÃ THUA CUỘC!"
            } else {
                destructionMessage = "❌ Trả lời sai! Mất 1 ❤️ (Còn $lives/3 mạng)!"
            }
        }
    }

    // Falling word animation loop with progressive speed acceleration
    LaunchedEffect(currentWordIndex, isGameFinished, difficulty, currentSpeedFactor) {
        if (isGameFinished || currentWord == null) return@LaunchedEffect
        fallingProgress = 0f
        isWordDestroyed = false
        isWordMissed = false
        destructionMessage = null

        val dynamicDurationMs = (difficulty.fallDurationMs / currentSpeedFactor).toLong().coerceIn(2200L, 12000L)
        val tickMs = 50L
        val step = tickMs.toFloat() / dynamicDurationMs.toFloat()

        while (fallingProgress < 1.0f && !isWordDestroyed && !isGameFinished) {
            delay(tickMs)
            fallingProgress = (fallingProgress + step).coerceAtMost(1.0f)
        }

        if (fallingProgress >= 1.0f && !isWordDestroyed && !isGameFinished) {
            // Reached bottom baseline: Word Missed! LOSE 1 HEART!
            isWordMissed = true
            triggerVibration(false)
            lives -= 1
            combo = 1
            missedCount += 1

            if (lives <= 0) {
                destructionMessage = "☠️ Từ chạm đáy! Hết 3 mạng => BẠN ĐÃ THUA CUỘC!"
                delay(900)
                isGameFinished = true
            } else {
                destructionMessage = "⚠️ Từ chạm đáy phòng thủ! Mất 1 ❤️ (Còn $lives/3 mạng)!"
                delay(900)
                if (currentWordIndex >= words.size - 1) {
                    isGameFinished = true
                } else {
                    currentWordIndex += 1
                }
            }
        }
    }

    // Handle smooth transition after word is destroyed
    LaunchedEffect(isWordDestroyed) {
        if (isWordDestroyed) {
            delay(750)
            if (currentWordIndex >= words.size - 1 || lives <= 0) {
                isGameFinished = true
            } else {
                currentWordIndex += 1
            }
        }
    }

    // Final Game Over Screen
    if (isGameFinished || currentWord == null) {
        val xpEarned = (score / 8) + (destroyedCount * 30)
        LaunchedEffect(Unit) {
            onGameOver(score, xpEarned)
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Navy900)
                .padding(20.dp),
            contentAlignment = Alignment.Center
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("game_result_card"),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = DarkCard),
                border = CardDefaults.outlinedCardBorder().copy(
                    brush = Brush.verticalGradient(listOf(CyanAccent, GoldYellow))
                )
            ) {
                Column(
                    modifier = Modifier.padding(22.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(72.dp)
                            .clip(CircleShape)
                            .background(if (lives > 0) GoldYellow.copy(alpha = 0.2f) else RedDanger.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = if (lives > 0) Icons.Default.EmojiEvents else Icons.Default.HeartBroken,
                            contentDescription = null,
                            tint = if (lives > 0) GoldYellow else RedDanger,
                            modifier = Modifier.size(42.dp)
                        )
                    }

                    Text(
                        text = if (lives > 0) "CHIẾN THẮNG PHẢN XẠ!" else "HẾT MẠNG - ĐẤU LẠI NHÉ!",
                        color = TextPrimary,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Black,
                        textAlign = TextAlign.Center
                    )

                    Text(
                        text = "${unit.title} • Cấp độ: ${difficulty.label}",
                        color = CyanAccent,
                        fontSize = 13.sp,
                        textAlign = TextAlign.Center
                    )

                    // Stats Grid
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(Navy800)
                            .padding(14.dp),
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("Điểm số", color = TextSecondary, fontSize = 11.sp)
                            Text("$score", color = GoldYellow, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                        }
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("Max Combo", color = TextSecondary, fontSize = 11.sp)
                            Text("x$maxCombo", color = CyanAccent, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                        }
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("Đã phá huỷ", color = TextSecondary, fontSize = 11.sp)
                            Text("$destroyedCount/${words.size}", color = GreenSuccess, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                        }
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("XP Nhận", color = TextSecondary, fontSize = 11.sp)
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
                            destroyedCount = 0
                            missedCount = 0
                            fallingProgress = 0f
                            isWordDestroyed = false
                            isWordMissed = false
                            isGameFinished = false
                            typedInput = ""
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("play_again_button"),
                        colors = ButtonDefaults.buttonColors(containerColor = CyanAccent),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(Icons.Default.Replay, contentDescription = null, tint = Navy900)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Đấu Lại Trận Này", color = Navy900, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                    }

                    OutlinedButton(
                        onClick = onBack,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(46.dp),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Quay Về Trang Chủ", color = TextPrimary)
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
                    .padding(horizontal = 14.dp, vertical = 8.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = onBack, modifier = Modifier.size(36.dp)) {
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
                            .padding(horizontal = 8.dp, vertical = 3.dp)
                    ) {
                        Text(
                            text = "COMBO x$combo",
                            color = if (combo > 1) Navy900 else TextSecondary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp
                        )
                    }

                    Text(
                        text = "$score PTS",
                        color = GoldYellow,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                }

                // Difficulty Selector & Mode Chips
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        GameDifficulty.entries.forEach { diff ->
                            val isSelected = difficulty == diff
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(if (isSelected) CyanAccent else Navy800)
                                    .clickable { difficulty = diff }
                                    .padding(horizontal = 6.dp, vertical = 3.dp)
                            ) {
                                Text(
                                    text = diff.name,
                                    fontSize = 10.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                    color = if (isSelected) Navy900 else SlateBlue
                                )
                            }
                        }
                    }

                    // Dynamic Speed Multiplier & Language Switch
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        val speedColor = when {
                            currentSpeedFactor >= 2.0f -> RedDanger
                            currentSpeedFactor >= 1.4f -> GoldYellow
                            else -> CyanAccent
                        }
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(speedColor.copy(alpha = 0.2f))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = "⚡ ${String.format(java.util.Locale.US, "%.1fx", currentSpeedFactor)}",
                                color = speedColor,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.ExtraBold
                            )
                        }

                        Text(
                            text = if (isEnglishFalling) "EN ➔ VI" else "VI ➔ EN",
                            color = CyanAccent,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.clickable { isEnglishFalling = !isEnglishFalling }
                        )
                    }
                }
            }
        },
        containerColor = Navy900
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(12.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // 1. FALLING ARENA (Vùng từ rơi từ trên xuống)
            BoxWithConstraints(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color(0xFF08121E))
                    .border(
                        1.5.dp,
                        if (fallingProgress > 0.8f) RedDanger else DarkBorder,
                        RoundedCornerShape(16.dp)
                    )
                    .padding(8.dp)
            ) {
                val arenaHeight = maxHeight - 70.dp
                val currentOffset = arenaHeight * fallingProgress

                // Destruction burst animation scale
                val destroyScale by animateFloatAsState(
                    targetValue = if (isWordDestroyed) 1.6f else 1.0f,
                    animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
                    label = "destroyScale"
                )

                // The Falling Word Card
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .offset(y = currentOffset)
                        .scale(destroyScale),
                    contentAlignment = Alignment.Center
                ) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth(0.92f)
                            .testTag("falling_word_card"),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = when {
                                isWordDestroyed -> GreenSuccess
                                isWordMissed -> RedDanger
                                fallingProgress > 0.75f -> Color(0xFF6B1D2F)
                                else -> DarkCard
                            }
                        ),
                        border = CardDefaults.outlinedCardBorder().copy(
                            brush = Brush.horizontalGradient(
                                if (isWordDestroyed) listOf(GreenSuccess, GoldYellow)
                                else if (fallingProgress > 0.75f) listOf(RedDanger, GoldYellow)
                                else listOf(CyanAccent, PurpleNeon)
                            )
                        )
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 14.dp, horizontal = 12.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text(
                                text = if (isWordDestroyed) "💥 ĐÃ PHÁ HUỶ!" else if (isEnglishFalling) "TỪ TIẾNG ANH ĐANG RƠI" else "TỪ TIẾNG VIỆT ĐANG RƠI",
                                color = if (isWordDestroyed) Navy900 else CyanAccent,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.sp
                            )

                            Text(
                                text = if (isEnglishFalling) currentWord.word else currentWord.meaningVi,
                                color = if (isWordDestroyed) Navy900 else TextPrimary,
                                fontSize = 24.sp,
                                fontWeight = FontWeight.ExtraBold,
                                textAlign = TextAlign.Center
                            )

                            if (isEnglishFalling && currentWord.phonetic.isNotBlank()) {
                                Text(
                                    text = currentWord.phonetic,
                                    color = if (isWordDestroyed) Navy900 else GoldYellow,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }
                }

                // Bottom Defense Line
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.BottomCenter)
                        .padding(bottom = 4.dp)
                ) {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        HorizontalDivider(
                            color = if (fallingProgress > 0.8f) RedDanger else CyanAccent,
                            thickness = 2.dp
                        )
                        Text(
                            text = "🛡️ PHÒNG TUYẾN BẢO VỆ (ĐỪNG ĐỂ TỪ CHẠM ĐÁY)",
                            color = SlateBlue,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(top = 2.dp)
                        )
                    }
                }
            }

            // Flash destruction feedback banner
            AnimatedVisibility(
                visible = destructionMessage != null,
                enter = fadeIn() + expandVertically(),
                exit = fadeOut()
            ) {
                Text(
                    text = destructionMessage ?: "",
                    color = if (isWordDestroyed) GoldYellow else RedDanger,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                )
            }

            // 2. INPUT & SUGGESTION CONTROLS (GỢI Ý TỪ BÊN DƯỚI & Ô NHẬP)
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 6.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Quick typing and destroy bar
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = typedInput,
                        onValueChange = {
                            typedInput = it
                            // Instant auto-destroy if exact match!
                            checkAndDestroy(it)
                        },
                        placeholder = {
                            Text(
                                if (isEnglishFalling) "Gõ nghĩa tiếng Việt hoặc từ..." else "Gõ từ tiếng Anh...",
                                fontSize = 12.sp,
                                color = SlateBlue
                            )
                        },
                        singleLine = true,
                        modifier = Modifier
                            .weight(1f)
                            .testTag("game_typed_input"),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = CyanAccent,
                            unfocusedBorderColor = DarkBorder,
                            focusedContainerColor = DarkSurface,
                            unfocusedContainerColor = DarkSurface
                        )
                    )

                    Button(
                        onClick = { checkAndDestroy(typedInput, isExplicitSubmit = true) },
                        modifier = Modifier
                            .height(50.dp)
                            .testTag("shoot_destroy_button"),
                        colors = ButtonDefaults.buttonColors(containerColor = GoldYellow),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(Icons.Default.Bolt, contentDescription = null, tint = Navy900)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("BẮN", color = Navy900, fontWeight = FontWeight.Black, fontSize = 13.sp)
                    }
                }

                // GỢI Ý TỪ BÊN DƯỚI (SUGGESTIONS ACCORDING TO DIFFICULTY LEVEL)
                Text(
                    text = when (difficulty) {
                        GameDifficulty.EASY -> "💡 GỢI Ý PHẢN XẠ NHANH (Bấm để phá huỷ ngay):"
                        GameDifficulty.MEDIUM -> "⚡ GỢI Ý 4 ĐÁP ÁN (Bấm hoặc gõ từ để phá huỷ):"
                        GameDifficulty.HARD -> "🎯 THỬ THÁCH CAO THỦ (Gợi ý chữ cái đầu: '${(if (isEnglishFalling) currentWord.meaningVi else currentWord.word).take(2)}...'):"
                    },
                    color = GoldYellow,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )

                // 4 Suggestion Cards Grid (2x2)
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        currentSuggestions.take(2).forEachIndexed { idx, opt ->
                            SuggestionCard(
                                text = opt,
                                modifier = Modifier.weight(1f),
                                onClick = { checkAndDestroy(opt, isExplicitSubmit = true) }
                            )
                        }
                    }
                    if (currentSuggestions.size > 2) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            currentSuggestions.drop(2).take(2).forEachIndexed { idx, opt ->
                                SuggestionCard(
                                    text = opt,
                                    modifier = Modifier.weight(1f),
                                    onClick = { checkAndDestroy(opt, isExplicitSubmit = true) }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SuggestionCard(
    text: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier
            .clickable { onClick() }
            .testTag("suggestion_card_${text.take(6)}"),
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(containerColor = DarkCard),
        border = CardDefaults.outlinedCardBorder().copy(
            brush = Brush.horizontalGradient(listOf(DarkBorder, CyanAccent.copy(alpha = 0.5f)))
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 10.dp, horizontal = 8.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = text,
                color = TextPrimary,
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                textAlign = TextAlign.Center,
                maxLines = 2
            )
        }
    }
}
