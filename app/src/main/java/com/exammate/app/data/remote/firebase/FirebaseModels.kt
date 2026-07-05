package com.exammate.app.data.remote.firebase

data class FirebaseUser(
    val nis: String = "",
    val nama: String = "",
    val email: String = "",
    val kelas: String = "",
    val sekolah: String = "",
    val role: String = "MURID"
)

data class FirebaseSoal(
    val id: Int = 0,
    val nomor: Int = 0,
    val pertanyaan: String = "",
    val opsi: List<String> = emptyList(),
    val jawabanBenar: Int = 0
)

data class FirebaseExam(
    val id: Int = 0,
    val mapel: String = "",
    val tanggal: String = "",
    val jam: String = "",
    val durasiMenit: Int = 0,
    val status: String = "",
    val token: String = "",
    val totalSoal: Int = 0,
    val dibuatOleh: String = "",
    val soal: Map<String, FirebaseSoal> = emptyMap()
)

data class FirebaseAnswer(
    val jawaban: Map<String, Int> = emptyMap(),
    val flagged: List<Int> = emptyList(),
    val waktuPengerjaan: Int = 0,
    val submittedAt: Long = 0
)

data class FirebaseResult(
    val score: Int = 0,
    val totalSoal: Int = 0,
    val benar: Int = 0,
    val salah: Int = 0,
    val kosong: Int = 0,
    val submittedAt: Long = 0
)
