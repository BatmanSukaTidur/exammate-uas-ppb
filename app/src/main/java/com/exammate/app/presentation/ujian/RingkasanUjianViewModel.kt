package com.exammate.app.presentation.ujian

import androidx.lifecycle.ViewModel
import com.exammate.app.data.model.Ujian
import com.exammate.app.data.repository.DummyDataRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

data class RingkasanUjianUiState(
    val ujian: Ujian? = null,
    val selectedAnswers: Map<Int, Int> = emptyMap(),
    val flaggedQuestions: Set<Int> = emptySet(),
    val totalSoal: Int = 0,
    val dijawab: Int = 0,
    val belumDijawab: Int = 0,
    val raguRagu: Int = 0
)

@HiltViewModel
class RingkasanUjianViewModel @Inject constructor(
    private val dummyRepo: DummyDataRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(RingkasanUjianUiState())
    val uiState: StateFlow<RingkasanUjianUiState> = _uiState.asStateFlow()

    fun loadData(
        examId: Int,
        selectedAnswers: Map<Int, Int>,
        flaggedQuestions: Set<Int>
    ) {
        val ujian = dummyRepo.getUjianById(examId)
        if (ujian != null) {
            val total = ujian.soal.size
            val answered = selectedAnswers.size
            _uiState.value = RingkasanUjianUiState(
                ujian = ujian,
                selectedAnswers = selectedAnswers,
                flaggedQuestions = flaggedQuestions,
                totalSoal = total,
                dijawab = answered,
                belumDijawab = total - answered,
                raguRagu = flaggedQuestions.size
            )
        }
    }
}
