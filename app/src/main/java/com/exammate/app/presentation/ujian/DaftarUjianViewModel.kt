package com.exammate.app.presentation.ujian

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.exammate.app.data.model.Ujian
import com.exammate.app.domain.repository.AuthRepository
import com.exammate.app.domain.repository.ExamRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class DaftarUjianUiState(
    val selectedTab: Int = 0,
    val ujianMendatang: List<Ujian> = emptyList(),
    val ujianBerlangsung: List<Ujian> = emptyList(),
    val ujianSelesai: List<Ujian> = emptyList(),
    val isLoading: Boolean = true
)

@HiltViewModel
class DaftarUjianViewModel @Inject constructor(
    private val examRepository: ExamRepository,
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(DaftarUjianUiState())
    val uiState: StateFlow<DaftarUjianUiState> = _uiState.asStateFlow()

    init {
        loadData()
    }

    private fun loadData() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            val result = examRepository.fetchAllExams()
            result.fold(
                onSuccess = { exams ->
                    val mendatang = exams.filter { it.status.name == "MENDATANG" && !authRepository.isExamCompleted(it.id) }
                    val berlangsung = exams.filter { it.status.name == "BERLANGSUNG" && !authRepository.isExamCompleted(it.id) }
                    val selesai = exams.filter { it.status.name == "SELESAI" } +
                        exams.filter { authRepository.isExamCompleted(it.id) && it.status.name != "SELESAI" }

                    _uiState.value = DaftarUjianUiState(
                        ujianMendatang = mendatang,
                        ujianBerlangsung = berlangsung,
                        ujianSelesai = selesai,
                        isLoading = false
                    )
                },
                onFailure = {
                    _uiState.value = _uiState.value.copy(isLoading = false)
                }
            )
        }
    }

    fun selectTab(index: Int) {
        _uiState.value = _uiState.value.copy(selectedTab = index)
    }
}
