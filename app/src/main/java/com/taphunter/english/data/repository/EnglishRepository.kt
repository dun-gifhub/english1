package com.taphunter.english.data.repository

import android.content.Context
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.SetOptions
import com.taphunter.english.R
import com.taphunter.english.data.models.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.text.Normalizer
import java.util.regex.Pattern

class EnglishRepository(
    private val db: FirebaseFirestore
) {
    constructor(context: Context) : this(
        FirebaseFirestore.getInstance(
            context.applicationContext.getString(R.string.firestore_database_id)
        )
    )

    private val auth = Firebase.auth

    // Default seeded assignments
    private val defaultAssignments = listOf(
        TeacherAssignment(
            id = "seed_assign_1",
            teacherUid = "teacher_seed_1",
            teacherName = "Cô Nguyễn Mai Lan (THPT Chuyên)",
            grade = 10,
            title = "Kiểm tra 15p Từ vựng & Ngữ pháp Unit 1-2",
            description = "Ôn tập Family Life và Humans & Environment cho học sinh lớp 10",
            questions = listOf(
                CustomQuestion(
                    id = "q1",
                    question = "My father is the ________; he works hard to earn money for our family.",
                    options = listOf("breadwinner", "homemaker", "chore", "routine"),
                    correctIndex = 0,
                    explanation = "'Breadwinner' nghĩa là trụ cột gia đình, người kiếm thu nhập chính."
                ),
                CustomQuestion(
                    id = "q2",
                    question = "We should adopt an ________ lifestyle to protect the environment.",
                    options = listOf("eco-friendly", "destructive", "harmful", "ordinary"),
                    correctIndex = 0,
                    explanation = "'Eco-friendly' có nghĩa là thân thiện với môi trường."
                ),
                CustomQuestion(
                    id = "q3",
                    question = "Look! The children ________ football in the school playground.",
                    options = listOf("play", "are playing", "played", "have played"),
                    correctIndex = 1,
                    explanation = "Có từ nhận biết 'Look!' nên chia ở thì hiện tại tiếp diễn (are playing)."
                )
            )
        ),
        TeacherAssignment(
            id = "seed_assign_2",
            teacherUid = "teacher_seed_2",
            teacherName = "Thầy Trần Đức Thắng (HSG Quốc Gia)",
            grade = 11,
            title = "Khảo sát Từ vựng Unit 1-2: Healthy Life & Generation Gap",
            description = "Kiểm tra kiến thức từ vựng nâng cao và mạo từ Lớp 11",
            questions = listOf(
                CustomQuestion(
                    id = "q4",
                    question = "Regular exercise strengthens the ________ system against viruses.",
                    options = listOf("immune", "digestive", "nervous", "circulatory"),
                    correctIndex = 0,
                    explanation = "'Immune system' là hệ miễn dịch bảo vệ cơ thể."
                ),
                CustomQuestion(
                    id = "q5",
                    question = "Her parents set a strict 10 PM ________ for every weekday.",
                    options = listOf("curfew", "deadline", "timetable", "schedule"),
                    correctIndex = 0,
                    explanation = "'Curfew' nghĩa là giờ giới nghiêm ban đêm."
                )
            )
        ),
        TeacherAssignment(
            id = "seed_assign_3",
            teacherUid = "teacher_seed_3",
            teacherName = "Thầy Lê Văn Hoàng",
            grade = 12,
            title = "Luyện đề HSG & Ôn Thi Tốt Nghiệp: Advanced Vocab & Inversion",
            description = "Bộ câu hỏi trắc nghiệm từ vựng nâng cao kỳ thi Chuyên & HSG",
            questions = listOf(
                CustomQuestion(
                    id = "q6",
                    question = "The professor gave a ________ explanation that everyone could easily understand.",
                    options = listOf("lucid", "ambiguous", "vague", "ephemeral"),
                    correctIndex = 0,
                    explanation = "'Lucid' nghĩa là rõ ràng, minh bạch, dễ hiểu."
                ),
                CustomQuestion(
                    id = "q7",
                    question = "Seldom ________ such a brilliant performance in English debate.",
                    options = listOf("I have seen", "have I seen", "did I saw", "I saw"),
                    correctIndex = 1,
                    explanation = "Đảo ngữ với phó từ phủ định 'Seldom': Seldom + have + S + V3 (have I seen)."
                )
            )
        )
    )

    private val _assignments = MutableStateFlow<List<TeacherAssignment>>(defaultAssignments)
    val assignments: StateFlow<List<TeacherAssignment>> = _assignments.asStateFlow()

    private val _customWords = MutableStateFlow<List<WordItem>>(emptyList())
    val customWords: StateFlow<List<WordItem>> = _customWords.asStateFlow()

    private val _customGrammar = MutableStateFlow<List<GrammarLesson>>(emptyList())
    val customGrammar: StateFlow<List<GrammarLesson>> = _customGrammar.asStateFlow()

    private val _registeredUsers = MutableStateFlow<List<UserProfile>>(emptyList())
    val registeredUsers: StateFlow<List<UserProfile>> = _registeredUsers.asStateFlow()

    private var assignmentsListener: ListenerRegistration? = null
    private var wordsListener: ListenerRegistration? = null
    private var grammarListener: ListenerRegistration? = null
    private var usersListener: ListenerRegistration? = null

    init {
        // Start listening to Firestore collections when authenticated
        auth.addAuthStateListener { firebaseAuth ->
            if (firebaseAuth.currentUser != null) {
                attachFirestoreListeners()
            } else {
                detachFirestoreListeners()
            }
        }
        if (auth.currentUser != null) {
            attachFirestoreListeners()
        }
    }

    private fun attachFirestoreListeners() {
        detachFirestoreListeners()

        // 1. Observe Assignments
        val assignRef = db.collection("assignments")
        assignmentsListener = assignRef.addSnapshotListener { snapshot, error ->
            if (error != null) {
                handleFirestoreError(error, OperationType.LIST, assignRef.path)
                return@addSnapshotListener
            }
            if (snapshot != null) {
                val firestoreList = snapshot.documents.mapNotNull { doc ->
                    try {
                        val questionsRaw = doc.get("questions") as? List<*>
                        val questions = questionsRaw?.mapNotNull { q ->
                            val qMap = q as? Map<*, *> ?: return@mapNotNull null
                            val optsRaw = qMap["options"] as? List<*>
                            val opts = optsRaw?.map { it.toString() } ?: emptyList()
                            CustomQuestion(
                                id = qMap["id"]?.toString() ?: "",
                                question = qMap["question"]?.toString() ?: "",
                                options = opts,
                                correctIndex = (qMap["correctIndex"] as? Number)?.toInt() ?: 0,
                                explanation = qMap["explanation"]?.toString() ?: ""
                            )
                        } ?: emptyList()

                        TeacherAssignment(
                            id = doc.getString("id") ?: doc.id,
                            teacherUid = doc.getString("teacherUid") ?: "",
                            teacherName = doc.getString("teacherName") ?: "Thầy Cô",
                            grade = doc.getLong("grade")?.toInt() ?: 10,
                            title = doc.getString("title") ?: "",
                            description = doc.getString("description") ?: "",
                            questions = questions,
                            createdAt = doc.getTimestamp("createdAt")?.toDate()?.time ?: System.currentTimeMillis()
                        )
                    } catch (_: Exception) {
                        null
                    }
                }
                _assignments.value = (firestoreList + defaultAssignments).distinctBy { it.id }
            }
        }

        // 2. Observe Custom Words
        val wordsRef = db.collection("customWords")
        wordsListener = wordsRef.addSnapshotListener { snapshot, error ->
            if (error != null) {
                handleFirestoreError(error, OperationType.LIST, wordsRef.path)
                return@addSnapshotListener
            }
            if (snapshot != null) {
                val wordsList = snapshot.documents.mapNotNull { doc ->
                    try {
                        val distractorsRaw = doc.get("distractorsVi") as? List<*>
                        val distractors = distractorsRaw?.map { it.toString() } ?: emptyList()
                        WordItem(
                            id = doc.getString("id") ?: doc.id,
                            word = doc.getString("word") ?: "",
                            phonetic = doc.getString("phonetic") ?: "",
                            meaningVi = doc.getString("meaningVi") ?: "",
                            exampleEn = doc.getString("exampleEn") ?: "",
                            exampleVi = doc.getString("exampleVi") ?: "",
                            grade = doc.getLong("grade")?.toInt() ?: 10,
                            unitNumber = doc.getLong("unitNumber")?.toInt() ?: 1,
                            distractorsVi = distractors
                        )
                    } catch (_: Exception) {
                        null
                    }
                }
                _customWords.value = wordsList
            }
        }

        // 3. Observe Custom Grammar
        val grammarRef = db.collection("customGrammar")
        grammarListener = grammarRef.addSnapshotListener { snapshot, error ->
            if (error != null) {
                handleFirestoreError(error, OperationType.LIST, grammarRef.path)
                return@addSnapshotListener
            }
            if (snapshot != null) {
                val grammarList = snapshot.documents.mapNotNull { doc ->
                    try {
                        GrammarLesson(
                            id = doc.getString("id") ?: doc.id,
                            grade = doc.getLong("grade")?.toInt() ?: 10,
                            title = doc.getString("title") ?: "",
                            formula = doc.getString("formula") ?: "",
                            explanationVi = doc.getString("explanationVi") ?: "",
                            exampleEn = doc.getString("exampleEn") ?: "",
                            exampleVi = doc.getString("exampleVi") ?: "",
                            usageNotes = doc.getString("usageNotes") ?: ""
                        )
                    } catch (_: Exception) {
                        null
                    }
                }
                _customGrammar.value = grammarList
            }
        }

        // 4. Observe Users for Real-Time Monthly & All-Time Leaderboard
        val usersRef = db.collection("users")
        usersListener = usersRef.addSnapshotListener { snapshot, error ->
            if (error != null) {
                return@addSnapshotListener
            }
            if (snapshot != null) {
                val cal = java.util.Calendar.getInstance()
                val currentMonth = String.format(java.util.Locale.US, "%04d-%02d", cal.get(java.util.Calendar.YEAR), cal.get(java.util.Calendar.MONTH) + 1)

                val userList = snapshot.documents.mapNotNull { doc ->
                    try {
                        val uid = doc.id
                        val name = doc.getString("displayName") ?: "Học sinh"
                        val customClass = doc.getString("customClassName") ?: "10A1"
                        val baseGrade = doc.getLong("baseGrade")?.toInt() ?: 10
                        val regYear = doc.getLong("registeredAcademicYear")?.toInt() ?: 2026
                        val xp = doc.getLong("xp")?.toInt() ?: 0
                        val level = doc.getLong("level")?.toInt() ?: 1
                        val highestScore = doc.getLong("highestScore")?.toInt() ?: 0
                        val monthlyScore = doc.getLong("monthlyScore")?.toInt() ?: 0
                        val lastMonth = doc.getString("lastScoreMonthKey") ?: ""
                        val activeMonthly = if (lastMonth == currentMonth) monthlyScore else 0

                        UserProfile(
                            uid = uid,
                            displayName = name,
                            customClassName = customClass,
                            baseGrade = baseGrade,
                            registeredAcademicYear = regYear,
                            level = level,
                            xp = xp,
                            highestScore = highestScore,
                            monthlyScore = activeMonthly,
                            lastScoreMonthKey = currentMonth
                        )
                    } catch (_: Exception) {
                        null
                    }
                }
                _registeredUsers.value = userList
            }
        }
    }

    private fun detachFirestoreListeners() {
        assignmentsListener?.remove()
        assignmentsListener = null
        wordsListener?.remove()
        wordsListener = null
        grammarListener?.remove()
        grammarListener = null
        usersListener?.remove()
        usersListener = null
    }

    fun addCustomAssignment(assignment: TeacherAssignment) {
        val uid = auth.currentUser?.uid ?: "teacher_local"
        _assignments.value = listOf(assignment) + _assignments.value.filter { it.id != assignment.id }

        val payload = hashMapOf(
            "id" to assignment.id,
            "teacherUid" to uid,
            "teacherName" to assignment.teacherName,
            "grade" to assignment.grade,
            "title" to assignment.title,
            "description" to assignment.description,
            "questions" to assignment.questions.map { q ->
                mapOf(
                    "id" to q.id,
                    "question" to q.question,
                    "options" to q.options,
                    "correctIndex" to q.correctIndex,
                    "explanation" to q.explanation
                )
            },
            "createdAt" to FieldValue.serverTimestamp()
        )

        val docRef = db.collection("assignments").document(assignment.id)
        docRef.set(payload)
            .addOnFailureListener { e ->
                handleFirestoreError(e, OperationType.CREATE, docRef.path)
            }
    }

    fun updateCustomAssignment(assignment: TeacherAssignment) {
        val uid = auth.currentUser?.uid ?: "teacher_local"
        _assignments.value = _assignments.value.map { if (it.id == assignment.id) assignment else it }

        val payload = hashMapOf(
            "id" to assignment.id,
            "teacherUid" to uid,
            "teacherName" to assignment.teacherName,
            "grade" to assignment.grade,
            "title" to assignment.title,
            "description" to assignment.description,
            "questions" to assignment.questions.map { q ->
                mapOf(
                    "id" to q.id,
                    "question" to q.question,
                    "options" to q.options,
                    "correctIndex" to q.correctIndex,
                    "explanation" to q.explanation
                )
            },
            "updatedAt" to FieldValue.serverTimestamp()
        )

        val docRef = db.collection("assignments").document(assignment.id)
        docRef.set(payload, SetOptions.merge())
            .addOnFailureListener { e ->
                handleFirestoreError(e, OperationType.UPDATE, docRef.path)
            }
    }

    fun deleteCustomAssignment(assignmentId: String) {
        _assignments.value = _assignments.value.filter { it.id != assignmentId }
        val docRef = db.collection("assignments").document(assignmentId)
        docRef.delete()
            .addOnFailureListener { e ->
                handleFirestoreError(e, OperationType.DELETE, docRef.path)
            }
    }

    fun generateAndSaveReputableExam(
        grade: Int,
        source: ExamSource,
        questionCount: Int,
        teacherName: String
    ): TeacherAssignment {
        val exam = ReputableExamBank.generateReputableExam(grade, source, questionCount, teacherName)
        addCustomAssignment(exam)
        return exam
    }

    fun addCustomWord(
        grade: Int,
        unitNumber: Int,
        word: String,
        phonetic: String,
        meaningVi: String,
        exampleEn: String,
        exampleVi: String,
        distractors: List<String>
    ) {
        val id = "word_${System.currentTimeMillis()}"
        val uid = auth.currentUser?.uid ?: "teacher_local"

        val newWord = WordItem(
            id = id,
            word = word.trim(),
            phonetic = phonetic.trim(),
            meaningVi = meaningVi.trim(),
            exampleEn = exampleEn.trim(),
            exampleVi = exampleVi.trim(),
            grade = grade,
            unitNumber = unitNumber,
            distractorsVi = distractors
        )
        _customWords.value = listOf(newWord) + _customWords.value

        val payload = hashMapOf(
            "id" to id,
            "teacherUid" to uid,
            "grade" to grade,
            "unitNumber" to unitNumber,
            "word" to word.trim(),
            "phonetic" to phonetic.trim(),
            "meaningVi" to meaningVi.trim(),
            "exampleEn" to exampleEn.trim(),
            "exampleVi" to exampleVi.trim(),
            "distractorsVi" to distractors,
            "createdAt" to FieldValue.serverTimestamp()
        )

        val docRef = db.collection("customWords").document(id)
        docRef.set(payload)
            .addOnFailureListener { e ->
                handleFirestoreError(e, OperationType.CREATE, docRef.path)
            }
    }

    fun addCustomGrammar(lesson: GrammarLesson) {
        val uid = auth.currentUser?.uid ?: "teacher_local"
        _customGrammar.value = listOf(lesson) + _customGrammar.value

        val payload = hashMapOf(
            "id" to lesson.id,
            "teacherUid" to uid,
            "grade" to lesson.grade,
            "title" to lesson.title,
            "formula" to lesson.formula,
            "explanationVi" to lesson.explanationVi,
            "exampleEn" to lesson.exampleEn,
            "exampleVi" to lesson.exampleVi,
            "usageNotes" to lesson.usageNotes,
            "createdAt" to FieldValue.serverTimestamp()
        )

        val docRef = db.collection("customGrammar").document(lesson.id)
        docRef.set(payload)
            .addOnFailureListener { e ->
                handleFirestoreError(e, OperationType.CREATE, docRef.path)
            }
    }

    fun getSubjectsForGrade(grade: Int): List<Subject> {
        val baseSubjects = getAllGradesData()[grade] ?: getGrade10Data()
        val customForGrade = _customWords.value.filter { it.grade == grade }
        val customGrammarForGrade = _customGrammar.value.filter { it.grade == grade }

        if (customForGrade.isEmpty() && customGrammarForGrade.isEmpty()) {
            return baseSubjects
        }

        val baseUnits = baseSubjects.flatMap { it.units }

        // Merge custom words into existing matching units
        val updatedUnits = baseUnits.map { unit ->
            val wordsForUnit = customForGrade.filter { it.unitNumber == unit.unitNumber }
            if (wordsForUnit.isNotEmpty()) {
                unit.copy(words = (unit.words + wordsForUnit).distinctBy { it.word.lowercase() })
            } else {
                unit
            }
        }.toMutableList()

        // Collect extra words that belong to unit 0 or units not in base list
        val extraWords = customForGrade.filter { customWord ->
            baseUnits.none { it.unitNumber == customWord.unitNumber }
        }

        if (extraWords.isNotEmpty() || customGrammarForGrade.isNotEmpty()) {
            val customUnitWords = if (extraWords.isNotEmpty()) extraWords else customForGrade
            updatedUnits.add(
                0,
                UnitTopic(
                    id = "unit_teacher_custom_$grade",
                    grade = grade,
                    unitNumber = 0,
                    title = "Unit Bổ Sung (Thầy Cô)",
                    description = "Từ vựng & chuyên đề giáo viên vừa cập nhật trực tuyến",
                    difficulty = "Nâng cao GV",
                    words = customUnitWords,
                    grammarLesson = customGrammarForGrade.firstOrNull(),
                    bestScore = 0,
                    isCompleted = false
                )
            )
        }

        return listOf(
            Subject(
                id = "sub_grade_$grade",
                grade = grade,
                title = "Tiếng Anh Lớp $grade",
                subtitle = "Chương trình GDPT mới chuẩn Bộ GD&ĐT",
                iconCategory = "school",
                units = updatedUnits
            )
        )
    }

    fun getGrammarLessonsForGrade(grade: Int): List<GrammarLesson> {
        val baseLessons = getGrammarDatabase()[grade] ?: emptyList()
        val custom = _customGrammar.value.filter { it.grade == grade }
        return custom + baseLessons
    }

    fun getAssignmentsForGrade(grade: Int): List<TeacherAssignment> {
        return _assignments.value.filter { it.grade == grade }
    }

    // Comprehensive Bilingual Dictionary Database (EN <-> VI)
    private val dictionaryDatabase = listOf(
        DictionaryEntry("ubiquitous", "/juːˈbɪk.wə.təs/", "adj", "có mặt khắp nơi, phổ biến rộng rãi", "Smartphones are ubiquitous today.", "Điện thoại hiện diện khắp mọi nơi ngày nay.", listOf("omnipresent", "pervasive")),
        DictionaryEntry("resilience", "/rɪˈzɪl.jəns/", "n", "sự kiên cường, khả năng phục hồi nhanh", "He showed great resilience in crisis.", "Anh ấy thể hiện sự kiên cường trong khủng hoảng.", listOf("toughness", "flexibility")),
        DictionaryEntry("meticulous", "/məˈtɪk.jə.ləs/", "adj", "tỉ mỉ, cẩn thận từng chi tiết", "She is meticulous about her work.", "Cô ấy rất tỉ mỉ trong công việc.", listOf("thorough", "diligent")),
        DictionaryEntry("ephemeral", "/ɪˈfem.ər.əl/", "adj", "phù du, ngắn ngủi, chóng tàn", "Fashions are ephemeral.", "Thời trang thường rất chóng tàn.", listOf("transient", "fleeting")),
        DictionaryEntry("breadwinner", "/ˈbredˌwɪn.ər/", "n", "trụ cột gia đình, người kiếm sống chính", "He is the sole breadwinner.", "Anh ấy là người trụ cột kiếm tiền duy nhất.", listOf("provider")),
        DictionaryEntry("homemaker", "/ˈhəʊmˌmeɪ.kər/", "n", "người nội trợ quán xuyến gia đình", "She decided to be a full-time homemaker.", "Bà ấy quyết định làm người nội trợ toàn thời gian.", listOf("housewife", "housekeeper")),
        DictionaryEntry("biodiversity", "/ˌbaɪ.əʊ.daɪˈvɜː.sə.ti/", "n", "đa dạng sinh học", "Conserving forest biodiversity is vital.", "Bảo tồn đa dạng sinh học rừng là tối quan trọng.", listOf("ecological diversity")),
        DictionaryEntry("catastrophic", "/ˌkæt.əˈstrɒf.ɪk/", "adj", "thảm họa, thảm khốc, tàn phá lớn", "A catastrophic earthquake struck.", "Một trận động đất thảm khốc đã ập đến.", listOf("disastrous", "devastating")),
        DictionaryEntry("perseverance", "/ˌpɜː.sɪˈvɪə.rəns/", "n", "sự bền chí, lòng kiên trì vượt khó", "Success comes with perseverance.", "Thành công đến cùng sự kiên trì bền bỉ.", listOf("persistence", "tenacity")),
        DictionaryEntry("lucid", "/ˈluː.sɪd/", "adj", "rõ ràng, minh bạch, dễ hiểu", "His explanation was lucid.", "Lời giải thích của ông ấy rất rõ ràng.", listOf("clear", "coherent")),
        DictionaryEntry("inversion", "/ɪnˈvɜː.ʃən/", "n", "đảo ngữ, sự đảo lộn cấu trúc", "Inversion is often used for emphasis.", "Đảo ngữ thường dùng để nhấn mạnh.", listOf("reversal")),
        DictionaryEntry("sustainable", "/səˈsteɪ.nə.bəl/", "adj", "bền vững, thân thiện môi trường", "Sustainable energy is essential.", "Năng lượng bền vững là điều thiết yếu.", listOf("renewable", "eco-friendly")),
        DictionaryEntry("infrastructure", "/ˈɪn.frəˌstrʌk.tʃər/", "n", "cơ sở hạ tầng", "Modern transport infrastructure helps trade.", "Hạ tầng giao thông hiện đại hỗ trợ thương mại.", listOf("facilities")),
        DictionaryEntry("congestion", "/kənˈdʒes.tʃən/", "n", "sự ùn tắc, tắc nghẽn", "Traffic congestion is severe.", "Ùn tắc giao thông rất nghiêm trọng.", listOf("traffic jam", "blockage")),
        DictionaryEntry("distinguished", "/dɪˈstɪŋ.ɡwɪʃt/", "adj", "kiệt xuất, lỗi lạc, xuất chúng", "A distinguished scientist gave a speech.", "Một nhà khoa học kiệt xuất đã phát biểu.", listOf("eminent", "renowned")),
        DictionaryEntry("dedicate", "/ˈded.ɪ.keɪt/", "v", "cống hiến, tận tụy hy sinh", "She dedicated her life to teaching.", "Cô đã cống hiến cuộc đời cho việc dạy học.", listOf("devote", "commit")),
        DictionaryEntry("scrutinize", "/ˈskruː.tɪ.naɪz/", "v", "xem xét kỹ lưỡng, soi xét cẩn thận", "Inspectors scrutinized the data.", "Các thanh tra đã soi xét dữ liệu kỹ càng.", listOf("examine", "inspect")),
        DictionaryEntry("ambiguous", "/æmˈbɪɡ.ju.əs/", "adj", "mơ hồ, đa nghĩa, không rõ ràng", "The law was ambiguous.", "Điều luật này rất mơ hồ.", listOf("vague", "unclear")),
        DictionaryEntry("profound", "/prəˈfaʊnd/", "adj", "sâu sắc, thâm thúy, to lớn", "A profound impact on culture.", "Tác động sâu sắc đến nền văn hóa.", listOf("deep", "insightful")),
        DictionaryEntry("indispensable", "/ˌɪn.dɪˈspen.sə.bəl/", "adj", "không thể thiếu, thiết yếu", "Water is indispensable for life.", "Nước là không thể thiếu đối với sự sống.", listOf("essential", "crucial")),
        DictionaryEntry("hobby", "/ˈhɒb.i/", "n", "sở thích, thú vui lúc rảnh", "Photography is my hobby.", "Nhiếp ảnh là sở thích của tôi.", listOf("pastime")),
        DictionaryEntry("volunteer", "/ˌvɒl.ənˈtɪər/", "n, v", "tình nguyện viên, tình nguyện", "Many students volunteered at the shelter.", "Nhiều học sinh đã tình nguyện tại mái ấm.", listOf("helper")),
        DictionaryEntry("harvest", "/ˈhɑː.vɪst/", "n, v", "mùa thu hoạch, thu hoạch mùa màng", "Farmers celebrate a good harvest.", "Nông dân ăn mừng vụ mùa bội thu.", listOf("crop", "yield")),
        DictionaryEntry("artisan", "/ˌɑː.tɪˈzæn/", "n", "thợ thủ công mỹ nghệ lành nghề", "The village is famous for its pottery artisans.", "Ngôi làng nổi tiếng với các nghệ nhân gốm.", listOf("craftsman")),
        DictionaryEntry("adolescence", "/ˌæd.əˈles.əns/", "n", "tuổi thanh thiếu niên, vị thành niên", "Adolescence brings emotional changes.", "Tuổi dậy thì mang lại nhiều thay đổi tâm lý.", listOf("youth", "teens")),
        DictionaryEntry("longevity", "/lɒnˈdʒev.ə.ti/", "n", "tuổi thọ, sự sống lâu", "Diet plays a role in longevity.", "Chế độ ăn đóng vai trò với tuổi thọ.", listOf("long life")),
        DictionaryEntry("curfew", "/ˈkɜː.fjuː/", "n", "giờ giới nghiêm về nhà", "Her parents set a 10 PM curfew.", "Bố mẹ cô ấy đặt giờ giới nghiêm lúc 10 giờ tối.", listOf("deadline")),
        DictionaryEntry("generation gap", "/ˌdʒen.əˈreɪ.ʃən ɡæp/", "n", "khoảng cách thế hệ", "Parents and teens face a generation gap.", "Phụ huynh và con cái đối mặt khoảng cách thế hệ.", listOf("age divide")),
        DictionaryEntry("heritage", "/ˈher.ɪ.tɪdʒ/", "n", "di sản văn hóa, truyền thống", "Preserving cultural heritage is essential.", "Bảo tồn di sản văn hóa là việc cần thiết.", listOf("legacy", "tradition")),
        DictionaryEntry("assimilation", "/əˌsɪm.ɪˈleɪ.ʃən/", "n", "sự đồng hóa văn hóa", "Cultural assimilation takes generations.", "Sự đồng hóa văn hóa mất nhiều thế hệ.", listOf("integration")),
        DictionaryEntry("compass", "/ˈkʌm.pəs/", "n", "com-pa vẽ hình tròn, la bàn định hướng", "Use a compass to draw circles.", "Dùng com-pa để vẽ các hình tròn.", listOf("pair of compasses")),
        DictionaryEntry("calculator", "/ˈkæl.kjə.leɪ.tər/", "n", "máy tính bỏ túi", "A scientific calculator helps in exams.", "Máy tính khoa học giúp ích trong các kỳ thi.", listOf("adding machine")),
        DictionaryEntry("uniform", "/ˈjuː.nɪ.fɔːm/", "n", "bộ đồng phục học sinh / công sở", "Students wear clean uniforms.", "Học sinh mặc những bộ đồng phục sạch đẹp.", listOf("livery", "costume")),
        DictionaryEntry("textbook", "/ˈtekst.bʊk/", "n", "sách giáo khoa chuẩn chương trình", "Refer to the textbook for details.", "Hãy tham khảo sách giáo khoa để biết thêm chi tiết.", listOf("coursebook", "manual")),
        DictionaryEntry("pottery", "/ˈpɒt.ər.i/", "n", "đồ gốm, nghệ thuật gốm sứ", "Bat Trang is famous for traditional pottery.", "Bát Tràng nổi tiếng với đồ gốm truyền thống.", listOf("ceramics")),
        DictionaryEntry("sunburn", "/ˈsʌn.bɜːn/", "n", "vết cháy nắng, bỏng rát do ánh mặt trời", "Sunburn damages sensitive skin.", "Cháy nắng gây hại cho làn da nhạy cảm.", listOf("solar burn")),
        DictionaryEntry("allergy", "/ˈæl.ə.dʒi/", "n", "chứng dị ứng phấn hoa / hải sản", "He takes medicine for his allergy.", "Cậu ấy uống thuốc điều trị chứng dị ứng.", listOf("hypersensitivity")),
        DictionaryEntry("school", "/skuːl/", "n", "trường học, ngôi trường", "Luong Phu High School is located in Thai Nguyen.", "Trường THPT Lương Phú nằm tại Thái Nguyên.", listOf("academy", "institution")),
        DictionaryEntry("teacher", "/ˈtiː.tʃər/", "n", "giáo viên, thầy cô giáo", "The teacher explained the lesson clearly.", "Thầy giáo giải thích bài học rất rõ ràng.", listOf("instructor", "educator")),
        DictionaryEntry("student", "/ˈstjuː.dənt/", "n", "học sinh, sinh viên", "Students are eager to practice reflex English.", "Học sinh rất hào hứng luyện tiếng Anh phản xạ.", listOf("pupil", "learner")),
        DictionaryEntry("education", "/ˌedʒ.ʊˈkeɪ.ʃən/", "n", "nền giáo dục, việc học tập", "Education empowers the youth.", "Giáo dục chắp cánh cho thế hệ trẻ.", listOf("instruction", "schooling")),
        DictionaryEntry("knowledge", "/ˈnɒl.ɪdʒ/", "n", "kiến thức, sự hiểu biết", "Knowledge is power.", "Tri thức là sức mạnh.", listOf("understanding", "wisdom")),
        DictionaryEntry("practice", "/ˈpræk.tɪs/", "v, n", "luyện tập, thực hành", "Practice makes perfect.", "Có công mài sắt có ngày nên kim.", listOf("train", "exercise")),
        DictionaryEntry("intelligent", "/ɪnˈtel.ɪ.dʒənt/", "adj", "thông minh, sáng dạ", "She is an intelligent student.", "Cô ấy là một học sinh thông minh.", listOf("smart", "clever")),
        DictionaryEntry("environment", "/ɪnˈvaɪ.rən.mənt/", "n", "môi trường sống tự nhiên", "Protect our school environment.", "Hãy bảo vệ môi trường trường học của chúng ta.", listOf("surroundings", "nature")),
        DictionaryEntry("friendly", "/ˈfrend.li/", "adj", "thân thiện, hòa đồng, hiếu khách", "Teachers and students are friendly.", "Thầy cô và bạn bè đều rất thân thiện.", listOf("welcoming", "amiable")),
        DictionaryEntry("future", "/ˈfjuː.tʃər/", "n, adj", "tương lai, ngày mai", "Work hard for a brighter future.", "Hãy nỗ lực cho một tương lai tươi sáng hơn.", listOf("tomorrow")),
        DictionaryEntry("success", "/səkˈses/", "n", "sự thành công, thắng lợi", "Patience leads to success.", "Sự kiên nhẫn dẫn lối đến thành công.", listOf("achievement", "triumph")),
        DictionaryEntry("challenge", "/ˈtʃæl.ɪndʒ/", "n, v", "thử thách, thách thức vượt lên", "Every exam is an exciting challenge.", "Mỗi kỳ thi là một thử thách thú vị.", listOf("trial", "contest")),
        DictionaryEntry("curiosity", "/ˌkjʊə.riˈɒs.ə.ti/", "n", "tính tò mò, lòng ham học hỏi", "Children learn through curiosity.", "Trẻ em học hỏi qua lòng tò mò.", listOf("inquisitiveness")),
        DictionaryEntry("generation", "/ˌdʒen.əˈreɪ.ʃən/", "n", "thế hệ, lứa tuổi", "Young generation leads technology.", "Thế hệ trẻ dẫn dắt công nghệ.", listOf("era", "age group")),
        DictionaryEntry("independent", "/ˌɪn.dɪˈpen.dənt/", "adj", "độc lập, tự chủ, tự lực", "He became independent after university.", "Anh ấy trở nên tự lập sau khi tốt nghiệp đại học.", listOf("self-reliant")),
        DictionaryEntry("opportunity", "/ˌɒp.əˈtʃuː.nə.ti/", "n", "cơ hội, thời cơ tốt", "Grab the opportunity to study abroad.", "Hãy nắm bắt cơ hội đi du học.", listOf("chance", "opening")),
        DictionaryEntry("confidence", "/ˈkɒn.fɪ.dəns/", "n", "sự tự tin, niềm tin vào bản thân", "Speak English with confidence.", "Hãy nói tiếng Anh với sự tự tin.", listOf("assurance", "belief"))
    )

    fun searchDictionary(query: String, isEnglishToVietnamese: Boolean): List<DictionaryEntry> {
        return ComprehensiveDictionary.lookup(query, isEnglishToVietnamese)
    }

    private fun removeDiacritics(text: String): String {
        val normalized = Normalizer.normalize(text, Normalizer.Form.NFD)
        val pattern = Pattern.compile("\\p{InCombiningDiacriticalMarks}+")
        return pattern.matcher(normalized).replaceAll("").replace("đ", "d").replace("Đ", "D")
    }

    // Grammar Lessons by Grade (Grades 6 - 12)
    private fun getGrammarDatabase(): Map<Int, List<GrammarLesson>> {
        return mapOf(
            6 to listOf(
                GrammarLesson(
                    id = "g6_1",
                    grade = 6,
                    title = "Thì Hiện Tại Đơn (Present Simple)",
                    formula = "Khẳng định: S + V(s/es) | Phủ định: S + do/does not + V-inf | Nghi vấn: Do/Does + S + V-inf?",
                    explanationVi = "Dùng để diễn tả thói quen, hành động lặp đi lặp lại hoặc chân lý, sự thật hiển nhiên.",
                    exampleEn = "She studies English every morning at 7 AM.",
                    exampleVi = "Cô ấy học tiếng Anh vào mỗi buổi sáng lúc 7 giờ.",
                    usageNotes = "Dấu hiệu: always, usually, often, sometimes, everyday."
                ),
                GrammarLesson(
                    id = "g6_2",
                    grade = 6,
                    title = "Thì Hiện Tại Tiếp Diễn (Present Continuous)",
                    formula = "S + am/is/are + V-ing",
                    explanationVi = "Diễn tả hành động đang xảy ra tại thời điểm nói hoặc kế hoạch chắc chắn trong tương lai gần.",
                    exampleEn = "They are playing football right now.",
                    exampleVi = "Họ đang chơi bóng đá ngay lúc này.",
                    usageNotes = "Dấu hiệu: now, at the moment, at present, Look!, Listen!"
                )
            ),
            7 to listOf(
                GrammarLesson(
                    id = "g7_1",
                    grade = 7,
                    title = "Thì Quá Khứ Đơn (Past Simple)",
                    formula = "Động từ to-be: S + was/were | Động từ thường: S + V2/ed",
                    explanationVi = "Diễn tả hành động đã xảy ra và chấm dứt hoàn toàn trong quá khứ.",
                    exampleEn = "We visited Ha Long Bay last summer.",
                    exampleVi = "Chúng tôi đã đi thăm Vịnh Hạ Long vào mùa hè năm ngoái.",
                    usageNotes = "Dấu hiệu: yesterday, ago, last week/year, in 2020."
                ),
                GrammarLesson(
                    id = "g7_2",
                    grade = 7,
                    title = "Câu Mệnh Lệnh & Lời Khuyên Sức Khỏe",
                    formula = "Do more exercise! / Don't eat too much junk food! / You should + V-inf",
                    explanationVi = "Dùng để đưa ra lời hướng dẫn hoặc lời khuyên bảo vệ sức khỏe cho học sinh.",
                    exampleEn = "You should drink plenty of water everyday.",
                    exampleVi = "Bạn nên uống nhiều nước mỗi ngày.",
                    usageNotes = "Should = nên; Shouldn't = không nên."
                )
            ),
            8 to listOf(
                GrammarLesson(
                    id = "g8_1",
                    grade = 8,
                    title = "So Sánh Hơn Của Trạng Từ (Comparative Adverbs)",
                    formula = "Ngắn: S1 + V + adv-er + than + S2 | Dài: S1 + V + more + adv + than + S2",
                    explanationVi = "Dùng để so sánh mức độ, tốc độ hay cách thức thực hiện hành động giữa hai đối tượng.",
                    exampleEn = "A horse runs faster than a camel.",
                    exampleVi = "Một con ngựa chạy nhanh hơn một con lạc đà.",
                    usageNotes = "Ngoại lệ: well -> better, badly -> worse, far -> farther/further."
                ),
                GrammarLesson(
                    id = "g8_2",
                    grade = 8,
                    title = "Câu Phức Với Liên Từ Chỉ Thời Gian & Nhượng Bộ",
                    formula = "Although/Even though + S + V, S + V | Because/Since + S + V, S + V",
                    explanationVi = "Nối các mệnh đề chỉ sự tương phản, đối lập hoặc nguyên nhân kết quả.",
                    exampleEn = "Although it rained heavily, we still went camping.",
                    exampleVi = "Mặc dù trời mưa to, chúng tôi vẫn đi cắm trại.",
                    usageNotes = "Không dùng 'Although' đi kèm với 'But' trong cùng một câu."
                )
            ),
            9 to listOf(
                GrammarLesson(
                    id = "g9_1",
                    grade = 9,
                    title = "Cụm Động Từ Thông Dụng (Phrasal Verbs)",
                    formula = "Verb + Preposition / Particle (look after, set off, pass down, turn down)",
                    explanationVi = "Sự kết hợp giữa động từ và giới từ tạo nên nghĩa mới hoàn toàn.",
                    exampleEn = "Traditional craft skills are passed down from generation to generation.",
                    exampleVi = "Các kỹ năng thủ công truyền thống được truyền lại từ đời này sang đời khác.",
                    usageNotes = "Pass down = lưu truyền; Set off = khởi hành; Look after = chăm sóc."
                ),
                GrammarLesson(
                    id = "g9_2",
                    grade = 9,
                    title = "Mệnh Đề Quan Hệ Xác Định & Không Xác Định",
                    formula = "Who (chỉ người làm S/O), Which (chỉ vật), Whose (sở hữu), That",
                    explanationVi = "Bổ nghĩa cho danh từ đứng trước, giúp câu văn cô đọng và liên kết chặt chẽ.",
                    exampleEn = "The artisan who made this vase lives in Bat Trang village.",
                    exampleVi = "Nghệ nhân người làm ra chiếc bình này sống ở làng Bát Tràng.",
                    usageNotes = "Mệnh đề không xác định có dấu phẩy và KHÔNG được dùng 'that'."
                )
            ),
            10 to listOf(
                GrammarLesson(
                    id = "g10_1",
                    grade = 10,
                    title = "Thì Hiện Tại Đơn vs Hiện Tại Tiếp Diễn (Family Life)",
                    formula = "Hiện tại đơn: S + V(s/es) | Hiện tại tiếp diễn: S + is/am/are + V-ing",
                    explanationVi = "Phân biệt thói quen phân chia việc nhà thường nhật và hành động đang diễn ra tại thời điểm nói.",
                    exampleEn = "My mother usually cooks dinner, but today my father is preparing the meal.",
                    exampleVi = "Mẹ tôi thường nấu cơm tối, nhưng hôm nay bố tôi đang chuẩn bị bữa ăn.",
                    usageNotes = "Không dùng thì tiếp diễn với động từ tri giác: know, believe, like, understand."
                ),
                GrammarLesson(
                    id = "g10_2",
                    grade = 10,
                    title = "Câu Bị Động Với Động Từ Khuyết Thiếu (Passive Modals)",
                    formula = "S + modal verb (can/must/should/will) + be + V3/ed",
                    explanationVi = "Nhấn mạnh vào hành động bảo vệ môi trường thay vì người thực hiện.",
                    exampleEn = "Plastic bags should be replaced by reusable canvas bags.",
                    exampleVi = "Túi nilon nên được thay thế bằng túi vải tái sử dụng.",
                    usageNotes = "By + tân ngữ có thể lược bỏ nếu chủ thể không xác định (someone, people)."
                ),
                GrammarLesson(
                    id = "g10_3",
                    grade = 10,
                    title = "To-Infinitive và Bare Infinitive (Âm Nhạc & Thần Tượng)",
                    formula = "Verb + to-V (want, decide, hope, promise) | Verb + V-bare (make, let, see, hear)",
                    explanationVi = "Quy tắc sử dụng động từ nguyên mẫu có 'to' hoặc không 'to' sau các động từ chính.",
                    exampleEn = "The singer decided to release her new album next month.",
                    exampleVi = "Nữ ca sĩ đã quyết định phát hành album mới vào tháng tới.",
                    usageNotes = "Make someone do something; Help someone (to) do something."
                )
            ),
            11 to listOf(
                GrammarLesson(
                    id = "g11_1",
                    grade = 11,
                    title = "Mạo Từ A / An / The & Zero Article (Lối Sống Lành Mạnh)",
                    formula = "A/An + N đếm được số ít chưa xác định | The + N đã xác định / duy nhất",
                    explanationVi = "Cách dùng mạo từ với các danh từ chỉ bệnh tật, hệ cơ quan cơ thể và bữa ăn.",
                    exampleEn = "The immune system plays a crucial role in defending against diseases.",
                    exampleVi = "Hệ miễn dịch đóng vai trò quan trọng trong việc phòng chống bệnh tật.",
                    usageNotes = "Dùng 'the' với phát minh, nhạc cụ và các tổ chức độc nhất: the internet, the sun."
                ),
                GrammarLesson(
                    id = "g11_2",
                    grade = 11,
                    title = "Động Từ Khuyết Thiếu Bắt Buộc & Cấm Đoán (Must vs Should vs Ought to)",
                    formula = "Must / Have to: Bắt buộc | Mustn't: Cấm tiệt | Should / Ought to: Khuyên bảo",
                    explanationVi = "Biểu thị các quy định trong gia đình, khoảng cách thế hệ và giờ giới nghiêm của phụ huynh.",
                    exampleEn = "Teenagers must not break the curfew set by their parents.",
                    exampleVi = "Thanh thiếu niên không được phá vỡ giờ giới nghiêm do cha mẹ đặt ra.",
                    usageNotes = "Must = xuất phát từ người nói; Have to = do hoàn cảnh khách quan."
                ),
                GrammarLesson(
                    id = "g11_3",
                    grade = 11,
                    title = "Thì Hiện Tại Hoàn Thành Tiếp Diễn (Present Perfect Continuous)",
                    formula = "S + have/has + been + V-ing",
                    explanationVi = "Nhấn mạnh tính liên tục của hành động bắt đầu ở quá khứ và vẫn đang tiếp diễn ở hiện tại.",
                    exampleEn = "Scientists have been researching smart cities for over a decade.",
                    exampleVi = "Các nhà khoa học đã và đang nghiên cứu về đô thị thông minh trong hơn một thập kỷ qua.",
                    usageNotes = "Dấu hiệu: for, since, all day, lately, recently."
                )
            ),
            12 to listOf(
                GrammarLesson(
                    id = "g12_1",
                    grade = 12,
                    title = "Đảo Ngữ Với Phó Từ Phủ Định (Inversion - Ôn Thi HSG & THPT)",
                    formula = "Seldom / Rarely / Never / Hardly + Trợ Động Từ + S + V-inf",
                    explanationVi = "Cấu trúc nâng cao tạo ấn tượng mạnh mẽ trong bài thi học sinh giỏi và nghị luận tiếng Anh.",
                    exampleEn = "Seldom had he witnessed such unshakeable perseverance and dedication.",
                    exampleVi = "Hiếm khi nào ông lại chứng kiến lòng kiên trì và sự cống hiến kiên định đến như vậy.",
                    usageNotes = "Đảo trợ động từ lên trước chủ ngữ giống như cấu trúc câu hỏi."
                ),
                GrammarLesson(
                    id = "g12_2",
                    grade = 12,
                    title = "Câu Điều Kiện Hỗn Hợp (Mixed Conditionals 3 - 2)",
                    formula = "If + S + had + V3/ed (quá khứ), S + would/could + V-inf (hiện tại)",
                    explanationVi = "Giả định một hành động trái thực tế trong quá khứ dẫn đến kết quả ở hiện tại.",
                    exampleEn = "If she had taken the scholarship then, she would be studying in Oxford now.",
                    exampleVi = "Nếu lúc đó cô ấy nhận học bổng, thì bây giờ cô ấy đang học tại Oxford rồi.",
                    usageNotes = "Thường có trạng từ 'then', 'in the past' ở vế If và 'now', 'today' ở vế chính."
                ),
                GrammarLesson(
                    id = "g12_3",
                    grade = 12,
                    title = "Thể Giả Định (Subjunctive Mood)",
                    formula = "It is essential/crucial/vital that + S + (should) + V-inf",
                    explanationVi = "Dùng trong các văn bản trang trọng khi bàn về chính sách môi trường, giáo dục và công nghệ.",
                    exampleEn = "It is essential that every country take prompt action on climate change.",
                    exampleVi = "Điều tối cần thiết là mỗi quốc gia cần hành động kịp thời về biến đổi khí hậu.",
                    usageNotes = "Động từ luôn ở dạng nguyên mẫu không chia dù chủ ngữ là số ít (he, she, it)."
                )
            )
        )
    }

    // Comprehensive Curriculum from Grade 6 to 12
    private fun getAllGradesData(): Map<Int, List<Subject>> {
        return mapOf(
            6 to getGrade6Data(),
            7 to getGrade7Data(),
            8 to getGrade8Data(),
            9 to getGrade9Data(),
            10 to getGrade10Data(),
            11 to getGrade11Data(),
            12 to getGrade12Data()
        )
    }

    private fun getGrade6Data(): List<Subject> {
        return listOf(
            Subject(
                id = "sub_grade_6",
                grade = 6,
                title = "Tiếng Anh Lớp 6",
                subtitle = "Chương trình Global Success & GDPT mới",
                iconCategory = "school",
                units = listOf(
                    UnitTopic(
                        id = "g6_u1",
                        grade = 6,
                        unitNumber = 1,
                        title = "Unit 1: My New School",
                        description = "Ngôi trường mới, đồ dùng học tập và hoạt động hàng ngày",
                        difficulty = "Cơ bản",
                        words = listOf(
                            WordItem("g6_w1", "Compass", "/ˈkʌm.pəs/", "Com-pa dùng vẽ hình tròn", "I use a compass in maths class.", "Tôi dùng com-pa trong giờ toán.", 6, 1, listOf("Thước kẻ dẻo", "Hộp bút màu", "Cục tẩy mực")),
                            WordItem("g6_w2", "Calculator", "/ˈkæl.kjə.leɪ.tər/", "Máy tính bỏ túi", "Bring your calculator to school.", "Hãy mang máy tính bỏ túi đến trường.", 6, 1, listOf("Bảng viết phấn", "Bút chì kim", "Cặp sách mới")),
                            WordItem("g6_w3", "Uniform", "/ˈjuː.nɪ.fɔːm/", "Bộ đồng phục học sinh", "We wear school uniforms on Mondays.", "Chúng tôi mặc đồng phục vào các ngày thứ Hai.", 6, 1, listOf("Áo khoác gió", "Giày thể thao", "Mũ lưỡi trai")),
                            WordItem("g6_w4", "Textbook", "/ˈtekst.bʊk/", "Sách giáo khoa", "Open your English textbook to page 10.", "Mở sách giáo khoa tiếng Anh trang 10.", 6, 1, listOf("Vở ghi chép", "Tập giấy nháp", "Từ điển mini"))
                        ),
                        grammarLesson = getGrammarDatabase()[6]?.getOrNull(0),
                        bestScore = 1500,
                        isCompleted = true
                    ),
                    UnitTopic(
                        id = "g6_u2",
                        grade = 6,
                        unitNumber = 2,
                        title = "Unit 2: My House",
                        description = "Các phòng trong nhà, đồ nội thất và giới từ chỉ vị trí",
                        difficulty = "Cơ bản",
                        words = listOf(
                            WordItem("g6_w5", "Microwave", "/ˈmaɪ.krə.weɪv/", "Lò vi sóng hâm nóng thức ăn", "Heat the soup in the microwave.", "Hâm nóng canh trong lò vi sóng.", 6, 2, listOf("Tủ lạnh hai cánh", "Máy giặt tự động", "Bếp ga đôi")),
                            WordItem("g6_w6", "Wardrobe", "/ˈwɔː.drəʊb/", "Tủ đựng quần áo", "Put your clothes into the wardrobe.", "Cất quần áo của bạn vào tủ.", 6, 2, listOf("Bàn học gỗ", "Giường tầng", "Kệ để sách"))
                        ),
                        grammarLesson = getGrammarDatabase()[6]?.getOrNull(1),
                        bestScore = 0,
                        isCompleted = false
                    )
                )
            )
        )
    }

    private fun getGrade7Data(): List<Subject> {
        return listOf(
            Subject(
                id = "sub_grade_7",
                grade = 7,
                title = "Tiếng Anh Lớp 7",
                subtitle = "Sở thích, lối sống lành mạnh và phục vụ cộng đồng",
                iconCategory = "school",
                units = listOf(
                    UnitTopic(
                        id = "g7_u1",
                        grade = 7,
                        unitNumber = 1,
                        title = "Unit 1: Hobbies",
                        description = "Các sở thích độc đáo, làm đồ gốm và trượt băng",
                        difficulty = "Cơ bản",
                        words = listOf(
                            WordItem("g7_w1", "Pottery", "/ˈpɒt.ər.i/", "Đồ gốm, nghề làm gốm thủ công", "Making pottery is relaxing.", "Làm đồ gốm rất thư giãn.", 7, 1, listOf("Vẽ tranh sơn dầu", "Đan lát len", "Sưu tầm tem cổ")),
                            WordItem("g7_w2", "Ice-skating", "/ˈaɪsˌskeɪ.tɪŋ/", "Môn trượt băng nghệ thuật", "She goes ice-skating at weekends.", "Cô ấy đi trượt băng vào cuối tuần.", 7, 1, listOf("Bơi lội tự do", "Leo núi mạo hiểm", "Trượt patin đường phố"))
                        ),
                        grammarLesson = getGrammarDatabase()[7]?.getOrNull(0),
                        bestScore = 1620,
                        isCompleted = true
                    ),
                    UnitTopic(
                        id = "g7_u2",
                        grade = 7,
                        unitNumber = 2,
                        title = "Unit 2: Healthy Living",
                        description = "Sức khỏe tuổi mới lớn, dị ứng và chế độ ăn cân bằng",
                        difficulty = "Trung cấp",
                        words = listOf(
                            WordItem("g7_w3", "Allergy", "/ˈæl.ə.dʒi/", "Bệnh dị ứng thời tiết / thực phẩm", "He has a seafood allergy.", "Anh ấy bị dị ứng hải sản.", 7, 2, listOf("Cảm cúm thông thường", "Đau đầu dữ dội", "Sốt phát ban")),
                            WordItem("g7_w4", "Sunburn", "/ˈsʌn.bɜːn/", "Vết cháy nắng, bỏng nắng", "Wear sunscreen to prevent sunburn.", "Thoa kem chống nắng để tránh cháy nắng.", 7, 2, listOf("Nổi mề đay", "Vết trầy xước", "Cảm lạnh"))
                        ),
                        grammarLesson = getGrammarDatabase()[7]?.getOrNull(1),
                        bestScore = 0,
                        isCompleted = false
                    )
                )
            )
        )
    }

    private fun getGrade8Data(): List<Subject> {
        return listOf(
            Subject(
                id = "sub_grade_8",
                grade = 8,
                title = "Tiếng Anh Lớp 8",
                subtitle = "Nông thôn, thế hệ tuổi teen và phong tục tập quán",
                iconCategory = "school",
                units = listOf(
                    UnitTopic(
                        id = "g8_u1",
                        grade = 8,
                        unitNumber = 1,
                        title = "Unit 1: Life in the Countryside",
                        description = "Cuộc sống đồng quê thanh bình, vụ mùa và chăn nuôi",
                        difficulty = "Trung cấp",
                        words = listOf(
                            WordItem("g8_w1", "Harvest", "/ˈhɑː.vɪst/", "Vụ thu hoạch mùa màng", "Farmers work hard during harvest time.", "Bà con nông dân làm việc chăm chỉ trong mùa gặt.", 8, 1, listOf("Mùa gieo hạt giống", "Mùa cấy lúa", "Mùa mưa lũ")),
                            WordItem("g8_w2", "Paddy field", "/ˈpæd.i fiːld/", "Cánh đồng lúa bạt ngàn", "Children fly kites near the paddy field.", "Trẻ em thả diều gần cánh đồng lúa.", 8, 1, listOf("Vườn cây ăn trái", "Nương rẫy bậc thang", "Khu rừng nguyên sinh")),
                            WordItem("g8_w3", "Peaceful", "/ˈpiːs.fəl/", "Yên bình, thanh thản", "The countryside is quiet and peaceful.", "Vùng quê thật yên tĩnh và thanh bình.", 8, 1, listOf("Ồn ào náo nhiệt", "Nguy hiểm rình rập", "Tấp nập đông đúc"))
                        ),
                        grammarLesson = getGrammarDatabase()[8]?.getOrNull(0),
                        bestScore = 1750,
                        isCompleted = true
                    ),
                    UnitTopic(
                        id = "g8_u2",
                        grade = 8,
                        unitNumber = 2,
                        title = "Unit 2: Teenagers & Pressure",
                        description = "Áp lực học tập, diễn đàn và mạng xã hội",
                        difficulty = "Trung cấp",
                        words = listOf(
                            WordItem("g8_w4", "Peer pressure", "/ˈpɪə ˌpreʃ.ər/", "Áp lực từ bạn bè đồng trang lứa", "She faces peer pressure to fit in.", "Cô ấy đối mặt với áp lực đồng trang lứa để hòa nhập.", 8, 2, listOf("Kỳ vọng từ thầy cô", "Bất đồng gia đình", "Áp lực tài chính")),
                            WordItem("g8_w5", "Overcome", "/ˌəʊ.vəˈkʌm/", "Vượt qua khó khăn, nghịch cảnh", "He overcame stress with music.", "Cậu ấy đã vượt qua căng thẳng nhờ âm nhạc.", 8, 2, listOf("Đầu hàng thất bại", "Tránh né vấn đề", "Tạo thêm rắc rối"))
                        ),
                        grammarLesson = getGrammarDatabase()[8]?.getOrNull(1),
                        bestScore = 0,
                        isCompleted = false
                    )
                )
            )
        )
    }

    private fun getGrade9Data(): List<Subject> {
        return listOf(
            Subject(
                id = "sub_grade_9",
                grade = 9,
                title = "Tiếng Anh Lớp 9",
                subtitle = "Làng nghề truyền thống, đô thị và chuẩn bị thi vào 10",
                iconCategory = "school",
                units = listOf(
                    UnitTopic(
                        id = "g9_u1",
                        grade = 9,
                        unitNumber = 1,
                        title = "Unit 1: Local Community",
                        description = "Nghệ nhân, làng nghề thủ công mỹ nghệ và bảo tồn văn hóa",
                        difficulty = "Trung cấp",
                        words = listOf(
                            WordItem("g9_w1", "Artisan", "/ˌɑː.tɪˈzæn/", "Nghệ nhân tay nghề cao", "The artisan carved a wooden statue.", "Nghệ nhân đã tạc một pho tượng gỗ.", 9, 1, listOf("Người mua hàng rong", "Hướng dẫn viên", "Khách du lịch")),
                            WordItem("g9_w2", "Handicraft", "/ˈhæn.dɪ.krɑːft/", "Đồ thủ công mỹ nghệ tinh xảo", "Bat Trang is famous for handicrafts.", "Bát Tràng nổi tiếng với các sản phẩm thủ công.", 9, 1, listOf("Hàng công nghiệp nặng", "Đồ điện tử thông minh", "Nông sản tươi")),
                            WordItem("g9_w3", "Preserve", "/prɪˈzɜːv/", "Gìn giữ, bảo tồn giá trị truyền thống", "We must preserve our traditions.", "Chúng ta phải gìn giữ truyền thống của mình.", 9, 1, listOf("Bỏ quên lãng phí", "Thương mại hóa thô bạo", "Thay đổi hoàn toàn"))
                        ),
                        grammarLesson = getGrammarDatabase()[9]?.getOrNull(0),
                        bestScore = 1880,
                        isCompleted = true
                    ),
                    UnitTopic(
                        id = "g9_u2",
                        grade = 9,
                        unitNumber = 2,
                        title = "Unit 2: City Life",
                        description = "Đô thị hiện đại, chi phí sinh hoạt và tắc nghẽn",
                        difficulty = "Trung cấp",
                        words = listOf(
                            WordItem("g9_w4", "Metropolitan", "/ˌmet.rəˈpɒl.ɪ.tən/", "Thuộc vùng đô thị lớn, thủ phủ", "Living in a metropolitan area has pros and cons.", "Sống ở vùng đại đô thị có cả ưu và nhược điểm.", 9, 2, listOf("Vùng nông thôn hẻo lánh", "Vùng duyên hải", "Khu bảo tồn hoang dã")),
                            WordItem("g9_w5", "Affordable", "/əˈfɔː.də.bəl/", "Giá cả phải chăng, vừa túi tiền", "They found an affordable apartment.", "Họ đã tìm được một căn hộ vừa túi tiền.", 9, 2, listOf("Đắt đỏ xa xỉ", "Cực kỳ khan hiếm", "Chất lượng kém"))
                        ),
                        grammarLesson = getGrammarDatabase()[9]?.getOrNull(1),
                        bestScore = 0,
                        isCompleted = false
                    )
                )
            )
        )
    }

    private fun getGrade10Data(): List<Subject> {
        return listOf(
            Subject(
                id = "sub_grade_10",
                grade = 10,
                title = "Tiếng Anh Lớp 10",
                subtitle = "Gia đình, môi trường sống, âm nhạc và cộng đồng",
                iconCategory = "school",
                units = listOf(
                    UnitTopic(
                        id = "g10_u1",
                        grade = 10,
                        unitNumber = 1,
                        title = "Unit 1: Family Life",
                        description = "Trụ cột gia đình, phân chia việc nhà và lối sống gắn kết",
                        difficulty = "Trung cấp",
                        words = listOf(
                            WordItem("g10_w1", "Breadwinner", "/ˈbredˌwɪn.ər/", "Trụ cột kiếm tiền nuôi gia đình", "His father is the sole breadwinner.", "Bố của cậu ấy là trụ cột kiếm tiền duy nhất.", 10, 1, listOf("Người nội trợ quán xuyến", "Con một trong nhà", "Người họ hàng xa")),
                            WordItem("g10_w2", "Homemaker", "/ˈhəʊmˌmeɪ.kər/", "Người làm nội trợ chăm sóc tổ ấm", "Both parents can share homemaker duties.", "Cả bố và mẹ đều có thể chia sẻ việc nội trợ.", 10, 1, listOf("Trụ cột tài chính", "Khách viếng thăm", "Người làm thuê")),
                            WordItem("g10_w3", "Heavy lifting", "/ˌhev.i ˈlɪf.tɪŋ/", "Công việc nặng nhọc, mang vác", "He helps his mother with the heavy lifting.", "Anh giúp mẹ những việc nặng nhọc.", 10, 1, listOf("Nấu nướng nhẹ nhàng", "Rửa chén bát", "Quét dọn phòng khách"))
                        ),
                        grammarLesson = getGrammarDatabase()[10]?.getOrNull(0),
                        bestScore = 1920,
                        isCompleted = true
                    ),
                    UnitTopic(
                        id = "g10_u2",
                        grade = 10,
                        unitNumber = 2,
                        title = "Unit 2: Humans and Environment",
                        description = "Lối sống xanh, bảo vệ sinh thái và cắt giảm rác thải",
                        difficulty = "Trung cấp",
                        words = listOf(
                            WordItem("g10_w4", "Eco-friendly", "/ˌiː.kəʊˈfrend.li/", "Thân thiện với môi trường tự nhiên", "They use eco-friendly paper bags.", "Họ sử dụng túi giấy thân thiện môi trường.", 10, 2, listOf("Độc hại khôn lường", "Khó phân hủy sinh học", "Lãng phí tài nguyên")),
                            WordItem("g10_w5", "Carbon footprint", "/ˌkɑː.bən ˈfʊt.prɪnt/", "Dấu chân carbon, lượng khí thải", "Biking helps reduce your carbon footprint.", "Đi xe đạp giúp cắt giảm dấu chân carbon.", 10, 2, listOf("Nguồn nước ngầm", "Năng lượng hóa thạch", "Nhiệt độ toàn cầu"))
                        ),
                        grammarLesson = getGrammarDatabase()[10]?.getOrNull(1),
                        bestScore = 0,
                        isCompleted = false
                    ),
                    UnitTopic(
                        id = "g10_u3",
                        grade = 10,
                        unitNumber = 3,
                        title = "Unit 3: Music & Arts",
                        description = "Thần tượng âm nhạc, giai điệu và nhạc cụ truyền thống",
                        difficulty = "Trung cấp",
                        words = listOf(
                            WordItem("g10_w6", "Talented", "/ˈtæl.ən.tɪd/", "Có tài năng bẩm sinh, tài hoa", "She is a talented pianist.", "Cô ấy là một nghệ sĩ dương cầm tài năng.", 10, 3, listOf("Kém cỏi", "Bình thường", "Thiếu kiên nhẫn")),
                            WordItem("g10_w7", "Audience", "/ˈɔː.di.əns/", "Khán giả theo dõi buổi biểu diễn", "The audience clapped enthusiastically.", "Khán giả đã vỗ tay tán thưởng nồng nhiệt.", 10, 3, listOf("Ban giám khảo", "Nhạc công", "Đạo diễn"))
                        ),
                        grammarLesson = getGrammarDatabase()[10]?.getOrNull(2),
                        bestScore = 0,
                        isCompleted = false
                    )
                )
            )
        )
    }

    private fun getGrade11Data(): List<Subject> {
        return listOf(
            Subject(
                id = "sub_grade_11",
                grade = 11,
                title = "Tiếng Anh Lớp 11",
                subtitle = "Sức khỏe dẻo dai, khoảng cách thế hệ và đô thị thông minh",
                iconCategory = "school",
                units = listOf(
                    UnitTopic(
                        id = "g11_u1",
                        grade = 11,
                        unitNumber = 1,
                        title = "Unit 1: A Long and Healthy Life",
                        description = "Tuổi thọ, hệ miễn dịch, chế độ dinh dưỡng và thói quen lành mạnh",
                        difficulty = "Nâng cao",
                        words = listOf(
                            WordItem("g11_w1", "Longevity", "/lɒnˈdʒev.ə.ti/", "Tuổi thọ, sự sống lâu dài", "Exercise contributes to longevity.", "Tập thể dục góp phần kéo dài tuổi thọ.", 11, 1, listOf("Căn bệnh mãn tính", "Sự suy giảm trí nhớ", "Thể trạng suy nhược")),
                            WordItem("g11_w2", "Immune system", "/ɪˈmjuːn ˌsɪs.təm/", "Hệ miễn dịch phòng chống bệnh tật", "Vitamin C strengthens the immune system.", "Vitamin C tăng cường hệ miễn dịch.", 11, 1, listOf("Hệ bài tiết cơ thể", "Hệ tuần hoàn máu", "Hệ hô hấp")),
                            WordItem("g11_w3", "Nutrient", "/ˈnjuː.tri.ənt/", "Chất dinh dưỡng vi lượng & đa lượng", "Vegetables are rich in nutrients.", "Rau củ rất giàu chất dinh dưỡng.", 11, 1, listOf("Độc tố tích tụ", "Calo rỗng có hại", "Chất tạo màu nhân tạo"))
                        ),
                        grammarLesson = getGrammarDatabase()[11]?.getOrNull(0),
                        bestScore = 2100,
                        isCompleted = true
                    ),
                    UnitTopic(
                        id = "g11_u2",
                        grade = 11,
                        unitNumber = 2,
                        title = "Unit 2: The Generation Gap",
                        description = "Bất đồng quan điểm, giờ giới nghiêm và sự thấu hiểu",
                        difficulty = "Nâng cao",
                        words = listOf(
                            WordItem("g11_w4", "Curfew", "/ˈkɜː.fjuː/", "Giờ giới nghiêm buộc phải về nhà", "I must be home before the 10 PM curfew.", "Tôi phải về nhà trước giờ giới nghiêm 10 giờ tối.", 11, 2, listOf("Kỳ nghỉ cuối tuần", "Quy định trang phục", "Lịch trực nhật lớp")),
                            WordItem("g11_w5", "Conflict", "/ˈkɒn.flɪkt/", "Xung đột, mâu thuẫn tranh chấp", "Open communication resolves family conflict.", "Giao tiếp cởi mở giúp giải quyết mâu thuẫn gia đình.", 11, 2, listOf("Sự đồng thuận tuyệt đối", "Tình cảm gắn bó", "Thỏa hiệp hài hòa"))
                        ),
                        grammarLesson = getGrammarDatabase()[11]?.getOrNull(1),
                        bestScore = 0,
                        isCompleted = false
                    ),
                    UnitTopic(
                        id = "g11_u3",
                        grade = 11,
                        unitNumber = 3,
                        title = "Unit 3: Cities of the Future",
                        description = "Đô thị tương lai, cảm biến thông minh và hạ tầng hiện đại",
                        difficulty = "Nâng cao",
                        words = listOf(
                            WordItem("g11_w6", "Infrastructure", "/ˈɪn.frəˌstrʌk.tʃər/", "Cơ sở hạ tầng đô thị", "The city invested in green infrastructure.", "Thành phố đầu tư vào hạ tầng xanh.", 11, 3, listOf("Ô nhiễm môi trường", "Khu ổ chuột", "Ùn tắc kinh hoàng")),
                            WordItem("g11_w7", "Pedestrian", "/pəˈdes.tri.ən/", "Người đi bộ trên đường", "Pedestrian zones reduce traffic jams.", "Phố đi bộ giúp giảm ùn tắc giao thông.", 11, 3, listOf("Người lái xe tải", "Khách đi tàu điện", "Tài xế xe buýt"))
                        ),
                        grammarLesson = getGrammarDatabase()[11]?.getOrNull(2),
                        bestScore = 0,
                        isCompleted = false
                    )
                )
            )
        )
    }

    private fun getGrade12Data(): List<Subject> {
        return listOf(
            Subject(
                id = "sub_grade_12",
                grade = 12,
                title = "Tiếng Anh Lớp 12 & Ôn Thi Tốt Nghiệp",
                subtitle = "Chuyện đời danh nhân, hội nhập văn hóa và đề thi HSG",
                iconCategory = "school",
                units = listOf(
                    UnitTopic(
                        id = "g12_u1",
                        grade = 12,
                        unitNumber = 1,
                        title = "Unit 1: Life Stories & Perseverance",
                        description = "Những tấm gương cống hiến, ý chí kiên định và thành tựu",
                        difficulty = "Nâng cao HSG",
                        words = listOf(
                            WordItem("g12_w1", "Dedicate", "/ˈded.ɪ.keɪt/", "Cống hiến, tận tụy hy sinh", "He dedicated his life to education.", "Ông đã cống hiến cả cuộc đời cho giáo dục.", 12, 1, listOf("Từ chối tham gia", "Kiếm lợi nhuận riêng", "Phá bỏ truyền thống")),
                            WordItem("g12_w2", "Distinguished", "/dɪˈstɪŋ.ɡwɪʃt/", "Kiệt xuất, lỗi lạc, ưu tú xuất chúng", "She is a distinguished scientist.", "Bà là một nhà khoa học kiệt xuất.", 12, 1, listOf("Bình thường mờ nhạt", "Thất bại thảm hại", "Độc tài bảo thủ")),
                            WordItem("g12_w3", "Persevere", "/ˌpɜː.sɪˈvɪər/", "Kiên trì theo đuổi mục tiêu", "They persevered through every hardship.", "Họ đã kiên trì vượt qua mọi gian khó.", 12, 1, listOf("Bỏ cuộc buông xuôi", "Do dự thoái thác", "Nản chí nửa chừng"))
                        ),
                        grammarLesson = getGrammarDatabase()[12]?.getOrNull(0),
                        bestScore = 2350,
                        isCompleted = true
                    ),
                    UnitTopic(
                        id = "g12_u2",
                        grade = 12,
                        unitNumber = 2,
                        title = "Unit 2: A Multicultural World",
                        description = "Đa văn hóa, sự hội nhập và giữ gìn bản sắc",
                        difficulty = "Nâng cao HSG",
                        words = listOf(
                            WordItem("g12_w4", "Assimilation", "/əˌsɪm.ɪˈleɪ.ʃən/", "Sự đồng hóa văn hóa", "Immigrants adapt without total assimilation.", "Người nhập cư thích nghi mà không bị đồng hóa hoàn toàn.", 12, 2, listOf("Sự cô lập cách ly", "Xung đột chủng tộc", "Sự bảo thủ cực đoan")),
                            WordItem("g12_w5", "Heritage", "/ˈher.ɪ.tɪdʒ/", "Di sản văn hóa truyền đời", "Folk music is our treasured heritage.", "Âm nhạc dân gian là di sản quý báu của chúng ta.", 12, 2, listOf("Trào lưu nhất thời", "Công nghệ mới xuất hiện", "Tài sản nợ nần"))
                        ),
                        grammarLesson = getGrammarDatabase()[12]?.getOrNull(1),
                        bestScore = 0,
                        isCompleted = false
                    ),
                    UnitTopic(
                        id = "g12_u3",
                        grade = 12,
                        unitNumber = 3,
                        title = "Unit 3: Green Living & Eco-Action",
                        description = "Đa dạng sinh học, thảm họa sinh thái và năng lượng sạch",
                        difficulty = "Nâng cao HSG",
                        words = listOf(
                            WordItem("g12_w6", "Catastrophic", "/ˌkæt.əˈstrɒf.ɪk/", "Thảm họa, thảm khốc tàn khốc", "A catastrophic storm damaged the coast.", "Cơn bão thảm khốc đã tàn phá vùng bờ biển.", 12, 3, listOf("Nhẹ nhàng thoáng qua", "Có lợi cho sinh vật", "Bình yên vô hại")),
                            WordItem("g12_w7", "Biodiversity", "/ˌbaɪ.əʊ.daɪˈvɜː.sə.ti/", "Đa dạng sinh học của các loài", "Protecting biodiversity ensures food security.", "Bảo vệ đa dạng sinh học đảm bảo an ninh lương thực.", 12, 3, listOf("Ô nhiễm kim loại nặng", "Sự suy giảm dân số", "Khai thác cạn kiệt"))
                        ),
                        grammarLesson = getGrammarDatabase()[12]?.getOrNull(2),
                        bestScore = 1800,
                        isCompleted = false
                    )
                )
            )
        )
    }

    fun getLeaderboard(currentUser: UserProfile? = null, isMonthly: Boolean = true): List<LeaderboardEntry> {
        val allUsers = mutableListOf<UserProfile>()
        allUsers.addAll(_registeredUsers.value)
        if (currentUser != null) {
            val existingIndex = allUsers.indexOfFirst { it.uid == currentUser.uid }
            if (existingIndex >= 0) {
                allUsers[existingIndex] = currentUser
            } else {
                allUsers.add(currentUser)
            }
        }

        if (allUsers.isEmpty()) {
            return emptyList()
        }

        val sorted = if (isMonthly) {
            allUsers.sortedWith(compareByDescending<UserProfile> { it.monthlyScore }.thenByDescending { it.highestScore })
        } else {
            allUsers.sortedWith(compareByDescending<UserProfile> { it.highestScore }.thenByDescending { it.xp })
        }

        return sorted.mapIndexed { index, user ->
            val pts = if (isMonthly) user.monthlyScore else user.highestScore
            val badge = when {
                index == 0 -> "Quán Quân"
                index == 1 -> "Á Quân"
                index == 2 -> "Hạng Ba"
                user.level >= 10 -> "Thần Tốc"
                user.level >= 5 -> "Chiến Binh"
                else -> "Tân Binh"
            }
            LeaderboardEntry(
                rank = index + 1,
                name = user.displayName.ifBlank { "Học sinh THPT Lương Phú" },
                className = user.getDisplayClassName(),
                score = user.highestScore,
                monthlyScore = user.monthlyScore,
                level = user.level,
                grade = user.calculateCurrentGrade(),
                badge = badge,
                avatarColor = user.avatarColor
            )
        }
    }
}
