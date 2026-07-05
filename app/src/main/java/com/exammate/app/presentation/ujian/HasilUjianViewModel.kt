package com.exammate.app.presentation.ujian

import androidx.lifecycle.ViewModel
import com.exammate.app.data.model.Ujian
import com.exammate.app.data.repository.DummyDataRepository
import com.exammate.app.domain.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

data class HasilUjianUiState(
    val ujian: Ujian? = null,
    val score: Int = 0,
    val total: Int = 0,
    val correct: Int = 0,
    val wrong: Int = 0,
    val empty: Int = 0,
    val selectedAnswers: Map<Int, Int> = emptyMap()
)

@HiltViewModel
class HasilUjianViewModel @Inject constructor(
    private val dummyRepo: DummyDataRepository,
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(HasilUjianUiState())
    val uiState: StateFlow<HasilUjianUiState> = _uiState.asStateFlow()

    fun loadData(
        examId: Int,
        score: Int,
        total: Int,
        correct: Int,
        selectedAnswers: Map<Int, Int>
    ) {
        val ujian = dummyRepo.getUjianById(examId)

        val answeredCount = if (selectedAnswers.isNotEmpty()) selectedAnswers.size
            else authRepository.getExamAnsweredCount(examId)

        val actualTotal = if (total > 0) total else authRepository.getExamTotal(examId)
        val actualScore = if (score > 0 || selectedAnswers.isNotEmpty()) score else authRepository.getExamScore(examId)
        val actualCorrect = if (correct > 0 || selectedAnswers.isNotEmpty()) correct else authRepository.getExamCorrect(examId)

        val wrong = answeredCount - actualCorrect
        val empty = actualTotal - answeredCount

        _uiState.value = HasilUjianUiState(
            ujian = ujian,
            score = actualScore,
            total = actualTotal,
            correct = actualCorrect,
            wrong = wrong.coerceAtLeast(0),
            empty = empty.coerceAtLeast(0),
            selectedAnswers = selectedAnswers
        )
    }
}
