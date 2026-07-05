package com.exammate.app.presentation.teacher

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.exammate.app.data.model.TeacherQuestionCategory
import com.exammate.app.data.repository.DummyDataRepository
import com.exammate.app.domain.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class TeacherQuestionBankUiState(
    val categories: List<TeacherQuestionCategory> = emptyList(),
    val teacherMapel: String = ""
)

@HiltViewModel
class TeacherQuestionBankViewModel @Inject constructor(
    private val dummyRepo: DummyDataRepository,
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(TeacherQuestionBankUiState())
    val uiState: StateFlow<TeacherQuestionBankUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            val user = authRepository.getSavedUser()
            val mapel = user?.mapel ?: ""
            val filtered = if (mapel.isNotBlank()) {
                dummyRepo.questionBank.filter { it.nama == mapel }
            } else {
                dummyRepo.questionBank
            }
            _uiState.value = TeacherQuestionBankUiState(categories = filtered, teacherMapel = mapel)
        }
    }
}
