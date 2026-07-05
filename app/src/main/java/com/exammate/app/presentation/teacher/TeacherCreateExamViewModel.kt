package com.exammate.app.presentation.teacher

import androidx.lifecycle.ViewModel
import com.exammate.app.data.model.TeacherQuestionCategory
import com.exammate.app.data.repository.DummyDataRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

data class TeacherCreateExamUiState(
    val namaUjian: String = "",
    val mapel: String = "",
    val token: String = "",
    val durasiMenit: String = "90",
    val categories: List<TeacherQuestionCategory> = emptyList(),
    val selectedCategoryId: Int? = null,
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val errorMessage: String? = null
)

@HiltViewModel
class TeacherCreateExamViewModel @Inject constructor(
    private val dummyRepo: DummyDataRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(TeacherCreateExamUiState())
    val uiState: StateFlow<TeacherCreateExamUiState> = _uiState.asStateFlow()

    init {
        _uiState.value = _uiState.value.copy(categories = dummyRepo.questionBank)
    }

    fun onNamaUjianChange(value: String) {
        _uiState.value = _uiState.value.copy(namaUjian = value, errorMessage = null)
    }

    fun onMapelChange(value: String) {
        _uiState.value = _uiState.value.copy(mapel = value, errorMessage = null)
    }

    fun onTokenChange(value: String) {
        _uiState.value = _uiState.value.copy(token = value, errorMessage = null)
    }

    fun onDurasiChange(value: String) {
        _uiState.value = _uiState.value.copy(durasiMenit = value, errorMessage = null)
    }

    fun onCategorySelected(categoryId: Int) {
        _uiState.value = _uiState.value.copy(selectedCategoryId = categoryId)
    }

    fun createExam() {
        val state = _uiState.value
        if (state.namaUjian.isBlank()) {
            _uiState.value = state.copy(errorMessage = "Nama ujian harus diisi")
            return
        }
        if (state.mapel.isBlank()) {
            _uiState.value = state.copy(errorMessage = "Mata pelajaran harus diisi")
            return
        }
        if (state.token.isBlank()) {
            _uiState.value = state.copy(errorMessage = "Token harus diisi")
            return
        }
        _uiState.value = _uiState.value.copy(isLoading = true)
        _uiState.value = _uiState.value.copy(isLoading = false, isSuccess = true)
    }

    fun reset() {
        _uiState.value = TeacherCreateExamUiState(categories = dummyRepo.questionBank)
    }
}
