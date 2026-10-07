package com.taphunter.english.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.taphunter.english.data.models.*
import com.taphunter.english.ui.theme.*

@Composable
fun HomeScreen(
    user: UserProfile?,
    selectedGrade: Int,
    subjects: List<Subject>,
    grammarLessons: List<GrammarLesson>,
    assignments: List<TeacherAssignment>,
    onSelectGrade: (Int) -> Unit,
    onSelectUnitToPlay: (UnitTopic) -> Unit,
    onSelectAssignmentToTake: (TeacherAssignment) -> Unit,
    onOpenTeacherPortal: () -> Unit,
    onOpenAuth: () -> Unit
) {
    val context = LocalContext.current
    var studentSectionTab by remember { mutableIntStateOf(0) } // 0: Từ Vựng & Game, 1: Ngữ Pháp, 2: Bài Tập Giáo Viên
    var viewingGrammarLesson by remember { mutableStateOf<GrammarLesson?>(null) }

    val currentSubject = subjects.firstOrNull()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .testTag("home_screen_list"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Hunter Profile Banner
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("hunter_profile_card"),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Navy800),
                border = CardDefaults.outlinedCardBorder().copy(brush = Brush.horizontalGradient(listOf(CyanAccent, PurpleNeon)))
            ) {
                Column(
                    modifier = Modifier.padding(18.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(50.dp)
                                    .clip(CircleShape)
                                    .background(Color(user?.avatarColor ?: 0xFF00E5FF)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Bolt,
                                    contentDescription = "Avatar",
                                    tint = Navy900,
                                    modifier = Modifier.size(30.dp)
                                )
                            }
                            Column {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Text(
                                        text = user?.displayName ?: "Thợ Săn HSG",
                                        color = TextPrimary,
                                        fontSize = 17.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(6.dp))
                                            .background(CyanAccent.copy(alpha = 0.2f))
                                            .padding(horizontal = 6.dp, vertical = 2.dp)
                                    ) {
                                        Text("Lớp $selectedGrade", color = CyanAccent, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                                    }
                                }
                                Text(
                                    text = "Cấp ${user?.level ?: 1} • ${user?.xp ?: 0} XP",
                                    color = CyanAccent,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }

                        IconButton(
                            onClick = onOpenAuth,
                            modifier = Modifier.testTag("switch_account_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.AccountCircle,
                                contentDescription = "Tài khoản",
                                tint = GoldYellow
                            )
                        }
                    }

                    // Progress bar
                    val xpForCurrentLevel = (user?.xp ?: 0) % 500
                    val progress = (xpForCurrentLevel / 500f).coerceIn(0f, 1f)
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Tiến độ thăng cấp", color = TextSecondary, fontSize = 12.sp)
                            Text("$xpForCurrentLevel / 500 XP", color = TextSecondary, fontSize = 12.sp)
                        }
                        LinearProgressIndicator(
                            progress = { progress },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(6.dp)
                                .clip(RoundedCornerShape(3.dp)),
                            color = CyanAccent,
                            trackColor = Navy700
                        )
                    }

                    // Role switch affordance & Friend Code
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(Navy700)
                            .padding(horizontal = 12.dp, vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.School, contentDescription = null, tint = PurpleNeon, modifier = Modifier.size(16.dp))
                            Text(
                                text = "Bạn là Giáo viên?",
                                color = TextPrimary,
                                fontSize = 12.sp
                            )
                        }

                        Button(
                            onClick = onOpenTeacherPortal,
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = PurpleNeon),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.height(30.dp).testTag("open_teacher_branch_button")
                        ) {
                            Text("Nhánh Ra Đề GV", fontSize = 11.sp, color = TextPrimary, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        // Grade Selector (Lớp 6 đến Lớp 12)
        item {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Chọn Khối Lớp Học (6 - 12)",
                        color = TextPrimary,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Đang chọn: Lớp $selectedGrade",
                        color = CyanAccent,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(listOf(6, 7, 8, 9, 10, 11, 12)) { grade ->
                        val isSelected = selectedGrade == grade
                        FilterChip(
                            selected = isSelected,
                            onClick = { onSelectGrade(grade) },
                            label = {
                                Text(
                                    text = "Lớp $grade",
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                    fontSize = 13.sp
                                )
                            },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = CyanAccent,
                                selectedLabelColor = Navy900,
                                containerColor = DarkCard,
                                labelColor = TextPrimary
                            ),
                            border = FilterChipDefaults.filterChipBorder(
                                enabled = true,
                                selected = isSelected,
                                borderColor = if (isSelected) CyanAccent else DarkBorder
                            ),
                            modifier = Modifier.testTag("grade_chip_$grade")
                        )
                    }
                }
            }
        }

        // Tap Hunter Hero Card
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        val firstUnit = currentSubject?.units?.firstOrNull()
                        if (firstUnit != null) {
                            onSelectUnitToPlay(firstUnit)
                        }
                    }
                    .testTag("tap_hunter_hero_card"),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Navy800)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Brush.linearGradient(
                                listOf(Color(0xFF0F3460), Color(0xFF16213E), Color(0xFF533483))
                            )
                        )
                        .padding(18.dp)
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(Icons.Default.SportsEsports, contentDescription = null, tint = GoldYellow, modifier = Modifier.size(18.dp))
                            Text(
                                text = "ĐẤU TỪ PHẢN XẠ • LỚP $selectedGrade",
                                color = GoldYellow,
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 11.sp,
                                letterSpacing = 1.sp
                            )
                        }

                        Text(
                            text = "Tap Hunter: Săn Từ Vựng",
                            color = TextPrimary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp
                        )

                        Text(
                            text = "Luyện phản xạ tốc độ cao với các từ vựng chuẩn sách giáo khoa Lớp $selectedGrade và đề thi HSG!",
                            color = SlateBlue,
                            fontSize = 12.sp,
                            lineHeight = 17.sp
                        )

                        Button(
                            onClick = {
                                val firstUnit = currentSubject?.units?.firstOrNull()
                                if (firstUnit != null) {
                                    onSelectUnitToPlay(firstUnit)
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = CyanAccent),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.testTag("quick_hunt_button")
                        ) {
                            Icon(Icons.Default.PlayArrow, contentDescription = null, tint = Navy900)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Bắt Đầu Săn Từ Ngay", color = Navy900, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        // Section Tabs: 1. Từ Vựng & Game | 2. Ngữ Pháp | 3. Bài Tập GV Giao
        item {
            TabRow(
                selectedTabIndex = studentSectionTab,
                containerColor = Navy800,
                contentColor = CyanAccent,
                indicator = {},
                divider = {},
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(Navy800)
            ) {
                Tab(
                    selected = studentSectionTab == 0,
                    onClick = { studentSectionTab = 0 },
                    text = { Text("Từ Vựng (Units)", fontWeight = FontWeight.Bold, fontSize = 12.sp) }
                )
                Tab(
                    selected = studentSectionTab == 1,
                    onClick = { studentSectionTab = 1 },
                    text = { Text("Ngữ Pháp", fontWeight = FontWeight.Bold, fontSize = 12.sp) }
                )
                Tab(
                    selected = studentSectionTab == 2,
                    onClick = { studentSectionTab = 2 },
                    text = { Text("Bài Tập GV (${assignments.size})", fontWeight = FontWeight.Bold, fontSize = 12.sp) }
                )
            }
        }

        // SECTION CONTENT
        when (studentSectionTab) {
            0 -> {
                // Section 0: Units of the grade
                items(currentSubject?.units ?: emptyList()) { unit ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onSelectUnitToPlay(unit) }
                            .testTag("unit_card_${unit.unitNumber}"),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = DarkCard),
                        border = CardDefaults.outlinedCardBorder().copy(brush = Brush.horizontalGradient(listOf(DarkBorder, DarkBorder)))
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(14.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(12.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.weight(1f)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(44.dp)
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(if (unit.isCompleted) GreenSuccess.copy(alpha = 0.2f) else Navy700),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = if (unit.unitNumber == 0) "GV" else "U${unit.unitNumber}",
                                        color = if (unit.unitNumber == 0) PurpleNeon else (if (unit.isCompleted) GreenSuccess else CyanAccent),
                                        fontWeight = FontWeight.ExtraBold,
                                        fontSize = 15.sp
                                    )
                                }

                                Column(verticalArrangement = Arrangement.spacedBy(3.dp)) {
                                    Text(
                                        text = unit.title,
                                        color = TextPrimary,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 15.sp
                                    )
                                    Text(
                                        text = unit.description,
                                        color = TextSecondary,
                                        fontSize = 12.sp,
                                        maxLines = 1
                                    )
                                    Row(
                                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text("${unit.words.size} từ vựng", color = SlateBlue, fontSize = 11.sp)
                                        Text("• ${unit.difficulty}", color = GoldYellow, fontSize = 11.sp)
                                        if (unit.bestScore > 0) {
                                            Text("• Kỷ lục: ${unit.bestScore}", color = GreenSuccess, fontSize = 11.sp)
                                        }
                                    }
                                }
                            }

                            FilledIconButton(
                                onClick = { onSelectUnitToPlay(unit) },
                                colors = IconButtonDefaults.filledIconButtonColors(containerColor = CyanAccent),
                                modifier = Modifier.size(38.dp).testTag("play_unit_button_${unit.unitNumber}")
                            ) {
                                Icon(Icons.Default.PlayArrow, contentDescription = "Chơi Unit", tint = Navy900)
                            }
                        }
                    }
                }
            }

            1 -> {
                // Section 1: Grammar Lessons for this grade
                items(grammarLessons) { lesson ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { viewingGrammarLesson = lesson }
                            .testTag("grammar_card_${lesson.id}"),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = DarkCard),
                        border = CardDefaults.outlinedCardBorder().copy(brush = Brush.horizontalGradient(listOf(DarkBorder, DarkBorder)))
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = lesson.title,
                                    color = CyanAccent,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp
                                )
                                Icon(Icons.Default.MenuBook, contentDescription = null, tint = GoldYellow, modifier = Modifier.size(18.dp))
                            }

                            // Formula Preview Box
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(Navy800)
                                    .padding(10.dp)
                            ) {
                                Text(
                                    text = lesson.formula,
                                    color = GoldYellow,
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 13.sp
                                )
                            }

                            Text(
                                text = lesson.explanationVi,
                                color = TextPrimary,
                                fontSize = 13.sp,
                                maxLines = 2
                            )

                            Text(
                                text = "Ví dụ: ${lesson.exampleEn}",
                                color = SlateBlue,
                                fontSize = 12.sp
                            )
                        }
                    }
                }
            }

            2 -> {
                // Section 2: Teacher Assignments for this grade
                if (assignments.isEmpty()) {
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = DarkCard)
                        ) {
                            Column(
                                modifier = Modifier.padding(24.dp).fillMaxWidth(),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(Icons.Default.AssignmentLate, contentDescription = null, tint = SlateBlue, modifier = Modifier.size(40.dp))
                                Text("Chưa có bài tập nào cho Lớp $selectedGrade", color = TextPrimary, fontWeight = FontWeight.Bold)
                                Text("Hãy chuyển sang 'Nhánh Ra Đề GV' để tạo đề thi mới cho lớp này.", color = SlateBlue, fontSize = 12.sp)
                            }
                        }
                    }
                } else {
                    items(assignments) { assignment ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onSelectAssignmentToTake(assignment) }
                                .testTag("take_assignment_${assignment.id}"),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = DarkCard),
                            border = CardDefaults.outlinedCardBorder().copy(brush = Brush.horizontalGradient(listOf(PurpleNeon.copy(alpha = 0.5f), CyanAccent.copy(alpha = 0.5f))))
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(
                                    modifier = Modifier.weight(1f),
                                    verticalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Text(
                                        text = assignment.title,
                                        color = TextPrimary,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 15.sp
                                    )
                                    Text(
                                        text = assignment.description,
                                        color = SlateBlue,
                                        fontSize = 12.sp,
                                        maxLines = 1
                                    )
                                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                        Text("GV: ${assignment.teacherName}", color = CyanAccent, fontSize = 11.sp)
                                        Text("• ${assignment.questions.size} câu", color = GoldYellow, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                    }
                                }

                                Button(
                                    onClick = { onSelectAssignmentToTake(assignment) },
                                    colors = ButtonDefaults.buttonColors(containerColor = CyanAccent),
                                    shape = RoundedCornerShape(10.dp)
                                ) {
                                    Text("Làm Bài", color = Navy900, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Grammar Detail Dialog
    viewingGrammarLesson?.let { lesson ->
        AlertDialog(
            onDismissRequest = { viewingGrammarLesson = null },
            confirmButton = {
                TextButton(onClick = { viewingGrammarLesson = null }) {
                    Text("Đã Hiểu", color = CyanAccent, fontWeight = FontWeight.Bold)
                }
            },
            title = {
                Text(lesson.title, color = CyanAccent, fontWeight = FontWeight.Bold, fontSize = 18.sp)
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(Navy800)
                            .padding(10.dp)
                    ) {
                        Text(lesson.formula, color = GoldYellow, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    }

                    Text("Giải thích:", color = TextSecondary, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    Text(lesson.explanationVi, color = TextPrimary, fontSize = 14.sp)

                    Text("Ví dụ minh họa:", color = TextSecondary, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    Text("• ${lesson.exampleEn}", color = CyanAccent, fontWeight = FontWeight.Medium, fontSize = 13.sp)
                    Text("• ${lesson.exampleVi}", color = SlateBlue, fontSize = 12.sp)

                    if (lesson.usageNotes.isNotBlank()) {
                        Text("Lưu ý & Dấu hiệu nhận biết:", color = GoldYellow, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        Text(lesson.usageNotes, color = TextSecondary, fontSize = 12.sp)
                    }
                }
            },
            containerColor = DarkCard
        )
    }
}
