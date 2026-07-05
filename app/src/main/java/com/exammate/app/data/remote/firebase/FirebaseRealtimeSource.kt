package com.exammate.app.data.remote.firebase

import com.exammate.app.data.model.Soal
import com.exammate.app.data.model.StatusUjian
import com.exammate.app.data.model.Ujian
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.getValue
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FirebaseRealtimeSource @Inject constructor() {

    private val db = FirebaseDatabase.getInstance()

    suspend fun fetchExamByToken(token: String): Result<Ujian> {
        return try {
            val snapshot = db.getReference("exams")
                .orderByChild("token")
                .equalTo(token)
                .get()
                .await()

            if (!snapshot.exists()) {
                return Result.failure(Exception("Ujian dengan token $token tidak ditemukan"))
            }

            val examEntry = snapshot.children.first()
            val fbExam = examEntry.getValue<FirebaseExam>()
                ?: return Result.failure(Exception("Data ujian tidak valid"))

            val status = when (fbExam.status) {
                "MENDATANG" -> StatusUjian.MENDATANG
                "BERLANGSUNG" -> StatusUjian.BERLANGSUNG
                "SELESAI" -> StatusUjian.SELESAI
                else -> StatusUjian.MENDATANG
            }

            val soalList = fbExam.soal.entries.map { (_, fbSoal) ->
                Soal(
                    id = fbSoal.id,
                    nomor = fbSoal.nomor,
                    pertanyaan = fbSoal.pertanyaan,
                    opsi = fbSoal.opsi,
                    jawabanBenar = fbSoal.jawabanBenar
                )
            }.sortedBy { it.nomor }

            val ujian = Ujian(
                id = fbExam.id,
                mapel = fbExam.mapel,
                tanggal = fbExam.tanggal,
                jam = fbExam.jam,
                durasiMenit = fbExam.durasiMenit,
                status = status,
                totalSoal = fbExam.totalSoal,
                soal = soalList
            )
            Result.success(ujian)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun fetchExamById(examId: Int): Result<Ujian> {
        return try {
            val snapshot = db.getReference("exams")
                .orderByChild("id")
                .equalTo(examId.toDouble())
                .get()
                .await()

            if (!snapshot.exists()) {
                return Result.failure(Exception("Ujian tidak ditemukan"))
            }

            val examEntry = snapshot.children.first()
            val fbExam = examEntry.getValue<FirebaseExam>()
                ?: return Result.failure(Exception("Data ujian tidak valid"))

            val status = when (fbExam.status) {
                "MENDATANG" -> StatusUjian.MENDATANG
                "BERLANGSUNG" -> StatusUjian.BERLANGSUNG
                "SELESAI" -> StatusUjian.SELESAI
                else -> StatusUjian.MENDATANG
            }

            val soalList = fbExam.soal.entries.map { (_, fbSoal) ->
                Soal(
                    id = fbSoal.id,
                    nomor = fbSoal.nomor,
                    pertanyaan = fbSoal.pertanyaan,
                    opsi = fbSoal.opsi,
                    jawabanBenar = fbSoal.jawabanBenar
                )
            }.sortedBy { it.nomor }

            val ujian = Ujian(
                id = fbExam.id,
                mapel = fbExam.mapel,
                tanggal = fbExam.tanggal,
                jam = fbExam.jam,
                durasiMenit = fbExam.durasiMenit,
                status = status,
                totalSoal = fbExam.totalSoal,
                soal = soalList
            )
            Result.success(ujian)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun fetchAllExams(): Result<List<Ujian>> {
        return try {
            val snapshot = db.getReference("exams").get().await()
            val ujianList = snapshot.children.mapNotNull { child ->
                val fbExam = child.getValue<FirebaseExam>() ?: return@mapNotNull null
                val status = when (fbExam.status) {
                    "MENDATANG" -> StatusUjian.MENDATANG
                    "BERLANGSUNG" -> StatusUjian.BERLANGSUNG
                    "SELESAI" -> StatusUjian.SELESAI
                    else -> return@mapNotNull null
                }
                val soalList = fbExam.soal.entries.map { (_, fbSoal) ->
                    Soal(
                        id = fbSoal.id,
                        nomor = fbSoal.nomor,
                        pertanyaan = fbSoal.pertanyaan,
                        opsi = fbSoal.opsi,
                        jawabanBenar = fbSoal.jawabanBenar
                    )
                }.sortedBy { it.nomor }

                Ujian(
                    id = fbExam.id,
                    mapel = fbExam.mapel,
                    tanggal = fbExam.tanggal,
                    jam = fbExam.jam,
                    durasiMenit = fbExam.durasiMenit,
                    status = status,
                    totalSoal = fbExam.totalSoal,
                    soal = soalList
                )
            }
            Result.success(ujianList)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun submitAnswers(
        examId: Int,
        studentUid: String,
        jawaban: Map<Int, Int>,
        flagged: Set<Int>,
        waktuPengerjaan: Int
    ): Result<Unit> {
        return try {
            val answerData = FirebaseAnswer(
                jawaban = jawaban.mapKeys { it.key.toString() },
                flagged = flagged.toList(),
                waktuPengerjaan = waktuPengerjaan,
                submittedAt = System.currentTimeMillis()
            )
            db.getReference("answers")
                .child(examId.toString())
                .child(studentUid)
                .setValue(answerData)
                .await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun saveResult(
        examId: Int,
        studentUid: String,
        score: Int,
        totalSoal: Int,
        benar: Int,
        salah: Int,
        kosong: Int
    ): Result<Unit> {
        return try {
            val resultData = FirebaseResult(
                score = score,
                totalSoal = totalSoal,
                benar = benar,
                salah = salah,
                kosong = kosong,
                submittedAt = System.currentTimeMillis()
            )
            db.getReference("results")
                .child(examId.toString())
                .child(studentUid)
                .setValue(resultData)
                .await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun fetchResults(examId: Int): Result<List<FirebaseResult>> {
        return try {
            val snapshot = db.getReference("results")
                .child(examId.toString())
                .get()
                .await()

            val results = snapshot.children.mapNotNull { child ->
                child.getValue<FirebaseResult>()
            }
            Result.success(results)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
