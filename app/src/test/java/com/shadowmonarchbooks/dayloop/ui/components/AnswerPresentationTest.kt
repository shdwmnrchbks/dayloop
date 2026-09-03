package com.shadowmonarchbooks.dayloop.ui.components

import kotlin.test.Test
import kotlin.test.assertEquals

class AnswerPresentationTest {
    @Test
    fun `answer kind label matches authored class question label`() {
        assertEquals("Class question", answerKindLabel("classQuestion"))
        assertEquals("Exam", answerKindLabel("exam"))
    }
}
