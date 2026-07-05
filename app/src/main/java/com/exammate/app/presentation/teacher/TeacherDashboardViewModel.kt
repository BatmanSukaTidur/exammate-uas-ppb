package com.exammate.app.presentation.teacher

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.exammate.app.domain.repository.AuthRepository
import com.exammate.app.domain.repository.ExamRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class TeacherDashboardUiState(
    val guruName: String = "Guru",
    val sekolah: String = "",
    val mapel: String = "",
    val totalUjian: Int = 0,
    val totalSoal: Int = 4,
    val totalSiswa: Int = 0,
    val isLoading: Boolean = true
)

@HiltViewModel
class TeacherDashboardViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val examRepository: ExamRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(TeacherDashboardUiState())
    val uiState: StateFlow<TeacherDashboardUiState> = _uiState.asStateFlow()

    init {
        loadData()
    }

    private fun loadData() {
        viewModelScope.launch {
            val user = authRepository.getSavedUser()

            val result = examRepository.fetchAllExams()
            val ujianCount = result.getOrNull()?.size ?: 0

            _uiState.value = TeacherDashboardUiState(
                guruName = user?.nama ?: "Guru",
                sekolah = user?.sekolah ?: "",
                mapel = user?.mapel ?: "",
                totalUjian = ujianCount,
                totalSiswa = 0,
                isLoading = false
            )
        }
    }
}
