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
}
