package com.exammate.app.presentation.ujian

import androidx.lifecycle.ViewModel
import com.exammate.app.data.model.User
import com.exammate.app.domain.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

data class ProfilUiState(
    val user: User? = null
)

@HiltViewModel
class ProfilViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProfilUiState())
    val uiState: StateFlow<ProfilUiState> = _uiState.asStateFlow()

    init {
        _uiState.value = ProfilUiState(user = authRepository.getSavedUser())
    }

    fun loadUser() {
        _uiState.value = ProfilUiState(user = authRepository.getSavedUser())
    }

    fun logout() {
        authRepository.logout()
    }
}
