package com.taphunter.english.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.taphunter.english.data.models.TeacherAssignment
import com.taphunter.english.ui.theme.*

@Composable
fun AssignmentQuizScreen(
    assignment: TeacherAssignment,
    onFinish: (scorePoints: Int) -> Unit,
    onBack: () -> Unit
) {
    BackHandler { onBack() }

    var currentQIndex by remember { mutableStateOf(0) }
    val userAnswers = remember { mutableStateMapOf<Int, Int>() } // questionIndex -> selectedOptionIndex
    var isSubmitted by remember { mutableStateOf(false) }

    val questions = assignment.questions
    val currentQuestion = questions.getOrNull(currentQIndex)

    if (isSubmitted) {
        val correctCount = questions.indices.count { index ->
            userAnswers[index] == questions[index].correctIndex
        }
        val scorePercent = if (questions.isNotEmpty()) (correctCount * 100 / questions.size) else 0
        val xpReward = correctCount * 50

        LaunchedEffect(Unit) {
            onFinish(xpReward)
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(Navy900)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = DarkCard),
                    border = CardDefaults.outlinedCardBorder().copy(brush = Brush.horizontalGradient(listOf(CyanAccent, GoldYellow)))
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Text(
                            text = "KẾT QUẢ BÀI THI",
                            color = GoldYellow,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            letterSpacing = 1.sp
                        )
                        Text(
                            text = assignment.title,
                            color = TextPrimary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            textAlign = TextAlign.Center
                        )
                        Text(
                            text = "Đúng: $correctCount / ${questions.size} câu ($scorePercent%)",
                            color = if (scorePercent >= 50) GreenSuccess else RedDanger,
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 22.sp
                        )
                        Text(
                            text = "+$xpReward XP kinh nghiệm thợ săn",
                            color = PurpleNeon,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )

                        Button(
                            onClick = onBack,
                            colors = ButtonDefaults.buttonColors(containerColor = CyanAccent),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Quay Lại Danh Sách Bài", color = Navy900, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            item {
                Text(
                    text = "Chi tiết lời giải từ giáo viên:",
                    color = TextPrimary,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
            }

            items(questions.size) { index ->
                val q = questions[index]
                val selected = userAnswers[index]
                val isCorrect = selected == q.correctIndex

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = Navy800),
                    border = CardDefaults.outlinedCardBorder().copy(
                        brush = Brush.horizontalGradient(
                            listOf(if (isCorrect) GreenSuccess else RedDanger, if (isCorrect) GreenSuccess else RedDanger)
                        )
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Câu ${index + 1}:", color = SlateBlue, fontSize = 12.sp)
                            Text(
                                text = if (isCorrect) "Chính xác ✓" else "Chưa đúng ✗",
                                color = if (isCorrect) GreenSuccess else RedDanger,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp
                            )
                        }
                        Text(q.question, color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 15.sp)

                        Text(
                            text = "Đáp án đúng: ${q.options.getOrNull(q.correctIndex)}",
                            color = GreenSuccess,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold
                        )

                        Text(
                            text = "Giải thích: ${q.explanation}",
                            color = SlateBlue,
                            fontSize = 12.sp
                        )
                    }
                }
            }
        }
        return
    }

    Scaffold(
        topBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Navy900)
                    .padding(horizontal = 16.dp, vertical = 10.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBack) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Trở về", tint = TextPrimary)
                }
                Text(
                    text = "Câu ${currentQIndex + 1}/${questions.size}",
                    color = CyanAccent,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
                Button(
                    onClick = { isSubmitted = true },
                    colors = ButtonDefaults.buttonColors(containerColor = GoldYellow),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.testTag("submit_assignment_quiz_button")
                ) {
                    Text("Nộp Bài", color = Navy900, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                }
            }
        },
        containerColor = Navy900
    ) { padding ->
        if (currentQuestion == null) return@Scaffold

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                // Header Question Card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = DarkCard)
                ) {
                    Column(
                        modifier = Modifier.padding(18.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = assignment.title,
                            color = GoldYellow,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = currentQuestion.question,
                            color = TextPrimary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            lineHeight = 24.sp
                        )
                    }
                }

                // Options List
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    currentQuestion.options.forEachIndexed { optIndex, optionText ->
                        val isSelected = userAnswers[currentQIndex] == optIndex
                        val letter = ('A' + optIndex).toString()

                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    userAnswers[currentQIndex] = optIndex
                                }
                                .testTag("quiz_option_$optIndex"),
                            shape = RoundedCornerShape(14.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = if (isSelected) CyanAccent.copy(alpha = 0.2f) else Navy800
                            ),
                            border = CardDefaults.outlinedCardBorder().copy(
                                brush = Brush.horizontalGradient(
                                    listOf(if (isSelected) CyanAccent else DarkBorder, if (isSelected) CyanAccent else DarkBorder)
                                )
                            )
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(14.dp),
                                horizontalArrangement = Arrangement.spacedBy(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(32.dp)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(if (isSelected) CyanAccent else Navy700),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = letter,
                                        color = if (isSelected) Navy900 else TextPrimary,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.sp
                                    )
                                }
                                Text(
                                    text = optionText,
                                    color = if (isSelected) CyanAccent else TextPrimary,
                                    fontSize = 15.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                )
                            }
                        }
                    }
                }
            }

            // Bottom Nav Between Questions
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedButton(
                    onClick = { if (currentQIndex > 0) currentQIndex -= 1 },
                    enabled = currentQIndex > 0,
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Icon(Icons.Default.ArrowBack, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Câu trước")
                }

                if (currentQIndex < questions.size - 1) {
                    Button(
                        onClick = { currentQIndex += 1 },
                        colors = ButtonDefaults.buttonColors(containerColor = CyanAccent),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text("Câu sau", color = Navy900, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.width(4.dp))
                        Icon(Icons.Default.ArrowForward, contentDescription = null, tint = Navy900, modifier = Modifier.size(16.dp))
                    }
                } else {
                    Button(
                        onClick = { isSubmitted = true },
                        colors = ButtonDefaults.buttonColors(containerColor = GreenSuccess),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text("Hoàn Thành", color = Navy900, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
