package com.exammate.app.data.model

data class TeacherQuestionCategory(
    val id: Int,
    val nama: String,
    val soal: List<Soal>
)
