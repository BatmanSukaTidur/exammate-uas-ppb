package com.exammate.app.domain.repository

import com.exammate.app.data.model.User

interface AuthRepository {
    suspend fun login(nis: String, email: String, password: String): Result<User>
    suspend fun register(nis: String, nama: String, email: String, password: String, kelas: String, sekolah: String, role: String): Result<User>
    suspend fun isNisRegistered(nis: String): Boolean
    fun getSavedUser(): User?
    fun getSavedRole(): String?
    fun isLoggedIn(): Boolean
    fun logout()
    fun isExamCompleted(examId: Int): Boolean
    fun markExamCompleted(examId: Int)
    fun saveExamResult(examId: Int, score: Int, total: Int, correct: Int, answeredCount: Int)
    fun getExamScore(examId: Int): Int
    fun getExamCorrect(examId: Int): Int
    fun getExamTotal(examId: Int): Int
    fun getExamAnsweredCount(examId: Int): Int
    fun getAverageScore(): Double
}
