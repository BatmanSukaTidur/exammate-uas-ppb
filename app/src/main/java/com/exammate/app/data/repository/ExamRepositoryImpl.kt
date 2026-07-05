package com.exammate.app.data.repository

import com.exammate.app.data.model.Ujian
import com.exammate.app.data.remote.firebase.FirebaseRealtimeSource
import com.exammate.app.domain.repository.ExamRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ExamRepositoryImpl @Inject constructor(
    private val firebaseRTDB: FirebaseRealtimeSource,
    private val dummyRepo: DummyDataRepository
) : ExamRepository {

    override suspend fun fetchExamById(examId: Int): Result<Ujian> {
        val fbResult = firebaseRTDB.fetchExamById(examId)
        if (fbResult.isSuccess) return fbResult
        val dummy = dummyRepo.getUjianById(examId)
        return if (dummy != null) Result.success(dummy)
        else Result.failure(Exception("Ujian tidak ditemukan"))
    }

    override suspend fun fetchAllExams(): Result<List<Ujian>> {
        val fbResult = firebaseRTDB.fetchAllExams()
        if (fbResult.isSuccess && fbResult.getOrNull()!!.isNotEmpty()) return fbResult
        return Result.success(dummyRepo.daftarUjian)
    }

    override suspend fun submitAnswers(
        examId: Int,
        studentUid: String,
        jawaban: Map<Int, Int>,
        flagged: Set<Int>,
        waktuPengerjaan: Int
    ): Result<Unit> {
        return firebaseRTDB.submitAnswers(examId, studentUid, jawaban, flagged, waktuPengerjaan)
    }

    override suspend fun saveResult(
        examId: Int,
        studentUid: String,
        score: Int,
        totalSoal: Int,
        benar: Int,
        salah: Int,
        kosong: Int
    ): Result<Unit> {
        return firebaseRTDB.saveResult(examId, studentUid, score, totalSoal, benar, salah, kosong)
    }
}
