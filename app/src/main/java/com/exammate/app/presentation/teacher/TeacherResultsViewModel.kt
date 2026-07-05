package com.exammate.app.presentation.teacher

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.exammate.app.data.model.Ujian
import com.exammate.app.data.repository.DummyDataRepository
import com.exammate.app.data.repository.StudentExamResult
import com.exammate.app.domain.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ExamWithStudents(
    val ujian: Ujian,
    val students: List<StudentExamResult>
)

data class TeacherResultsUiState(
    val examsWithStudents: List<ExamWithStudents> = emptyList(),
    val mapel: String = "",
    val isLoading: Boolean = true
)

@HiltViewModel
class TeacherResultsViewModel @Inject constructor(
    private val dummyRepo: DummyDataRepository,
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(TeacherResultsUiState())
    val uiState: StateFlow<TeacherResultsUiState> = _uiState.asStateFlow()

    init {
        loadResults()
    }

    private fun loadResults() {
        viewModelScope.launch {
            val user = authRepository.getSavedUser()
            val mapel = user?.mapel ?: ""

            val allExams = dummyRepo.daftarUjian
            val filteredExams = if (mapel.isNotBlank()) {
                allExams.filter { it.mapel == mapel }
            } else {
                allExams
            }

            val allResults = dummyRepo.getAllStudentResults()
            val examsWithStudents = filteredExams.map { exam ->
                val students = allResults.filter { it.examId == exam.id }
                ExamWithStudents(exam, students)
            }

            _uiState.value = TeacherResultsUiState(
                examsWithStudents = examsWithStudents,
                mapel = mapel,
                isLoading = false
            )
        }
    }
}
