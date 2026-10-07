package com.taphunter.english.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.taphunter.english.data.models.CustomQuestion
import com.taphunter.english.data.models.GrammarLesson
import com.taphunter.english.data.models.TeacherAssignment
import com.taphunter.english.data.models.WordItem
import com.taphunter.english.data.repository.EnglishRepository
import com.taphunter.english.data.repository.ExamSource
import com.taphunter.english.data.repository.ReputableExamBank
import com.taphunter.english.ui.theme.*

@Composable
fun TeacherPortalScreen(
    englishRepository: EnglishRepository,
    teacherName: String,
    onSwitchToStudentRole: () -> Unit
) {
    val context = LocalContext.current
    var selectedTab by remember { mutableIntStateOf(0) } // 0: Ra Đề Thi, 1: Soạn Từ Mới, 2: Soạn Ngữ Pháp
    val assignments by englishRepository.assignments.collectAsState()
    val customWords by englishRepository.customWords.collectAsState()
    val customGrammar by englishRepository.customGrammar.collectAsState()

    var showCreateQuizDialog by remember { mutableStateOf(false) }
    var showGenerateExamDialog by remember { mutableStateOf(false) }
    var editingAssignment by remember { mutableStateOf<TeacherAssignment?>(null) }
    var deletingAssignment by remember { mutableStateOf<TeacherAssignment?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .testTag("teacher_portal_screen")
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Teacher Banner
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Navy800),
            border = CardDefaults.outlinedCardBorder().copy(brush = Brush.horizontalGradient(listOf(PurpleNeon, CyanAccent)))
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(46.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(PurpleNeon.copy(alpha = 0.25f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(imageVector = Icons.Default.School, contentDescription = null, tint = PurpleNeon, modifier = Modifier.size(28.dp))
                    }
                    Column {
                        Text(
                            text = "NHÁNH GIÁO VIÊN",
                            color = PurpleNeon,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.ExtraBold,
                            letterSpacing = 1.sp
                        )
                        Text(
                            text = teacherName.ifBlank { "Thầy/Cô Giáo" },
                            color = TextPrimary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 17.sp
                        )
                        Text(
                            text = "Đồng bộ đề thi & từ vựng Lớp 6 - 12 lên Cloud",
                            color = SlateBlue,
                            fontSize = 12.sp
                        )
                    }
                }

                OutlinedButton(
                    onClick = onSwitchToStudentRole,
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = CyanAccent),
                    modifier = Modifier.testTag("teacher_switch_student_role_button")
                ) {
                    Icon(Icons.Default.SwapHoriz, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Góc Học Sinh", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        // Functional Tabs: 0: Ra Đề Thi | 1: Soạn Từ Mới | 2: Soạn Ngữ Pháp
        TabRow(
            selectedTabIndex = selectedTab,
            containerColor = Navy800,
            contentColor = PurpleNeon,
            indicator = {},
            divider = {},
            modifier = Modifier
                .clip(RoundedCornerShape(12.dp))
                .background(Navy800)
        ) {
            Tab(
                selected = selectedTab == 0,
                onClick = { selectedTab = 0 },
                text = { Text("1. Ra Đề Thi (${assignments.size})", fontWeight = FontWeight.Bold, fontSize = 12.sp) }
            )
            Tab(
                selected = selectedTab == 1,
                onClick = { selectedTab = 1 },
                text = { Text("2. Soạn Từ Mới (${customWords.size})", fontWeight = FontWeight.Bold, fontSize = 12.sp) }
            )
            Tab(
                selected = selectedTab == 2,
                onClick = { selectedTab = 2 },
                text = { Text("3. Soạn Ngữ Pháp (${customGrammar.size})", fontWeight = FontWeight.Bold, fontSize = 12.sp) }
            )
        }

        // Tab Content
        when (selectedTab) {
            0 -> {
                // TAB 0: Ra Đề Thi cho học sinh
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Đề Thi Đã Giao (${assignments.size})",
                            color = TextPrimary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp
                        )
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            // Generate from Reputable Sources button
                            Button(
                                onClick = { showGenerateExamDialog = true },
                                colors = ButtonDefaults.buttonColors(containerColor = GoldYellow),
                                shape = RoundedCornerShape(10.dp),
                                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
                                modifier = Modifier.testTag("open_generate_exam_dialog_button")
                            ) {
                                Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = Navy900, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("✨ Sinh Đề Chuẩn QG", color = Navy900, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                            }

                            // Manual create button
                            Button(
                                onClick = { showCreateQuizDialog = true },
                                colors = ButtonDefaults.buttonColors(containerColor = CyanAccent),
                                shape = RoundedCornerShape(10.dp),
                                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
                                modifier = Modifier.testTag("open_create_quiz_dialog_button")
                            ) {
                                Icon(Icons.Default.Add, contentDescription = null, tint = Navy900, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Tạo Đề Mới", color = Navy900, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                            }
                        }
                    }

                    LazyColumn(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        items(assignments) { item ->
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("assignment_card_${item.id}"),
                                shape = RoundedCornerShape(14.dp),
                                colors = CardDefaults.cardColors(containerColor = DarkCard),
                                border = CardDefaults.outlinedCardBorder().copy(brush = Brush.horizontalGradient(listOf(DarkBorder, DarkBorder)))
                            ) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(14.dp),
                                    verticalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(6.dp))
                                                .background(PurpleNeon.copy(alpha = 0.2f))
                                                .padding(horizontal = 8.dp, vertical = 2.dp)
                                        ) {
                                            Text("Lớp ${item.grade}", color = PurpleNeon, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                                        }
                                        Text("${item.questions.size} câu hỏi", color = GoldYellow, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                    }

                                    Text(
                                        text = item.title,
                                        color = TextPrimary,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 15.sp
                                    )
                                    Text(
                                        text = item.description,
                                        color = SlateBlue,
                                        fontSize = 12.sp
                                    )
                                    Text(
                                        text = "Người ra đề: ${item.teacherName}",
                                        color = TextSecondary,
                                        fontSize = 11.sp
                                    )

                                    // Action buttons: Edit & Delete
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(top = 4.dp),
                                        horizontalArrangement = Arrangement.End,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        OutlinedButton(
                                            onClick = { editingAssignment = item },
                                            shape = RoundedCornerShape(8.dp),
                                            colors = ButtonDefaults.outlinedButtonColors(contentColor = CyanAccent),
                                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                                            modifier = Modifier.height(32.dp).testTag("edit_assignment_button_${item.id}")
                                        ) {
                                            Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(14.dp))
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text("Sửa Đề", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                        }

                                        Spacer(modifier = Modifier.width(8.dp))

                                        Button(
                                            onClick = { deletingAssignment = item },
                                            shape = RoundedCornerShape(8.dp),
                                            colors = ButtonDefaults.buttonColors(containerColor = RedDanger),
                                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                                            modifier = Modifier.height(32.dp).testTag("delete_assignment_button_${item.id}")
                                        ) {
                                            Icon(Icons.Default.Delete, contentDescription = null, tint = TextPrimary, modifier = Modifier.size(14.dp))
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text("Xoá Đề", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            1 -> {
                // TAB 1: Soạn Từ Mới cho học sinh
                AddVocabularySection(englishRepository, customWords)
            }

            2 -> {
                // TAB 2: Soạn Ngữ Pháp cho học sinh
                AddGrammarSection(englishRepository, customGrammar)
            }
        }
    }

    if (showCreateQuizDialog) {
        CreateQuizDialog(
            teacherName = teacherName,
            onDismiss = { showCreateQuizDialog = false },
            onSaveQuiz = { newAssignment ->
                englishRepository.addCustomAssignment(newAssignment)
                showCreateQuizDialog = false
                Toast.makeText(context, "Đã giao đề thi thành công cho Lớp ${newAssignment.grade}!", Toast.LENGTH_SHORT).show()
            }
        )
    }

    if (showGenerateExamDialog) {
        GenerateExamDialog(
            teacherName = teacherName,
            onDismiss = { showGenerateExamDialog = false },
            onGenerated = { generatedAssignment ->
                englishRepository.addCustomAssignment(generatedAssignment)
                showGenerateExamDialog = false
                Toast.makeText(context, "Đã sinh và phát hành '${generatedAssignment.title}' thành công!", Toast.LENGTH_SHORT).show()
            }
        )
    }

    editingAssignment?.let { assignmentToEdit ->
        EditQuizDialog(
            assignment = assignmentToEdit,
            onDismiss = { editingAssignment = null },
            onUpdateQuiz = { updatedAssignment ->
                englishRepository.updateCustomAssignment(updatedAssignment)
                editingAssignment = null
                Toast.makeText(context, "Đã cập nhật chỉnh sửa đề thi thành công!", Toast.LENGTH_SHORT).show()
            }
        )
    }

    deletingAssignment?.let { assignmentToDelete ->
        ConfirmDeleteDialog(
            assignment = assignmentToDelete,
            onDismiss = { deletingAssignment = null },
            onConfirmDelete = {
                englishRepository.deleteCustomAssignment(assignmentToDelete.id)
                deletingAssignment = null
                Toast.makeText(context, "Đã xoá đề thi '${assignmentToDelete.title}'!", Toast.LENGTH_SHORT).show()
            }
        )
    }
}

@Composable
private fun AddVocabularySection(
    englishRepository: EnglishRepository,
    customWords: List<WordItem>
) {
    val context = LocalContext.current
    var selectedGrade by remember { mutableIntStateOf(10) }
    var unitNumber by remember { mutableIntStateOf(1) }
    var wordEn by remember { mutableStateOf("") }
    var phonetic by remember { mutableStateOf("") }
    var meaningVi by remember { mutableStateOf("") }
    var exampleEn by remember { mutableStateOf("") }
    var exampleVi by remember { mutableStateOf("") }
    var distractor1 by remember { mutableStateOf("") }
    var distractor2 by remember { mutableStateOf("") }
    var distractor3 by remember { mutableStateOf("") }

    val scrollState = rememberScrollState()
    val wordsForGrade = customWords.filter { it.grade == selectedGrade }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text("Thêm Từ Mới Cho Bài Học (Lớp 6 đến 12)", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 16.sp)

        // Grade selector (Grades 6-12 fully selectable)
        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Text("Chọn khối lớp áp dụng:", color = TextSecondary, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(listOf(6, 7, 8, 9, 10, 11, 12)) { g ->
                    val isSelected = selectedGrade == g
                    FilterChip(
                        selected = isSelected,
                        onClick = { selectedGrade = g },
                        label = { Text("Lớp $g", fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal, fontSize = 13.sp) },
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
                        modifier = Modifier.testTag("teacher_select_grade_$g")
                    )
                }
            }
        }

        // Unit selector
        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Text("Chọn bài học (Unit):", color = TextSecondary, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(listOf(1, 2, 3, 4, 5, 0)) { u ->
                    val isSelected = unitNumber == u
                    val label = if (u == 0) "Unit Mới (GV)" else "Unit $u"
                    FilterChip(
                        selected = isSelected,
                        onClick = { unitNumber = u },
                        label = { Text(label, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal, fontSize = 12.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = GoldYellow,
                            selectedLabelColor = Navy900,
                            containerColor = DarkCard,
                            labelColor = TextPrimary
                        ),
                        border = FilterChipDefaults.filterChipBorder(
                            enabled = true,
                            selected = isSelected,
                            borderColor = if (isSelected) GoldYellow else DarkBorder
                        )
                    )
                }
            }
        }

        OutlinedTextField(
            value = wordEn,
            onValueChange = { wordEn = it },
            label = { Text("Từ tiếng Anh (VD: Inevitable, Resilience)") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth().testTag("teacher_input_word"),
            shape = RoundedCornerShape(12.dp)
        )

        OutlinedTextField(
            value = phonetic,
            onValueChange = { phonetic = it },
            label = { Text("Phiên âm IPA (VD: /ɪˈnev.ɪ.tə.bəl/)") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth().testTag("teacher_input_phonetic"),
            shape = RoundedCornerShape(12.dp)
        )

        OutlinedTextField(
            value = meaningVi,
            onValueChange = { meaningVi = it },
            label = { Text("Nghĩa tiếng Việt (VD: Không thể tránh khỏi)") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth().testTag("teacher_input_meaning"),
            shape = RoundedCornerShape(12.dp)
        )

        OutlinedTextField(
            value = exampleEn,
            onValueChange = { exampleEn = it },
            label = { Text("Câu ví dụ tiếng Anh") },
            modifier = Modifier.fillMaxWidth().testTag("teacher_input_example_en"),
            shape = RoundedCornerShape(12.dp)
        )

        OutlinedTextField(
            value = exampleVi,
            onValueChange = { exampleVi = it },
            label = { Text("Dịch câu ví dụ tiếng Việt") },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp)
        )

        Text("3 Đáp án nhiễu (dùng trong game Tap Hunter):", color = SlateBlue, fontSize = 12.sp)

        OutlinedTextField(
            value = distractor1,
            onValueChange = { distractor1 = it },
            label = { Text("Đáp án sai 1") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(10.dp)
        )

        OutlinedTextField(
            value = distractor2,
            onValueChange = { distractor2 = it },
            label = { Text("Đáp án sai 2") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(10.dp)
        )

        OutlinedTextField(
            value = distractor3,
            onValueChange = { distractor3 = it },
            label = { Text("Đáp án sai 3") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(10.dp)
        )

        Button(
            onClick = {
                if (wordEn.isBlank() || meaningVi.isBlank()) {
                    Toast.makeText(context, "Vui lòng nhập từ tiếng Anh và nghĩa tiếng Việt!", Toast.LENGTH_SHORT).show()
                    return@Button
                }
                val distractors = listOf(
                    distractor1.ifBlank { "Đáp án khác 1" },
                    distractor2.ifBlank { "Đáp án khác 2" },
                    distractor3.ifBlank { "Đáp án khác 3" }
                )
                englishRepository.addCustomWord(
                    grade = selectedGrade,
                    unitNumber = unitNumber,
                    word = wordEn,
                    phonetic = phonetic.ifBlank { "/.../" },
                    meaningVi = meaningVi,
                    exampleEn = exampleEn,
                    exampleVi = exampleVi,
                    distractors = distractors
                )
                Toast.makeText(context, "Đã lưu từ \"$wordEn\" vào Lớp $selectedGrade thành công!", Toast.LENGTH_SHORT).show()
                wordEn = ""
                phonetic = ""
                meaningVi = ""
                exampleEn = ""
                exampleVi = ""
                distractor1 = ""
                distractor2 = ""
                distractor3 = ""
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .testTag("save_custom_word_button"),
            colors = ButtonDefaults.buttonColors(containerColor = CyanAccent),
            shape = RoundedCornerShape(12.dp)
        ) {
            Icon(Icons.Default.Save, contentDescription = null, tint = Navy900)
            Spacer(modifier = Modifier.width(6.dp))
            Text("Lưu Từ Mới Vào Lớp $selectedGrade", color = Navy900, fontWeight = FontWeight.Bold)
        }

        // List of custom words already added for this grade
        if (wordsForGrade.isNotEmpty()) {
            Spacer(modifier = Modifier.height(10.dp))
            Text("Từ vựng GV đã thêm cho Lớp $selectedGrade (${wordsForGrade.size}):", color = GoldYellow, fontWeight = FontWeight.Bold, fontSize = 14.sp)
            wordsForGrade.forEach { w ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    colors = CardDefaults.cardColors(containerColor = DarkCard)
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp).fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("${w.word} ${w.phonetic}", color = CyanAccent, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            Text(w.meaningVi, color = TextPrimary, fontSize = 12.sp)
                            if (w.exampleEn.isNotBlank()) {
                                Text(w.exampleEn, color = SlateBlue, fontSize = 11.sp)
                            }
                        }
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(Navy700)
                                .padding(horizontal = 8.dp, vertical = 2.dp)
                        ) {
                            Text(if (w.unitNumber == 0) "Unit GV" else "Unit ${w.unitNumber}", color = GoldYellow, fontSize = 11.sp)
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))
    }
}

@Composable
private fun AddGrammarSection(
    englishRepository: EnglishRepository,
    customGrammar: List<GrammarLesson>
) {
    val context = LocalContext.current
    var selectedGrade by remember { mutableIntStateOf(10) }
    var title by remember { mutableStateOf("") }
    var formula by remember { mutableStateOf("") }
    var explanation by remember { mutableStateOf("") }
    var exampleEn by remember { mutableStateOf("") }
    var exampleVi by remember { mutableStateOf("") }
    var usageNotes by remember { mutableStateOf("") }

    val scrollState = rememberScrollState()
    val grammarForGrade = customGrammar.filter { it.grade == selectedGrade }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text("Soạn Chuyên Đề Ngữ Pháp (Lớp 6 đến 12)", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 16.sp)

        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Text("Chọn khối lớp áp dụng:", color = TextSecondary, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(listOf(6, 7, 8, 9, 10, 11, 12)) { g ->
                    val isSelected = selectedGrade == g
                    FilterChip(
                        selected = isSelected,
                        onClick = { selectedGrade = g },
                        label = { Text("Lớp $g", fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal, fontSize = 13.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = PurpleNeon,
                            selectedLabelColor = TextPrimary,
                            containerColor = DarkCard,
                            labelColor = TextPrimary
                        ),
                        border = FilterChipDefaults.filterChipBorder(
                            enabled = true,
                            selected = isSelected,
                            borderColor = if (isSelected) PurpleNeon else DarkBorder
                        )
                    )
                }
            }
        }

        OutlinedTextField(
            value = title,
            onValueChange = { title = it },
            label = { Text("Tên chủ điểm ngữ pháp (VD: Câu điều kiện hỗn hợp)") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth().testTag("grammar_title_input"),
            shape = RoundedCornerShape(12.dp)
        )

        OutlinedTextField(
            value = formula,
            onValueChange = { formula = it },
            label = { Text("Công thức ngữ pháp (VD: If + S + had V3, S + would V)") },
            modifier = Modifier.fillMaxWidth().testTag("grammar_formula_input"),
            shape = RoundedCornerShape(12.dp)
        )

        OutlinedTextField(
            value = explanation,
            onValueChange = { explanation = it },
            label = { Text("Giải thích cách dùng chi tiết") },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp)
        )

        OutlinedTextField(
            value = exampleEn,
            onValueChange = { exampleEn = it },
            label = { Text("Ví dụ minh họa tiếng Anh") },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp)
        )

        OutlinedTextField(
            value = exampleVi,
            onValueChange = { exampleVi = it },
            label = { Text("Dịch nghĩa ví dụ tiếng Việt") },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp)
        )

        OutlinedTextField(
            value = usageNotes,
            onValueChange = { usageNotes = it },
            label = { Text("Lưu ý / Mẹo làm bài thi HSG & THPT") },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp)
        )

        Button(
            onClick = {
                if (title.isBlank() || formula.isBlank()) {
                    Toast.makeText(context, "Vui lòng nhập tên chuyên đề và công thức!", Toast.LENGTH_SHORT).show()
                    return@Button
                }
                val lesson = GrammarLesson(
                    id = "grammar_custom_${System.currentTimeMillis()}",
                    grade = selectedGrade,
                    title = title,
                    formula = formula,
                    explanationVi = explanation,
                    exampleEn = exampleEn,
                    exampleVi = exampleVi,
                    usageNotes = usageNotes
                )
                englishRepository.addCustomGrammar(lesson)
                Toast.makeText(context, "Đã lưu bài giảng ngữ pháp cho Lớp $selectedGrade!", Toast.LENGTH_SHORT).show()
                title = ""
                formula = ""
                explanation = ""
                exampleEn = ""
                exampleVi = ""
                usageNotes = ""
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .testTag("save_custom_grammar_button"),
            colors = ButtonDefaults.buttonColors(containerColor = PurpleNeon),
            shape = RoundedCornerShape(12.dp)
        ) {
            Icon(Icons.Default.Check, contentDescription = null, tint = TextPrimary)
            Spacer(modifier = Modifier.width(6.dp))
            Text("Lưu Chuyên Đề Cho Lớp $selectedGrade", color = TextPrimary, fontWeight = FontWeight.Bold)
        }

        // List of custom grammar already added for this grade
        if (grammarForGrade.isNotEmpty()) {
            Spacer(modifier = Modifier.height(10.dp))
            Text("Chuyên đề GV đã thêm cho Lớp $selectedGrade (${grammarForGrade.size}):", color = PurpleNeon, fontWeight = FontWeight.Bold, fontSize = 14.sp)
            grammarForGrade.forEach { g ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    colors = CardDefaults.cardColors(containerColor = DarkCard)
                ) {
                    Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text(g.title, color = PurpleNeon, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        Text(g.formula, color = GoldYellow, fontSize = 12.sp)
                        if (g.exampleEn.isNotBlank()) {
                            Text(g.exampleEn, color = SlateBlue, fontSize = 11.sp)
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))
    }
}

@Composable
private fun CreateQuizDialog(
    teacherName: String,
    onDismiss: () -> Unit,
    onSaveQuiz: (TeacherAssignment) -> Unit
) {
    var quizTitle by remember { mutableStateOf("") }
    var quizGrade by remember { mutableIntStateOf(10) }
    var quizDescription by remember { mutableStateOf("") }

    // First question fields
    var qText by remember { mutableStateOf("") }
    var optA by remember { mutableStateOf("") }
    var optB by remember { mutableStateOf("") }
    var optC by remember { mutableStateOf("") }
    var optD by remember { mutableStateOf("") }
    var correctIndex by remember { mutableIntStateOf(0) }
    var explanation by remember { mutableStateOf("") }

    val scrollState = rememberScrollState()

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            Button(
                onClick = {
                    if (quizTitle.isBlank() || qText.isBlank()) return@Button
                    val question = CustomQuestion(
                        id = "q_${System.currentTimeMillis()}",
                        question = qText,
                        options = listOf(
                            optA.ifBlank { "Lựa chọn A" },
                            optB.ifBlank { "Lựa chọn B" },
                            optC.ifBlank { "Lựa chọn C" },
                            optD.ifBlank { "Lựa chọn D" }
                        ),
                        correctIndex = correctIndex,
                        explanation = explanation.ifBlank { "Đáp án chính xác là phương án đã chọn." }
                    )
                    val assignment = TeacherAssignment(
                        id = "assign_${System.currentTimeMillis()}",
                        teacherName = teacherName.ifBlank { "Giáo viên bộ môn" },
                        grade = quizGrade,
                        title = quizTitle,
                        description = quizDescription.ifBlank { "Bài kiểm tra củng cố kiến thức theo lớp." },
                        questions = listOf(question)
                    )
                    onSaveQuiz(assignment)
                },
                colors = ButtonDefaults.buttonColors(containerColor = CyanAccent),
                modifier = Modifier.testTag("confirm_create_quiz_button")
            ) {
                Text("Phát Hành Cho Học Sinh", color = Navy900, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Hủy", color = SlateBlue)
            }
        },
        title = {
            Text("Tạo Đề Thi / Bài Tập Mới", color = TextPrimary, fontWeight = FontWeight.Bold)
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(scrollState),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedTextField(
                    value = quizTitle,
                    onValueChange = { quizTitle = it },
                    label = { Text("Tiêu đề bài thi (VD: Kiểm tra 15p Unit 1)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("new_quiz_title_input")
                )

                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text("Giao cho lớp:", color = TextSecondary, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        items(listOf(6, 7, 8, 9, 10, 11, 12)) { g ->
                            val isSelected = quizGrade == g
                            FilterChip(
                                selected = isSelected,
                                onClick = { quizGrade = g },
                                label = { Text("Lớp $g", fontSize = 12.sp, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = CyanAccent,
                                    selectedLabelColor = Navy900,
                                    containerColor = Navy800,
                                    labelColor = TextPrimary
                                )
                            )
                        }
                    }
                }

                OutlinedTextField(
                    value = quizDescription,
                    onValueChange = { quizDescription = it },
                    label = { Text("Ghi chú / Hướng dẫn học sinh") },
                    modifier = Modifier.fillMaxWidth()
                )

                HorizontalDivider(color = DarkBorder)
                Text("Câu hỏi số 1:", color = GoldYellow, fontWeight = FontWeight.Bold, fontSize = 13.sp)

                OutlinedTextField(
                    value = qText,
                    onValueChange = { qText = it },
                    label = { Text("Nội dung câu hỏi trắc nghiệm") },
                    modifier = Modifier.fillMaxWidth().testTag("new_question_text_input")
                )

                OutlinedTextField(
                    value = optA,
                    onValueChange = { optA = it },
                    label = { Text("Đáp án A") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = optB,
                    onValueChange = { optB = it },
                    label = { Text("Đáp án B") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = optC,
                    onValueChange = { optC = it },
                    label = { Text("Đáp án C") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = optD,
                    onValueChange = { optD = it },
                    label = { Text("Đáp án D") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Text("Đáp án đúng là:", color = TextSecondary, fontSize = 12.sp)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    listOf("A", "B", "C", "D").forEachIndexed { idx, letter ->
                        FilterChip(
                            selected = correctIndex == idx,
                            onClick = { correctIndex = idx },
                            label = { Text(letter, fontWeight = FontWeight.Bold) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = GreenSuccess,
                                selectedLabelColor = Navy900
                            )
                        )
                    }
                }

                OutlinedTextField(
                    value = explanation,
                    onValueChange = { explanation = it },
                    label = { Text("Lời giải chi tiết cho học sinh") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        containerColor = DarkCard
    )
}

@Composable
private fun ConfirmDeleteDialog(
    assignment: TeacherAssignment,
    onDismiss: () -> Unit,
    onConfirmDelete: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Icon(Icons.Default.Warning, contentDescription = null, tint = RedDanger)
                Text("Xác Nhận Xoá Đề Thi", color = TextPrimary, fontWeight = FontWeight.Bold)
            }
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "Thầy/Cô có chắc chắn muốn xoá đề thi này khỏi hệ thống không?",
                    color = TextPrimary,
                    fontSize = 14.sp
                )
                Card(
                    colors = CardDefaults.cardColors(containerColor = Navy800),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text(assignment.title, color = GoldYellow, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        Text("Khối Lớp ${assignment.grade} • ${assignment.questions.size} câu hỏi", color = SlateBlue, fontSize = 12.sp)
                    }
                }
                Text(
                    text = "Lưu ý: Học sinh sẽ không thể tiếp tục làm đề thi này sau khi xoá.",
                    color = RedDanger,
                    fontSize = 11.sp
                )
            }
        },
        confirmButton = {
            Button(
                onClick = onConfirmDelete,
                colors = ButtonDefaults.buttonColors(containerColor = RedDanger),
                modifier = Modifier.testTag("confirm_delete_assignment_button")
            ) {
                Text("Xác Nhận Xoá", color = TextPrimary, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Hủy Bỏ", color = SlateBlue)
            }
        },
        containerColor = DarkCard
    )
}

@Composable
private fun EditQuizDialog(
    assignment: TeacherAssignment,
    onDismiss: () -> Unit,
    onUpdateQuiz: (TeacherAssignment) -> Unit
) {
    var quizTitle by remember { mutableStateOf(assignment.title) }
    var quizGrade by remember { mutableIntStateOf(assignment.grade) }
    var quizDescription by remember { mutableStateOf(assignment.description) }

    // Mutable list of questions
    var questions by remember { mutableStateOf(assignment.questions) }

    val scrollState = rememberScrollState()

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Icon(Icons.Default.Edit, contentDescription = null, tint = CyanAccent)
                Text("Chỉnh Sửa Đề Thi", color = TextPrimary, fontWeight = FontWeight.Bold)
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(scrollState),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedTextField(
                    value = quizTitle,
                    onValueChange = { quizTitle = it },
                    label = { Text("Tiêu đề đề thi *") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("edit_quiz_title_input")
                )

                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text("Khối lớp áp dụng:", color = TextSecondary, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        items(listOf(6, 7, 8, 9, 10, 11, 12)) { g ->
                            val isSelected = quizGrade == g
                            FilterChip(
                                selected = isSelected,
                                onClick = { quizGrade = g },
                                label = { Text("Lớp $g", fontSize = 12.sp, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = CyanAccent,
                                    selectedLabelColor = Navy900,
                                    containerColor = Navy800,
                                    labelColor = TextPrimary
                                )
                            )
                        }
                    }
                }

                OutlinedTextField(
                    value = quizDescription,
                    onValueChange = { quizDescription = it },
                    label = { Text("Ghi chú / Hướng dẫn") },
                    modifier = Modifier.fillMaxWidth()
                )

                HorizontalDivider(color = DarkBorder)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Danh sách câu hỏi (${questions.size}):", color = GoldYellow, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    TextButton(onClick = {
                        val newQ = CustomQuestion(
                            id = "q_${System.currentTimeMillis()}",
                            question = "Câu hỏi mới cần soạn...",
                            options = listOf("Đáp án A", "Đáp án B", "Đáp án C", "Đáp án D"),
                            correctIndex = 0,
                            explanation = "Giải thích đáp án chi tiết."
                        )
                        questions = questions + newQ
                    }) {
                        Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Thêm Câu Hỏi", fontSize = 11.sp, color = CyanAccent)
                    }
                }

                questions.forEachIndexed { qIndex, q ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = Navy800)
                    ) {
                        Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("Câu ${qIndex + 1}:", color = CyanAccent, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                if (questions.size > 1) {
                                    IconButton(
                                        onClick = { questions = questions.filterIndexed { idx, _ -> idx != qIndex } },
                                        modifier = Modifier.size(24.dp)
                                    ) {
                                        Icon(Icons.Default.Delete, contentDescription = "Xoá câu", tint = RedDanger, modifier = Modifier.size(16.dp))
                                    }
                                }
                            }

                            var qText by remember { mutableStateOf(q.question) }
                            OutlinedTextField(
                                value = qText,
                                onValueChange = {
                                    qText = it
                                    questions = questions.mapIndexed { idx, item -> if (idx == qIndex) item.copy(question = it) else item }
                                },
                                label = { Text("Nội dung câu hỏi") },
                                modifier = Modifier.fillMaxWidth()
                            )

                            // 4 Options
                            q.options.forEachIndexed { optIndex, optText ->
                                val letter = when (optIndex) { 0 -> "A"; 1 -> "B"; 2 -> "C"; else -> "D" }
                                var optVal by remember { mutableStateOf(optText) }
                                OutlinedTextField(
                                    value = optVal,
                                    onValueChange = {
                                        optVal = it
                                        val newOpts = q.options.toMutableList()
                                        newOpts[optIndex] = it
                                        questions = questions.mapIndexed { idx, item -> if (idx == qIndex) item.copy(options = newOpts) else item }
                                    },
                                    label = { Text("Lựa chọn $letter") },
                                    singleLine = true,
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }

                            Text("Đáp án đúng:", color = TextSecondary, fontSize = 11.sp)
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceAround) {
                                listOf("A", "B", "C", "D").forEachIndexed { idx, letter ->
                                    FilterChip(
                                        selected = q.correctIndex == idx,
                                        onClick = {
                                            questions = questions.mapIndexed { qIdx, item -> if (qIdx == qIndex) item.copy(correctIndex = idx) else item }
                                        },
                                        label = { Text(letter, fontWeight = FontWeight.Bold) },
                                        colors = FilterChipDefaults.filterChipColors(
                                            selectedContainerColor = GreenSuccess,
                                            selectedLabelColor = Navy900
                                        )
                                    )
                                }
                            }

                            var expText by remember { mutableStateOf(q.explanation) }
                            OutlinedTextField(
                                value = expText,
                                onValueChange = {
                                    expText = it
                                    questions = questions.mapIndexed { idx, item -> if (idx == qIndex) item.copy(explanation = it) else item }
                                },
                                label = { Text("Lời giải chi tiết") },
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (quizTitle.isBlank() || questions.isEmpty()) return@Button
                    val updated = assignment.copy(
                        title = quizTitle.trim(),
                        grade = quizGrade,
                        description = quizDescription.trim(),
                        questions = questions
                    )
                    onUpdateQuiz(updated)
                },
                colors = ButtonDefaults.buttonColors(containerColor = CyanAccent),
                modifier = Modifier.testTag("confirm_update_quiz_button")
            ) {
                Text("Lưu Thay Đổi", color = Navy900, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Hủy", color = SlateBlue)
            }
        },
        containerColor = DarkCard
    )
}

@Composable
private fun GenerateExamDialog(
    teacherName: String,
    onDismiss: () -> Unit,
    onGenerated: (TeacherAssignment) -> Unit
) {
    var selectedSource by remember { mutableStateOf(ExamSource.THPT_QUOC_GIA) }
    var selectedGrade by remember { mutableIntStateOf(10) }
    var questionCount by remember { mutableIntStateOf(5) }

    val scrollState = rememberScrollState()

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = GoldYellow)
                Text("Sinh Đề Thi Chuẩn Quốc Gia", color = TextPrimary, fontWeight = FontWeight.Bold)
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(scrollState),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "Trích xuất câu hỏi chuẩn hóa từ các nguồn khảo thí uy tín:",
                    color = TextSecondary,
                    fontSize = 12.sp
                )

                // Exam sources selector
                ExamSource.entries.forEach { src ->
                    val isSelected = selectedSource == src
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { selectedSource = src }
                            .testTag("source_option_${src.name}"),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (isSelected) Navy700 else Navy800
                        ),
                        border = if (isSelected) {
                            CardDefaults.outlinedCardBorder().copy(brush = Brush.horizontalGradient(listOf(GoldYellow, CyanAccent)))
                        } else null
                    ) {
                        Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(src.badge, color = if (isSelected) GoldYellow else SlateBlue, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                                if (isSelected) {
                                    Icon(Icons.Default.CheckCircle, contentDescription = null, tint = GoldYellow, modifier = Modifier.size(16.dp))
                                }
                            }
                            Text(src.sourceName, color = TextPrimary, fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                            Text(src.description, color = SlateBlue, fontSize = 11.sp, lineHeight = 15.sp)
                        }
                    }
                }

                // Grade Selector
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text("Áp dụng cho khối lớp:", color = TextSecondary, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        items(listOf(6, 7, 8, 9, 10, 11, 12)) { g ->
                            val isSelected = selectedGrade == g
                            FilterChip(
                                selected = isSelected,
                                onClick = { selectedGrade = g },
                                label = { Text("Lớp $g", fontSize = 12.sp, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = CyanAccent,
                                    selectedLabelColor = Navy900,
                                    containerColor = Navy800,
                                    labelColor = TextPrimary
                                )
                            )
                        }
                    }
                }

                // Question count selector
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text("Số lượng câu hỏi trong đề:", color = TextSecondary, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        listOf(5, 10, 15).forEach { count ->
                            val isSelected = questionCount == count
                            FilterChip(
                                selected = isSelected,
                                onClick = { questionCount = count },
                                label = { Text("$count câu", fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = GoldYellow,
                                    selectedLabelColor = Navy900,
                                    containerColor = Navy800,
                                    labelColor = TextPrimary
                                )
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val exam = ReputableExamBank.generateReputableExam(
                        grade = selectedGrade,
                        source = selectedSource,
                        questionCount = questionCount,
                        teacherName = teacherName
                    )
                    onGenerated(exam)
                },
                colors = ButtonDefaults.buttonColors(containerColor = GoldYellow),
                modifier = Modifier.testTag("confirm_generate_exam_button")
            ) {
                Icon(Icons.Default.Bolt, contentDescription = null, tint = Navy900)
                Spacer(modifier = Modifier.width(4.dp))
                Text("Sinh Đề & Giao Ngay", color = Navy900, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Hủy", color = SlateBlue)
            }
        },
        containerColor = DarkCard
    )
}
