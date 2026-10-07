package com.taphunter.english.data.models

enum class UserRole {
    STUDENT,
    TEACHER
}

data class UserProfile(
    val uid: String = "",
    val email: String = "",
    val displayName: String = "",
    val friendCode: String = "",
    val role: UserRole = UserRole.STUDENT,
    val selectedGrade: Int = 10, // 6..12
    val level: Int = 1,
    val xp: Int = 120,
    val highestScore: Int = 1540,
    val streakDays: Int = 5,
    val avatarColor: Long = 0xFF00E5FF,
    val status: String = "Sẵn sàng săn từ vựng!"
)

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
    val score: Int = 0,
    val level: Int = 1,
    val grade: Int = 10,
    val badge: String = ""
)
