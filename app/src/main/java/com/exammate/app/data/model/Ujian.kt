package com.exammate.app.data.model

data class Ujian(
    val id: Int,
    val mapel: String,
    val tanggal: String,
    val jam: String,
    val durasiMenit: Int,
    val status: StatusUjian,
    val totalSoal: Int,
    val soal: List<Soal>
)

enum class StatusUjian {
    MENDATANG, BERLANGSUNG, SELESAI
}

data class Soal(
    val id: Int,
    val nomor: Int,
    val pertanyaan: String,
    val opsi: List<String>,
    val jawabanBenar: Int
)
