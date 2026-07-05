package com.exammate.app.data.repository

import android.content.Context
import android.content.SharedPreferences
import com.exammate.app.data.model.User
import com.exammate.app.data.remote.firebase.FirebaseAuthSource
import com.exammate.app.domain.repository.AuthRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context,
    private val firebaseAuth: FirebaseAuthSource,
    private val dummyRepo: DummyDataRepository
) : AuthRepository {

    private val prefs: SharedPreferences =
        context.getSharedPreferences("exammate_auth", Context.MODE_PRIVATE)

    override suspend fun register(
        nis: String, nama: String, email: String, password: String,
        kelas: String, sekolah: String, role: String
    ): Result<User> {
        val user = User(nis, nama, email, kelas, sekolah, role)

        val fbResult = try {
            firebaseAuth.signUp(email, password, nama)
        } catch (e: Exception) {
            val msg = e.message ?: ""
            if (msg.contains("email", ignoreCase = true) && msg.contains("already in use", ignoreCase = true)) {
                return Result.failure(Exception("Email sudah terdaftar, gunakan email lain"))
            }
            if (msg.contains("Firebase", ignoreCase = true) || msg.contains("network", ignoreCase = true)) {
                return Result.failure(Exception("Firebase tidak tersedia"))
            }
            return Result.failure(Exception("Pendaftaran gagal: ${e.message}"))
        }

        if (!fbResult.isSuccess) {
            return Result.failure(fbResult.exceptionOrNull() ?: Exception("Pendaftaran gagal"))
        }

        val firebaseUser = fbResult.getOrNull()!!
        try {
            firebaseAuth.saveUserData(firebaseUser.uid, user)
        } catch (_: Exception) { }

        saveSession(firebaseUser.uid, user)
        return Result.success(user)
    }

    override suspend fun login(nis: String, email: String, password: String): Result<User> {
        val loginEmail = if (email.isNotBlank()) email else "$nis@exammate.app"

        val fbResult = try {
            firebaseAuth.signIn(loginEmail, password)
        } catch (_: Exception) {
            Result.failure(Exception("Firebase tidak tersedia"))
        }

        if (fbResult.isSuccess) {
            val firebaseUser = fbResult.getOrNull()!!
            val userResult = try {
                firebaseAuth.getUserData(firebaseUser.uid)
            } catch (_: Exception) {
                Result.failure(Exception("Firebase tidak tersedia"))
            }
            if (userResult.isSuccess) {
                val user = userResult.getOrNull()!!
                val dummy = dummyRepo.getLoginUserByNis(nis)
                val enriched = if (user.mapel.isBlank() && dummy != null) {
                    user.copy(mapel = dummy.mapel, role = dummy.role)
                } else user
                saveSession(firebaseUser.uid, enriched)
                return Result.success(enriched)
            }
            val savedUser = getSavedUser()
            val dummy = dummyRepo.getLoginUserByNis(nis)
            val fallbackUser = User(
                nis = dummy?.nis ?: savedUser?.nis ?: loginEmail.substringBefore("@"),
                nama = dummy?.nama ?: savedUser?.nama ?: firebaseUser.displayName ?: loginEmail.substringBefore("@"),
                email = loginEmail,
                kelas = dummy?.kelas ?: savedUser?.kelas ?: "",
                sekolah = dummy?.sekolah ?: savedUser?.sekolah ?: "SMAN 115 Jakarta",
                role = dummy?.role ?: savedUser?.role ?: "MURID",
                mapel = dummy?.mapel ?: ""
            )
            saveSession(firebaseUser.uid, fallbackUser)
            return Result.success(fallbackUser)
        }

        val (_, dummyUser) = dummyRepo.getLoginUser(nis, password)
        if (dummyUser != null) {
            return migrateAndLogin(dummyUser, password, loginEmail)
        }

        val savedUser = getSavedUser()
        if (savedUser != null && (savedUser.nis == nis || savedUser.email == loginEmail)) {
            return migrateAndLogin(savedUser, password, loginEmail)
        }

        val nisExists = firebaseAuth.isNisRegistered(nis) || dummyRepo.getLoginUserByNis(nis) != null
        return if (nisExists) {
            Result.failure(Exception("Password salah"))
        } else {
            Result.failure(Exception("NIS tidak ditemukan"))
        }
    }

    private suspend fun migrateAndLogin(user: User, password: String, loginEmail: String): Result<User> {
        val signUpResult = try {
            firebaseAuth.signUp(loginEmail, password)
        } catch (_: Exception) {
            Result.failure(Exception("Firebase tidak tersedia"))
        }

        if (signUpResult.isSuccess) {
            val firebaseUser = signUpResult.getOrNull()!!
            try {
                firebaseAuth.saveUserData(firebaseUser.uid, user)
            } catch (_: Exception) { }
            saveSession(firebaseUser.uid, user)
        } else {
            val signInResult = try {
                firebaseAuth.signIn(loginEmail, password)
            } catch (_: Exception) {
                Result.failure(Exception("Firebase tidak tersedia"))
            }
            if (signInResult.isSuccess) {
                val firebaseUser = signInResult.getOrNull()!!
                saveSession(firebaseUser.uid, user)
            } else {
                saveSession(user.nis, user)
            }
        }

        return Result.success(user)
    }

    private fun saveSession(uid: String, user: User) {
        prefs.edit()
            .putString("uid", uid)
            .putString("role", user.role)
            .putString("nama", user.nama)
            .putString("nis", user.nis)
            .putString("email", user.email)
            .putString("kelas", user.kelas)
            .putString("sekolah", user.sekolah)
            .putString("mapel", user.mapel)
            .commit()
    }

    override fun getSavedUser(): User? {
        val uid = prefs.getString("uid", null) ?: return null
        return User(
            nis = prefs.getString("nis", "") ?: "",
            nama = prefs.getString("nama", "") ?: "",
            email = prefs.getString("email", "") ?: "",
            kelas = prefs.getString("kelas", "") ?: "",
            sekolah = prefs.getString("sekolah", "") ?: "",
            role = prefs.getString("role", "MURID") ?: "MURID",
            mapel = prefs.getString("mapel", "") ?: ""
        )
    }

    override fun getSavedRole(): String? = prefs.getString("role", null)

    override fun isLoggedIn(): Boolean = prefs.contains("uid")

    override fun isExamCompleted(examId: Int): Boolean {
        val nis = prefs.getString("nis", "") ?: ""
        if (nis.isBlank()) return false
        val completed = prefs.getString("completed_exams_$nis", "") ?: ""
        return completed.split(",").contains(examId.toString())
    }

    override fun markExamCompleted(examId: Int) {
        val nis = prefs.getString("nis", "") ?: ""
        if (nis.isBlank()) return
        val completed = prefs.getString("completed_exams_$nis", "") ?: ""
        val list = completed.split(",").filter { it.isNotBlank() }
        if (list.contains(examId.toString())) return
        val updated = if (list.isEmpty()) examId.toString() else "$completed,$examId"
        prefs.edit().putString("completed_exams_$nis", updated).commit()
    }

    override fun saveExamResult(examId: Int, score: Int, total: Int, correct: Int, answeredCount: Int) {
        prefs.edit()
            .putInt("exam_score_$examId", score)
            .putInt("exam_total_$examId", total)
            .putInt("exam_correct_$examId", correct)
            .putInt("exam_answered_$examId", answeredCount)
            .commit()
    }

    override fun getExamScore(examId: Int): Int = prefs.getInt("exam_score_$examId", -1)

    override fun getExamCorrect(examId: Int): Int = prefs.getInt("exam_correct_$examId", 0)

    override fun getExamTotal(examId: Int): Int = prefs.getInt("exam_total_$examId", 0)

    override fun getExamAnsweredCount(examId: Int): Int = prefs.getInt("exam_answered_$examId", 0)

    override fun getAverageScore(): Double {
        val nis = prefs.getString("nis", "") ?: ""
        val completed = prefs.getString("completed_exams_$nis", "") ?: ""
        val ids = completed.split(",").filter { it.isNotBlank() }
        if (ids.isEmpty()) return 0.0
        var total = 0
        var count = 0
        for (id in ids) {
            val score = prefs.getInt("exam_score_$id", -1)
            if (score >= 0) {
                total += score
                count++
            }
        }
        return if (count > 0) total.toDouble() / count else 0.0
    }

    override suspend fun isNisRegistered(nis: String): Boolean {
        if (firebaseAuth.isNisRegistered(nis)) return true
        if (dummyRepo.getLoginUserByNis(nis) != null) return true
        return false
    }

    override fun logout() {
        prefs.edit().clear().commit()
        firebaseAuth.signOut()
    }
}
