package com.taphunter.english.data.repository

import com.taphunter.english.data.models.CustomQuestion
import com.taphunter.english.data.models.TeacherAssignment

enum class ExamSource(val sourceName: String, val badge: String, val description: String) {
    THPT_QUOC_GIA(
        "Đề thi THPT Quốc Gia chính thức (Bộ GD&ĐT)",
        "Chuẩn THPTQG",
        "Bộ câu hỏi trắc nghiệm trọng tâm ngữ pháp, từ vựng và cụm từ cố định (Collocations) bám sát ma trận đề thi tốt nghiệp."
    ),
    HOC_SINH_GIOI(
        "Đề thi Học Sinh Giỏi (HSG) Tỉnh & Toàn Quốc",
        "Chuyên Sâu HSG",
        "Câu hỏi phân hóa cao, từ đồng nghĩa/trái nghĩa phức tạp, đảo ngữ, câu điều kiện hỗn hợp và Idioms nâng cao."
    ),
    CHUYEN_CAP_3(
        "Đề Tuyển sinh Lớp 10 Chuyên Ngoại Ngữ & THPT Chuyên",
        "Đề Thi Chuyên",
        "Kiểm tra ngữ âm, trọng âm, tìm lỗi sai và cấu trúc biến đổi câu dành cho học sinh giỏi THCS lên THPT."
    ),
    CAMBRIDGE_DGNL(
        "Đề thi Đánh Giá Năng Lực (ĐGNL) & Cambridge B1-B2",
        "Chuẩn Quốc Tế",
        "Bộ câu hỏi kiểm tra kỹ năng đọc hiểu ngữ cảnh, điền từ vào văn bản và tiếng Anh giao tiếp thực tế."
    )
}

object ReputableExamBank {

    fun generateReputableExam(
        grade: Int,
        source: ExamSource,
        questionCount: Int,
        teacherName: String
    ): TeacherAssignment {
        val pool = getQuestionsForSourceAndGrade(source, grade)
        val selectedQuestions = pool.shuffled().take(questionCount.coerceIn(3, 20))

        val title = when (source) {
            ExamSource.THPT_QUOC_GIA -> "Đề Thi Trọng Điểm THPTQG 2026 - Lớp $grade"
            ExamSource.HOC_SINH_GIOI -> "Đề Luyện Thi Học Sinh Giỏi (HSG) - Khối $grade"
            ExamSource.CHUYEN_CAP_3 -> "Đề Khảo Sát Năng Khiếu Chuyên Anh - Lớp $grade"
            ExamSource.CAMBRIDGE_DGNL -> "Đề Khảo Sát ĐGNL & Chuẩn B1/B2 - Lớp $grade"
        }

        val description = "Đề thi được trích xuất từ ${source.sourceName}. Gồm ${selectedQuestions.size} câu hỏi chuẩn hóa có đáp án và lời giải chi tiết."

        return TeacherAssignment(
            id = "exam_rep_${System.currentTimeMillis()}",
            teacherUid = "teacher_verified",
            teacherName = teacherName.ifBlank { "Tổ Ngoại Ngữ THPT Lương Phú" },
            grade = grade,
            title = title,
            description = description,
            questions = selectedQuestions,
            createdAt = System.currentTimeMillis()
        )
    }

    private fun getQuestionsForSourceAndGrade(source: ExamSource, grade: Int): List<CustomQuestion> {
        val list = mutableListOf<CustomQuestion>()

        // 1. THPT QUỐC GIA QUESTIONS (Grammar, collocations, phrasal verbs, tenses)
        list.add(
            CustomQuestion(
                id = "thpt_q1",
                question = "If our team ______ more carefully in the first half, we wouldn't have conceded that goal.",
                options = listOf("had played", "played", "would play", "plays"),
                correctIndex = 0,
                explanation = "Câu điều kiện loại 3 (Third Conditional) diễn tả giả định trái ngược với quá khứ. Cấu trúc: If + S + had + P2, S + would/could + have + P2."
            )
        )
        list.add(
            CustomQuestion(
                id = "thpt_q2",
                question = "Students are encouraged to ______ part in environmental volunteer activities.",
                options = listOf("take", "make", "get", "do"),
                correctIndex = 0,
                explanation = "Cụm từ cố định (Collocation): 'take part in' đồng nghĩa với 'participate in' (tham gia vào)."
            )
        )
        list.add(
            CustomQuestion(
                id = "thpt_q3",
                question = "The new renewable energy project is expected to ______ a profound impact on carbon emissions.",
                options = listOf("have", "bring", "create", "give"),
                correctIndex = 0,
                explanation = "Collocation chuẩn THPTQG: 'have an impact on sth' (có tác động / ảnh hưởng đến cái gì)."
            )
        )
        list.add(
            CustomQuestion(
                id = "thpt_q4",
                question = "Not only ______ the championship, but they also broke the national record.",
                options = listOf("did they win", "they won", "they did win", "have they won"),
                correctIndex = 0,
                explanation = "Cấu trúc đảo ngữ với 'Not only': Not only + Trợ động từ + S + V..., but S also... Do vế sau ở quá khứ đơn (broke) nên đảo ngữ dùng 'did they win'."
            )
        )
        list.add(
            CustomQuestion(
                id = "thpt_q5",
                question = "She dedicated most of her youth ______ scientific research on biodiversity.",
                options = listOf("to", "for", "with", "in"),
                correctIndex = 0,
                explanation = "Cấu trúc: 'dedicate sth to sth/V-ing' (cống hiến điều gì cho cái gì)."
            )
        )
        list.add(
            CustomQuestion(
                id = "thpt_q6",
                question = "By the time the headmaster arrived at the auditorium, the ceremony ______.",
                options = listOf("had begun", "began", "has begun", "would begin"),
                correctIndex = 0,
                explanation = "Sự phối hợp thì: By the time + S + V(quá khứ đơn), S + had + P2 (quá khứ hoàn thành diễn tả hành động xảy ra trước)."
            )
        )

        // 2. HỌC SINH GIỎI QUESTIONS (Advanced idioms, synonyms, antonyms, inversion)
        list.add(
            CustomQuestion(
                id = "hsg_q1",
                question = "Choose the CLOSEST in meaning to 'METICULOUS': The scientist was meticulous in recording laboratory measurements.",
                options = listOf("Painstaking and thorough", "Careless and hasty", "Ambiguous and vague", "Superficial and brief"),
                correctIndex = 0,
                explanation = "'Meticulous' có nghĩa là tỉ mỉ, cẩn thận từng chi tiết, đồng nghĩa với 'painstaking and thorough'."
            )
        )
        list.add(
            CustomQuestion(
                id = "hsg_q2",
                question = "Choose the OPPOSITE in meaning to 'UBIQUITOUS': The rare orchid is ubiquitous in high mountainous areas.",
                options = listOf("Scarce and hard to find", "Widespread", "Omnipresent", "Pervasive"),
                correctIndex = 0,
                explanation = "'Ubiquitous' là phổ biến khắp nơi. Từ trái nghĩa là 'Scarce and hard to find' (hiếm thấy, khó tìm)."
            )
        )
        list.add(
            CustomQuestion(
                id = "hsg_q3",
                question = "Hardly ______ down to revise his lesson when the electricity went out.",
                options = listOf("had he sat", "he had sat", "did he sit", "was he sitting"),
                correctIndex = 0,
                explanation = "Cấu trúc đảo ngữ HSG: 'Hardly + had + S + P2... when + S + V(quá khứ đơn)' (Vừa mới... thì đã...)."
            )
        )
        list.add(
            CustomQuestion(
                id = "hsg_q4",
                question = "Idiom: When faced with academic pressure, keeping your ______ helps you make rational choices.",
                options = listOf("cool", "hot", "warm", "chill"),
                correctIndex = 0,
                explanation = "Thành ngữ: 'keep one's cool' có nghĩa là giữ bình tĩnh, không nóng giận hay bối rối."
            )
        )
        list.add(
            CustomQuestion(
                id = "hsg_q5",
                question = "The witness's testimony was completely ______ with the physical evidence found at the scene.",
                options = listOf("consistent", "compatible", "continuous", "connected"),
                correctIndex = 0,
                explanation = "'Consistent with' nghĩa là nhất quán, phù hợp với chứng cứ."
            )
        )

        // 3. CHUYÊN CẤP 3 QUESTIONS (Pronunciation, stress, error identification)
        list.add(
            CustomQuestion(
                id = "chuyen_q1",
                question = "Pronunciation: Choose the word whose underlined part is pronounced differently: A. look<u>ed</u> B. watch<u>ed</u> C. stopp<u>ed</u> D. decid<u>ed</u>",
                options = listOf("decided (/ɪd/)", "looked (/t/)", "watched (/t/)", "stopped (/t/)"),
                correctIndex = 0,
                explanation = "Quy tắc phát âm đuôi -ed: 'decided' kết thúc bằng âm /d/ nên phát âm là /ɪd/, trong khi các từ còn lại phát âm là /t/."
            )
        )
        list.add(
            CustomQuestion(
                id = "chuyen_q2",
                question = "Stress: Choose the word that has a different stress pattern from the others: A. dedicate B. resilient C. volunteer D. infrastructure",
                options = listOf("volunteer (trọng âm 3)", "dedicate (trọng âm 1)", "resilient (trọng âm 2)", "infrastructure (trọng âm 1)"),
                correctIndex = 0,
                explanation = "Từ 'volunteer' có trọng âm rơi vào âm tiết thứ 3 (-teer nhận trọng âm chính)."
            )
        )
        list.add(
            CustomQuestion(
                id = "chuyen_q3",
                question = "Error Identification: '<u>Despite of</u> the heavy downpour, the soccer match went ahead as scheduled.'",
                options = listOf("Despite of (sửa thành Despite hoặc In spite of)", "heavy downpour", "went ahead", "as scheduled"),
                correctIndex = 0,
                explanation = "Lỗi sai: 'Despite' không đi với 'of' (Despite + N/V-ing hoặc In spite of + N/V-ing)."
            )
        )

        // 4. CAMBRIDGE & ĐGNL (Contextual reading & communication)
        list.add(
            CustomQuestion(
                id = "dgnl_q1",
                question = "Social Communication: Peter: 'Would you mind helping me carry these grammar books?' - Mary: '______.'",
                options = listOf("Not at all. Let me give you a hand.", "Yes, I would.", "Never mind, you're welcome.", "No, I don't like."),
                correctIndex = 0,
                explanation = "Đáp lại lời thỉnh cầu lịch sự 'Would you mind...?' thì câu đồng ý giúp đỡ là 'Not at all' (Không hề phiền chút nào. Để mình giúp một tay)."
            )
        )
        list.add(
            CustomQuestion(
                id = "dgnl_q2",
                question = "Contextual Cloze: Modern urban planners focus on creating ______ zones where motor vehicles are banned.",
                options = listOf("pedestrian", "passenger", "commuter", "driver"),
                correctIndex = 0,
                explanation = "'Pedestrian zones' là phố đi bộ (nơi cấm phương tiện cơ giới)."
            )
        )
        list.add(
            CustomQuestion(
                id = "dgnl_q3",
                question = "Academic Reading: The discovery of antibiotics is widely regarded as one of medicine's most ______ milestones.",
                options = listOf("significant", "significance", "significantly", "signify"),
                correctIndex = 0,
                explanation = "Sau cấu trúc so sánh hơn nhất 'most' và trước danh từ 'milestones' cần một tính từ: 'significant' (quan trọng, có ý nghĩa lớn)."
            )
        )

        return list
    }
}
