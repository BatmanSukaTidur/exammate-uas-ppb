package com.exammate.app.domain.repository

import com.exammate.app.data.model.Ujian

interface ExamRepository {
    suspend fun fetchExamById(examId: Int): Result<Ujian>
    suspend fun fetchAllExams(): Result<List<Ujian>>
    suspend fun submitAnswers(
        examId: Int,
        studentUid: String,
        jawaban: Map<Int, Int>,
        flagged: Set<Int>,
        waktuPengerjaan: Int
    ): Result<Unit>
    suspend fun saveResult(
        examId: Int,
        studentUid: String,
        score: Int,
        totalSoal: Int,
        benar: Int,
        salah: Int,
        kosong: Int
    ): Result<Unit>
}
