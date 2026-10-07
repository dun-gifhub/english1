package com.taphunter.english.data.models

enum class UserRole {
    STUDENT,
    TEACHER
}

data class UserProfile(
    val uid: String = "",
    val email: String = "",
    val displayName: String = "",
    val customClassName: String = "10A1", // Tên lớp học, ví dụ 10A1, 11A2, 12A3
    val baseGrade: Int = 10, // Khối lớp gốc khi đăng ký (6..12)
    val registeredAcademicYear: Int = 2026, // Năm học bắt đầu đăng ký (bắt đầu tháng 9 hàng năm)
    val friendCode: String = "",
    val role: UserRole = UserRole.STUDENT,
    val selectedGrade: Int = 10, // 6..12
    val level: Int = 1,
    val xp: Int = 0,
    val highestScore: Int = 0,
    val monthlyScore: Int = 0,
    val lastScoreMonthKey: String = "", // Ví dụ "2026-10"
    val streakDays: Int = 1,
    val avatarColor: Long = 0xFF00E5FF,
    val status: String = "Sẵn sàng săn từ vựng!"
) {
    /**
     * Tự động tính toán khối lớp hiện tại theo năm học.
     * Tại Việt Nam, năm học bắt đầu vào tháng 9 (Tháng 9 - Tháng 5 năm sau là một niên khóa).
     * Ví dụ: Học sinh đăng ký lớp 10 vào niên khóa 2026-2027 (bắt đầu 09/2026).
     * Đến tháng 09/2027 (niên khóa 2027-2028), lớp sẽ tự động tăng lên 11!
     */
    fun calculateCurrentGrade(): Int {
        val calendar = java.util.Calendar.getInstance()
        val currentYear = calendar.get(java.util.Calendar.YEAR)
        val currentMonth = calendar.get(java.util.Calendar.MONTH) + 1 // 1..12
        val currentAcademicYear = if (currentMonth >= 9) currentYear else currentYear - 1
        val yearsPassed = maxOf(0, currentAcademicYear - registeredAcademicYear)
        return minOf(13, baseGrade + yearsPassed)
    }

    /**
     * Trả về tên lớp hiển thị (tự động cập nhật số lớp theo năm học).
     * Ví dụ: 10A1 -> 11A1 sau 1 năm học.
     */
    fun getDisplayClassName(): String {
        val grade = calculateCurrentGrade()
        if (grade > 12) {
            return "Cựu học sinh Lương Phú (K$registeredAcademicYear)"
        }
        val trimmed = customClassName.trim()
        if (trimmed.isNotBlank()) {
            val prefixRegex = Regex("^\\d+")
            val match = prefixRegex.find(trimmed)
            return if (match != null) {
                trimmed.replaceFirst(match.value, grade.toString())
            } else {
                "Lớp $grade ($trimmed)"
            }
        }
        return "Lớp $grade"
    }

    /**
     * Niên khóa hiện tại (ví dụ: "2026 - 2027")
     */
    fun getCurrentSchoolYearString(): String {
        val calendar = java.util.Calendar.getInstance()
        val currentYear = calendar.get(java.util.Calendar.YEAR)
        val currentMonth = calendar.get(java.util.Calendar.MONTH) + 1
        val startYear = if (currentMonth >= 9) currentYear else currentYear - 1
        return "$startYear - ${startYear + 1}"
    }
}

data class WordItem(
    val id: String = "",
    val word: String = "",
    val phonetic: String = "",
    val meaningVi: String = "",
    val exampleEn: String = "",
    val exampleVi: String = "",
    val grade: Int = 10,
    val unitNumber: Int = 1,
    val distractorsVi: List<String> = emptyList(),
    val partOfSpeech: String = "n" // n, v, adj, adv
)

data class GrammarLesson(
    val id: String = "",
    val grade: Int = 10,
    val title: String = "",
    val formula: String = "",
    val explanationVi: String = "",
    val exampleEn: String = "",
    val exampleVi: String = "",
    val usageNotes: String = ""
)

data class UnitTopic(
    val id: String = "",
    val grade: Int = 10,
    val unitNumber: Int = 1,
    val title: String = "",
    val description: String = "",
    val difficulty: String = "Cơ bản", // "Cơ bản", "Trung cấp", "Nâng cao"
    val words: List<WordItem> = emptyList(),
    val grammarLesson: GrammarLesson? = null,
    val bestScore: Int = 0,
    val isCompleted: Boolean = false
)

data class Subject(
    val id: String = "",
    val grade: Int = 10,
    val title: String = "",
    val subtitle: String = "",
    val iconCategory: String = "school",
    val units: List<UnitTopic> = emptyList()
)

data class CustomQuestion(
    val id: String = "",
    val question: String = "",
    val options: List<String> = emptyList(),
    val correctIndex: Int = 0,
    val explanation: String = ""
)

data class TeacherAssignment(
    val id: String = "",
    val teacherUid: String = "",
    val teacherName: String = "",
    val grade: Int = 10,
    val title: String = "",
    val description: String = "",
    val questions: List<CustomQuestion> = emptyList(),
    val createdAt: Long = System.currentTimeMillis()
)

data class DictionaryEntry(
    val wordEn: String = "",
    val phonetic: String = "",
    val partOfSpeech: String = "",
    val meaningVi: String = "",
    val exampleEn: String = "",
    val exampleVi: String = "",
    val synonyms: List<String> = emptyList()
)

data class Friend(
    val uid: String = "",
    val friendCode: String = "",
    val displayName: String = "",
    val status: String = "",
    val isOnline: Boolean = true,
    val currentActivity: String = "",
    val avatarColor: Long = 0xFF00E5FF
)

data class CallRoom(
    val roomId: String = "",
    val roomCode: String = "",
    val title: String = "",
    val hostName: String = "",
    val participants: List<Friend> = emptyList(),
    val currentWordTopic: String = "Lớp 10 - Unit 1",
    val isMicOn: Boolean = true
)

data class LeaderboardEntry(
    val rank: Int = 1,
    val name: String = "",
    val className: String = "Lớp 10A1",
    val score: Int = 0,
    val monthlyScore: Int = 0,
    val level: Int = 1,
    val grade: Int = 10,
    val badge: String = "",
    val avatarColor: Long = 0xFF00E5FF
)
