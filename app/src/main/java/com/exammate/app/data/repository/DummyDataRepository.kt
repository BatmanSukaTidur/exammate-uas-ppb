package com.exammate.app.data.repository

import com.exammate.app.data.model.DashboardStats
import com.exammate.app.data.model.Soal
import com.exammate.app.data.model.StatusUjian
import com.exammate.app.data.model.TeacherQuestionCategory
import com.exammate.app.data.model.Ujian
import com.exammate.app.data.model.User
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import javax.inject.Inject
import javax.inject.Singleton

data class StudentExamResult(
    val studentNis: String,
    val studentName: String,
    val examId: Int,
    val mapel: String,
    val score: Int,
    val total: Int,
    val correct: Int,
    val wrong: Int = 0,
    val unanswered: Int = 0,
    val submittedAt: String = LocalDate.now().toString()
)

@Singleton
class DummyDataRepository @Inject constructor() {

    private val soalMatematika = listOf(
        Soal(1, 1, "Hasil dari 25 + 37 adalah...", listOf("52", "62", "72", "82", "92"), 0),
        Soal(2, 2, "Berapakah akar kuadrat dari 144?", listOf("10", "11", "12", "13", "14"), 2),
        Soal(3, 3, "Jika x + 5 = 12, maka nilai x adalah...", listOf("5", "6", "7", "8", "9"), 2),
        Soal(4, 4, "Luas lingkaran dengan jari-jari 7 cm adalah... (π = 22/7)", listOf("144 cm²", "154 cm²", "164 cm²", "174 cm²", "184 cm²"), 1),
        Soal(5, 5, "15% dari 200 adalah...", listOf("20", "25", "30", "35", "40"), 2),
        Soal(6, 6, "Sebuah segitiga siku-siku memiliki sisi tegak 6 cm dan 8 cm. Panjang sisi miringnya adalah...", listOf("10 cm", "12 cm", "14 cm", "16 cm", "18 cm"), 0),
        Soal(7, 7, "Hasil dari (-12) × 5 adalah...", listOf("-60", "-50", "50", "60", "70"), 0),
        Soal(8, 8, "Bentuk sederhana dari 8/12 adalah...", listOf("1/2", "2/3", "3/4", "4/5", "5/6"), 1),
        Soal(9, 9, "Volume kubus dengan rusuk 5 cm adalah...", listOf("25 cm³", "75 cm³", "100 cm³", "125 cm³", "150 cm³"), 3),
        Soal(10, 10, "Jika a = 3 dan b = 4, maka nilai a² + b² adalah...", listOf("20", "25", "30", "35", "40"), 1)
    )

    private val soalBIndo = listOf(
        Soal(1, 1, "Sinonim dari kata \"Berdikari\" adalah...", listOf("Bergantung", "Mandiri", "Bersama", "Berpisah", "Bekerja"), 1),
        Soal(2, 2, "Kalimat berikut yang menggunakan kata baku adalah...", listOf("Aktipitas belajar berjalan lancar", "Aktivitas belajar berjalan lancar", "Aktifitas belajar berjalan lancar", "Aktivitas beladjar berjalan lancar", "Aktipitas beladjar berjalan lancar"), 1),
        Soal(3, 3, "Majas yang menggunakan kata-kata kiasan untuk menyatakan perbandingan disebut...", listOf("Majas Personifikasi", "Majas Metafora", "Majas Litotes", "Majas Hiperbola", "Majas Ironi"), 1),
        Soal(4, 4, "Ide pokok paragraf disebut juga...", listOf("Kalimat penjelas", "Kalimat utama", "Kata hubung", "Kata baku", "Kata serapan"), 1),
        Soal(5, 5, "Penulisan alamat surat yang benar adalah...", listOf("Kepada Yth. Bapak Kepala Sekolah", "Yth. Bapak Kepala Sekolah", "Kepada Bapak Kepala Sekolah", "Bapak Kepala Sekolah Yth.", "Yth. Bapak Kepala Sekolah di tempat"), 4),
        Soal(6, 6, "Antonim dari kata \"Keras\" adalah...", listOf("Padat", "Kasar", "Lembek", "Kencang", "Ringan"), 2),
        Soal(7, 7, "Puisi yang berasal dari Melayu klasik dan memiliki sajak a-b-a-b disebut...", listOf("Pantun", "Syair", "Gurindam", "Karmina", "Seloka"), 0),
        Soal(8, 8, "Kata depan \"di\" yang penulisannya benar terdapat pada kalimat...", listOf("Disekolah", "di rumah", "dirumah", "diSekolah", "DiRumah"), 1),
        Soal(9, 9, "Cerita rakyat yang dianggap benar-benar terjadi dan bersifat suci disebut...", listOf("Legenda", "Mite", "Fabel", "Sage", "Dongeng"), 1),
        Soal(10, 10, "Bacaan yang mengandung fakta dan opini disebut...", listOf("Fiksi", "Nonfiksi", "Berita", "Cerpen", "Drama"), 2)
    )

    private val soalInggris = listOf(
        Soal(1, 1, "\"What is the meaning of 'Beautiful'?\"", listOf("Cantik", "Tampan", "Pintar", "Baik", "Indah"), 0),
        Soal(2, 2, "\"She ___ to school every day.\"", listOf("go", "goes", "going", "went", "gone"), 1),
        Soal(3, 3, "\"The book is ___ the table.\"", listOf("in", "on", "at", "under", "between"), 1),
        Soal(4, 4, "\"Arrange: is - this - book - your?\"", listOf("Your this is book?", "Is this your book?", "Book is this your?", "This is your book?", "Is your this book?"), 1),
        Soal(5, 5, "\"Antonym of 'Big' is...\"", listOf("Large", "Small", "Huge", "Tall", "Wide"), 1),
        Soal(6, 6, "\"I have ___ apple.\"", listOf("a", "an", "the", "some", "any"), 1),
        Soal(7, 7, "\"We ___ playing football now.\"", listOf("is", "am", "are", "was", "were"), 2),
        Soal(8, 8, "\"Yesterday, I ___ to the library.\"", listOf("go", "goes", "going", "went", "gone"), 3),
        Soal(9, 9, "\"Father works ___ a doctor.\"", listOf("as", "like", "for", "by", "with"), 0),
        Soal(10, 10, "\"The students ___ study hard for the exam.\"", listOf("must", "can", "may", "will", "would"), 0)
    )

    private val soalIPA = listOf(
        Soal(1, 1, "Fotosintesis pada tumbuhan terjadi di organ...", listOf("Akar", "Batang", "Daun", "Bunga", "Buah"), 2),
        Soal(2, 2, "Sumber energi terbesar di bumi adalah...", listOf("Bulan", "Matahari", "Bintang", "Planet", "Komet"), 1),
        Soal(3, 3, "Perubahan wujud dari cair menjadi padat disebut...", listOf("Menguap", "Membeku", "Mencair", "Menyublim", "Mengembun"), 1),
        Soal(4, 4, "Alat pernapasan pada ikan adalah...", listOf("Paru-paru", "Trakea", "Insang", "Kulit", "Stomata"), 2),
        Soal(5, 5, "Planet terbesar dalam tata surya adalah...", listOf("Mars", "Venus", "Saturnus", "Jupiter", "Neptunus"), 3)
    )

    private val guruMatematika = User("11111", "Budi Santoso", "budi@email.com", "Guru", "SMAN 115 Jakarta", "GURU", "Matematika")
    private val guruBIndo = User("22222", "Siti Rahayu", "siti@email.com", "Guru", "SMAN 115 Jakarta", "GURU", "Bahasa Indonesia")
    private val guruInggris = User("33333", "John Doe", "john@email.com", "Guru", "SMAN 115 Jakarta", "GURU", "Bahasa Inggris")
    private val guruIPA = User("44444", "Rina Wijaya", "rina@email.com", "Guru", "SMAN 115 Jakarta", "GURU", "IPA")

    val daftarUjian: List<Ujian>
        get() {
            val today = LocalDate.now()
            val fmt = DateTimeFormatter.ISO_LOCAL_DATE
            val subjects = listOf(
                Triple("Matematika", soalMatematika, 90),
                Triple("Bahasa Indonesia", soalBIndo, 90),
                Triple("Bahasa Inggris", soalInggris, 60),
                Triple("IPA", soalIPA, 90),
                Triple("IPS", soalBIndo, 60),
                Triple("PKN", soalInggris, 60)
            )
            val base = today.dayOfYear % subjects.size
            return subjects.mapIndexed { i, (name, soal, durasi) ->
                val seqPos = (i - base + subjects.size) % subjects.size
                val dayOffset = seqPos - 2
                val date = today.plusDays(dayOffset.toLong())
                val status = when {
                    date.isBefore(today) -> StatusUjian.SELESAI
                    date.isEqual(today) -> StatusUjian.BERLANGSUNG
                    else -> StatusUjian.MENDATANG
                }
                Ujian(
                    id = i + 1,
                    mapel = name,
                    tanggal = date.format(fmt),
                    jam = "08:00",
                    durasiMenit = durasi,
                    status = status,
                    totalSoal = 10,
                    soal = soal
                )
            }
        }

    val questionBank = listOf(
        TeacherQuestionCategory(1, "Matematika", soalMatematika),
        TeacherQuestionCategory(2, "Bahasa Indonesia", soalBIndo),
        TeacherQuestionCategory(3, "Bahasa Inggris", soalInggris),
        TeacherQuestionCategory(4, "IPA", soalIPA)
    )

    private val dummyUsers = mutableMapOf<String, Pair<String, User>>(
        "11111" to ("11111" to guruMatematika),
        "22222" to ("22222" to guruBIndo),
        "33333" to ("33333" to guruInggris),
        "44444" to ("44444" to guruIPA)
    )

    private val studentResults = mutableMapOf<Int, MutableList<StudentExamResult>>()

    fun saveStudentResult(result: StudentExamResult) {
        val list = studentResults.getOrPut(result.examId) { mutableListOf() }
        val existing = list.indexOfFirst { it.studentNis == result.studentNis }
        if (existing >= 0) list[existing] = result
        else list.add(result)
    }

    fun getStudentResults(examId: Int): List<StudentExamResult> =
        studentResults[examId] ?: emptyList()

    fun getAllStudentResults(): List<StudentExamResult> =
        studentResults.values.flatten()

    fun registerDummyUser(user: User, password: String): Boolean {
        if (dummyUsers.containsKey(user.nis)) return false
        dummyUsers[user.nis] = password to user
        return true
    }

    fun getLoginUser(nisOrEmail: String, password: String): Pair<String?, User?> {
        val entry = dummyUsers[nisOrEmail]
        if (entry != null && entry.first == password) {
            return entry.second.role to entry.second
        }
        for ((_, stored) in dummyUsers) {
            if (stored.second.email == nisOrEmail && stored.first == password) {
                return stored.second.role to stored.second
            }
        }
        return null to null
    }

    fun getLoginUserByRole(nis: String, password: String, role: String): User? {
        val (foundRole, user) = getLoginUser(nis, password)
        return if (foundRole == role) user else null
    }

    fun getLoginUserByNis(nis: String): User? {
        return dummyUsers[nis]?.second
    }

    companion object {
        private val VALID_NIS_RANGE = (240109001L..240109010L).map { it.toString() }.toSet()

        fun isValidStudentNis(nis: String): Boolean {
            return nis.matches(Regex("^\\d+$")) && nis in VALID_NIS_RANGE
        }

        fun isValidName(nama: String): Boolean {
            return nama.matches(Regex("^[a-zA-Z\\s]+$"))
        }

        fun isValidEmail(email: String): Boolean {
            return email.matches(Regex("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$"))
        }

        fun isValidPassword(password: String): Boolean {
            if (password.length < 8) return false
            if (!password.any { it.isUpperCase() }) return false
            if (!password.any { it.isLowerCase() }) return false
            if (!password.any { it.isDigit() }) return false
            if (!password.any { !it.isLetterOrDigit() }) return false
            return true
        }
    }

    fun getUjianMendatang(): List<Ujian> = daftarUjian.filter { it.status == StatusUjian.MENDATANG }
    fun getUjianBerlangsung(): List<Ujian> = daftarUjian.filter { it.status == StatusUjian.BERLANGSUNG }
    fun getUjianSelesai(): List<Ujian> = daftarUjian.filter { it.status == StatusUjian.SELESAI }

    fun getUjianById(id: Int): Ujian? = daftarUjian.find { it.id == id }

    fun getStats(): DashboardStats {
        val selesai = daftarUjian.filter { it.status == StatusUjian.SELESAI }
        val rata = if (selesai.isNotEmpty()) selesai.sumOf { 85.0 } / selesai.size else 0.0
        return DashboardStats(
            totalUjian = daftarUjian.size,
            ujianSelesai = selesai.size,
            nilaiRataRata = rata
        )
    }

    fun getCategoryById(id: Int): TeacherQuestionCategory? = questionBank.find { it.id == id }
}
