package com.exammate.app.presentation.teacher

import androidx.lifecycle.ViewModel
import com.exammate.app.data.model.TeacherQuestionCategory
import com.exammate.app.data.repository.DummyDataRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

data class TeacherAddQuestionUiState(
    val category: TeacherQuestionCategory? = null,
    val pertanyaan: String = "",
    val opsi: List<String> = listOf("", "", "", "", ""),
    val jawabanBenar: Int = 0,
    val isSuccess: Boolean = false,
    val errorMessage: String? = null
)

@HiltViewModel
class TeacherAddQuestionViewModel @Inject constructor(
    private val dummyRepo: DummyDataRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(TeacherAddQuestionUiState())
    val uiState: StateFlow<TeacherAddQuestionUiState> = _uiState.asStateFlow()

    fun loadCategory(categoryId: Int) {
        val cat = dummyRepo.getCategoryById(categoryId)
        _uiState.value = _uiState.value.copy(category = cat)
    }

    fun onPertanyaanChange(value: String) {
        _uiState.value = _uiState.value.copy(pertanyaan = value, errorMessage = null)
    }

    fun onOpsiChange(index: Int, value: String) {
        val newOpsi = _uiState.value.opsi.toMutableList()
        if (index in newOpsi.indices) {
            newOpsi[index] = value
        }
        _uiState.value = _uiState.value.copy(opsi = newOpsi, errorMessage = null)
    }

    fun onJawabanBenarChange(index: Int) {
        _uiState.value = _uiState.value.copy(jawabanBenar = index)
    }

    fun saveQuestion() {
        val state = _uiState.value
        if (state.pertanyaan.isBlank()) {
            _uiState.value = state.copy(errorMessage = "Pertanyaan harus diisi")
            return
        }
        if (state.opsi.any { it.isBlank() }) {
            _uiState.value = state.copy(errorMessage = "Semua opsi harus diisi")
            return
        }
        _uiState.value = _uiState.value.copy(isSuccess = true)
    }

    fun reset() {
        _uiState.value = TeacherAddQuestionUiState(category = _uiState.value.category)
    }
}
