package com.exammate.app.data.model

data class User(
    val nis: String,
    val nama: String,
    val email: String,
    val kelas: String,
    val sekolah: String,
    val role: String = "MURID",
    val mapel: String = ""
)
