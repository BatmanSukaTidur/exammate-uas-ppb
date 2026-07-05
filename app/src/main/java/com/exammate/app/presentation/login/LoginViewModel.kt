package com.exammate.app.presentation.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.exammate.app.domain.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class LoginUiState(
    val nis: String = "",
    val password: String = "",
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val isLoggedIn: Boolean = false,
    val role: String = "MURID"
)

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    fun onNisChange(nis: String) {
        _uiState.value = _uiState.value.copy(nis = nis, errorMessage = null)
    }

    fun onPasswordChange(password: String) {
        _uiState.value = _uiState.value.copy(password = password, errorMessage = null)
    }

    fun login() {
        val state = _uiState.value
        if (state.nis.isBlank()) {
            _uiState.value = state.copy(errorMessage = "NIS / Email tidak boleh kosong")
            return
        }
        if (state.password.isBlank()) {
            _uiState.value = state.copy(errorMessage = "Password tidak boleh kosong")
            return
        }

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)

            val email = if (state.nis.contains("@")) state.nis else "${state.nis}@exammate.app"
            val result = authRepository.login(state.nis, email, state.password)

            result.fold(
                onSuccess = { user ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        isLoggedIn = true,
                        role = user.role
                    )
                },
                onFailure = { e ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = e.message ?: "Login gagal"
                    )
                }
            )
        }
    }

}
