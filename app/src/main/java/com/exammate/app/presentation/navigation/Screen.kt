package com.exammate.app.presentation.navigation

sealed class Screen(val route: String) {
    data object Login : Screen("login")
    data object Register : Screen("register")
    data object Main : Screen("main")
    data object Dashboard : Screen("dashboard")
    data object DaftarUjian : Screen("daftar_ujian")
    data object HalamanUjian : Screen("halaman_ujian/{examId}") {
        fun createRoute(examId: Int) = "halaman_ujian/$examId"
    }
    data object RingkasanUjian : Screen("ringkasan_ujian/{examId}") {
        fun createRoute(examId: Int) = "ringkasan_ujian/$examId"
    }
    data object HasilUjian : Screen("hasil_ujian/{examId}/{score}/{total}/{correct}") {
        fun createRoute(examId: Int, score: Int = 0, total: Int = 0, correct: Int = 0) =
            "hasil_ujian/$examId/$score/$total/$correct"
    }
    data object Pengumuman : Screen("pengumuman")
    data object Profil : Screen("profil")

    // Teacher routes
    data object TeacherDashboard : Screen("teacher_dashboard")
    data object TeacherCreateExam : Screen("teacher_create_exam")
    data object TeacherResults : Screen("teacher_results")
    data object TeacherQuestionBank : Screen("teacher_question_bank")
    data object TeacherAddQuestion : Screen("teacher_add_question?categoryId={categoryId}") {
        fun createRoute(categoryId: Int = 0) = "teacher_add_question?categoryId=$categoryId"
    }
}
