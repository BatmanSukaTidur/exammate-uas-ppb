package com.exammate.app.data.remote.firebase

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.getValue
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FirebaseAuthSource @Inject constructor() {

    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseDatabase.getInstance()

    suspend fun signUp(email: String, password: String, nama: String = ""): Result<FirebaseUser> {
        return try {
            val authResult = auth.createUserWithEmailAndPassword(email, password).await()
            val firebaseUser = authResult.user ?: return Result.failure(Exception("Registration failed"))
            if (nama.isNotBlank()) {
                val profileUpdates = com.google.firebase.auth.UserProfileChangeRequest.Builder()
                    .setDisplayName(nama)
                    .build()
                firebaseUser.updateProfile(profileUpdates).await()
            }
            Result.success(firebaseUser)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun saveUserData(uid: String, user: com.exammate.app.data.model.User) {
        db.getReference("users").child(uid).setValue(user).await()
    }

    suspend fun signIn(email: String, password: String): Result<FirebaseUser> {
        return try {
            val authResult = auth.signInWithEmailAndPassword(email, password).await()
            val firebaseUser = authResult.user ?: return Result.failure(Exception("User not found"))
            Result.success(firebaseUser)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun isNisRegistered(nis: String): Boolean {
        return try {
            val snapshot = db.getReference("users")
                .orderByChild("nis")
                .equalTo(nis)
                .get()
                .await()
            snapshot.exists() && snapshot.childrenCount > 0
        } catch (_: Exception) {
            false
        }
    }

    suspend fun getUserData(uid: String): Result<com.exammate.app.data.model.User> {
        return try {
            val snapshot = db.getReference("users").child(uid).get().await()
            val fbUser = snapshot.getValue<com.exammate.app.data.remote.firebase.FirebaseUser>()
                ?: return Result.failure(Exception("User data not found"))
            val user = com.exammate.app.data.model.User(
                nis = fbUser.nis,
                nama = fbUser.nama,
                email = fbUser.email,
                kelas = fbUser.kelas,
                sekolah = fbUser.sekolah,
                role = fbUser.role.ifBlank { "MURID" },
                mapel = fbUser.mapel
            )
            Result.success(user)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun getCurrentFirebaseUser(): FirebaseUser? = auth.currentUser

    fun signOut() = auth.signOut()
}
