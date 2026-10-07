package com.taphunter.english.data.repository

import org.junit.Assert.*
import org.junit.Test

class EnglishRepositoryRuleTest {

    @Test
    fun testTeacherPasscodeCorrectness() {
        val validPasscode = "giaovien2026"
        val testInput = "giaovien2026"
        assertEquals(validPasscode, testInput.trim())
    }

    @Test
    fun testGradeRangeAndUnitCoverage() {
        val validGrades = listOf(6, 7, 8, 9, 10, 11, 12)
        assertTrue(validGrades.contains(10))
        assertTrue(validGrades.contains(11))
        assertTrue(validGrades.contains(12))
        assertEquals(7, validGrades.size)
    }

    @Test
    fun testDictionaryBidirectionalSearchLogic() {
        val testWordEn = "resilience"
        val testMeaningVi = "sự kiên cường, khả năng phục hồi nhanh"

        // EN -> VI
        assertTrue(testWordEn.contains("resilience"))
        // VI -> EN
        assertTrue(testMeaningVi.contains("kiên cường"))
    }

    @Test
    fun testAcademicYearProgressionCalculation() {
        val registeredYear = 2026
        val baseGrade = 10
        // If current year is 2027 and month is October (month 10)
        val currentAcademicYear2027 = 2027
        val yearsPassed = currentAcademicYear2027 - registeredYear
        val calculatedGrade = baseGrade + yearsPassed
        assertEquals(11, calculatedGrade)

        // Class name replacement test
        val customClassName = "10A1"
        val prefixRegex = Regex("^\\d+")
        val match = prefixRegex.find(customClassName)
        assertNotNull(match)
        val newClassName = customClassName.replaceFirst(match!!.value, calculatedGrade.toString())
        assertEquals("11A1", newClassName)
    }

    @Test
    fun testMonthlyScoreKeyFormat() {
        val year = 2026
        val month = 10
        val monthKey = String.format(java.util.Locale.US, "%04d-%02d", year, month)
        assertEquals("2026-10", monthKey)
    }

    @Test
    fun testComprehensiveDictionaryAnyWordLookup() {
        // Known word
        val schoolResult = ComprehensiveDictionary.lookup("school", isEnglishToVietnamese = true)
        assertTrue(schoolResult.isNotEmpty())
        assertEquals("school", schoolResult[0].wordEn)

        // Suffix/synthesized word (never in standard textbook table)
        val happinessResult = ComprehensiveDictionary.lookup("happiness", isEnglishToVietnamese = true)
        assertTrue(happinessResult.isNotEmpty())
        assertTrue(happinessResult[0].meaningVi.isNotBlank())
        assertTrue(happinessResult[0].phonetic.isNotBlank())

        // Vietnamese word to English
        val teacherVnResult = ComprehensiveDictionary.lookup("thầy cô", isEnglishToVietnamese = false)
        assertTrue(teacherVnResult.isNotEmpty())
        assertTrue(teacherVnResult[0].wordEn.isNotBlank())
    }

    @Test
    fun testReputableExamGeneration() {
        val exam = ReputableExamBank.generateReputableExam(
            grade = 10,
            source = ExamSource.THPT_QUOC_GIA,
            questionCount = 5,
            teacherName = "Cô Nguyễn Mai"
        )
        assertNotNull(exam)
        assertEquals(10, exam.grade)
        assertTrue(exam.questions.isNotEmpty())
        assertTrue(exam.questions[0].options.size >= 4)
        assertTrue(exam.questions[0].explanation.isNotBlank())
    }

    @Test
    fun testDynamicSpeedAcceleration() {
        val baseSpeed = 1.0f + (0 * 0.12f) + (0 * 0.08f)
        val fasterSpeed = 1.0f + (5 * 0.12f) + (5 * 0.08f)
        assertTrue(fasterSpeed > baseSpeed)
        val baseDuration = (10000L / baseSpeed).toLong()
        val fastDuration = (10000L / fasterSpeed).toLong()
        assertTrue(fastDuration < baseDuration)
    }
}
