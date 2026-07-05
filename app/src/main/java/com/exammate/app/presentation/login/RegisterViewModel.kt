package com.exammate.app.presentation.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.exammate.app.data.repository.DummyDataRepository
import com.exammate.app.domain.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class RegisterUiState(
    val nis: String = "",
    val nama: String = "",
    val email: String = "",
    val password: String = "",
    val kelas: String = "",
    val sekolah: String = "SMAN 115 Jakarta",
    val role: String = "MURID",
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val nisError: String? = null,
    val namaError: String? = null,
    val emailError: String? = null,
    val passwordError: String? = null,
    val kelasError: String? = null
)

@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(RegisterUiState())
    val uiState: StateFlow<RegisterUiState> = _uiState.asStateFlow()

    fun onNisChange(nis: String) {
        _uiState.value = _uiState.value.copy(nis = nis, nisError = null)
    }

    fun onNamaChange(nama: String) {
        _uiState.value = _uiState.value.copy(nama = nama, namaError = null)
    }

    fun onEmailChange(email: String) {
        _uiState.value = _uiState.value.copy(email = email, emailError = null)
    }

    fun onPasswordChange(password: String) {
        _uiState.value = _uiState.value.copy(password = password, passwordError = null)
    }

    fun onKelasChange(kelas: String) {
        _uiState.value = _uiState.value.copy(kelas = kelas, kelasError = null)
    }

    fun register() {
        val state = _uiState.value

        if (state.nis.isBlank()) {
            _uiState.value = state.copy(nisError = "NIS tidak boleh kosong")
            return
        }
        if (!DummyDataRepository.isValidStudentNis(state.nis)) {
            _uiState.value = state.copy(nisError = "NIS tidak ditemukan")
            return
        }
        if (state.nama.isBlank()) {
            _uiState.value = state.copy(namaError = "Nama tidak boleh kosong")
            return
        }
        if (!DummyDataRepository.isValidName(state.nama)) {
            _uiState.value = state.copy(namaError = "Nama hanya boleh huruf dan spasi")
            return
        }
        if (state.email.isBlank()) {
            _uiState.value = state.copy(emailError = "Email tidak boleh kosong")
            return
        }
        if (!DummyDataRepository.isValidEmail(state.email)) {
            _uiState.value = state.copy(emailError = "Format email tidak valid")
            return
        }
        if (state.password.isBlank()) {
            _uiState.value = state.copy(passwordError = "Password tidak boleh kosong")
            return
        }
        if (!DummyDataRepository.isValidPassword(state.password)) {
            _uiState.value = state.copy(passwordError = "Password minimal 8 karakter, huruf besar, huruf kecil, angka, dan simbol")
            return
        }
        if (state.kelas.isBlank()) {
            _uiState.value = state.copy(kelasError = "Kelas tidak boleh kosong")
            return
        }
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)

            val isRegistered = authRepository.isNisRegistered(state.nis)
            if (isRegistered) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    nisError = "NIS sudah terdaftar"
                )
                return@launch
            }

            val registerEmail = if (state.email.contains("@")) state.email else "${state.nis}@exammate.app"
            val result = authRepository.register(
                nis = state.nis,
                nama = state.nama,
                email = registerEmail,
                password = state.password,
                kelas = state.kelas,
                sekolah = state.sekolah,
                role = state.role
            )

            result.fold(
                onSuccess = {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        isSuccess = true
                    )
                },
                onFailure = { e ->
                    val msg = e.message ?: "Pendaftaran gagal"
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        emailError = if (msg.contains("email", ignoreCase = true) || msg.contains("Firebase", ignoreCase = true)) msg else null,
                        passwordError = if (!msg.contains("email", ignoreCase = true) && !msg.contains("Firebase", ignoreCase = true)) msg else null
                    )
                }
            )
        }
    }
}
