package com.exammate.app.presentation.ujian

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.exammate.app.data.model.Soal
import com.exammate.app.data.model.Ujian
import com.exammate.app.data.repository.DummyDataRepository
import com.exammate.app.data.repository.StudentExamResult
import com.exammate.app.domain.repository.AuthRepository
import com.exammate.app.domain.repository.ExamRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import java.time.LocalDate
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class HalamanUjianUiState(
    val ujian: Ujian? = null,
    val currentIndex: Int = 0,
    val selectedAnswers: Map<Int, Int> = emptyMap(),
    val flaggedQuestions: Set<Int> = emptySet(),
    val remainingTimeSeconds: Int = 0,
    val isLoading: Boolean = true,
    val isExamReady: Boolean = false,
    val isFinished: Boolean = false,
    val showCameraCheck: Boolean = false,
    val errorMessage: String? = null,
    val isAppInBackground: Boolean = false,
    val showGraceOverlay: Boolean = false,
    val graceCountdownSeconds: Int = 0,
    val isSubmitting: Boolean = false,
    val isAutoSubmitted: Boolean = false,
    val isReviewMode: Boolean = false
)

@HiltViewModel
class HalamanUjianViewModel @Inject constructor(
    private val examRepository: ExamRepository,
    private val authRepository: AuthRepository,
    private val dummyRepo: DummyDataRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(HalamanUjianUiState())
    val uiState: StateFlow<HalamanUjianUiState> = _uiState.asStateFlow()

    private var timerJob: Job? = null
    private var graceCountdownJob: Job? = null

    fun onAppSwitchDetected() {
        if (_uiState.value.isFinished || !_uiState.value.isExamReady) return
        if (_uiState.value.isAppInBackground) return

        _uiState.update {
            it.copy(
                isAppInBackground = true,
                showGraceOverlay = true,
                graceCountdownSeconds = 5
            )
        }
        startGraceCountdown()
    }

    fun onAppResumed() {
        if (!_uiState.value.isAppInBackground) return
        if (_uiState.value.isFinished) return

        graceCountdownJob?.cancel()
        _uiState.update {
            it.copy(
                isAppInBackground = false,
                showGraceOverlay = false,
                graceCountdownSeconds = 0
            )
        }
    }

    private fun startGraceCountdown() {
        graceCountdownJob?.cancel()
        graceCountdownJob = viewModelScope.launch {
            while (_uiState.value.graceCountdownSeconds > 0) {
                delay(1000L)
                _uiState.update {
                    it.copy(graceCountdownSeconds = it.graceCountdownSeconds - 1)
                }
            }
            if (!_uiState.value.isFinished) {
                finishExam(true)
            }
        }
    }

    fun startExam(examId: Int) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)

            val nis = authRepository.getSavedUser()?.nis
            if (nis != null && authRepository.isExamCompleted(examId)) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = "Ujian sudah pernah dikerjakan"
                )
                return@launch
            }

            val result = examRepository.fetchExamById(examId)
            result.fold(
                onSuccess = { ujian ->
                    if (ujian.status.name != "BERLANGSUNG") {
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            errorMessage = "Ujian ${if (ujian.status.name == "MENDATANG") "belum dimulai" else "sudah selesai"}"
                        )
                        return@launch
                    }
                    _uiState.value = _uiState.value.copy(
                        ujian = ujian,
                        remainingTimeSeconds = ujian.durasiMenit * 60,
                        isLoading = false,
                        showCameraCheck = true
                    )
                },
                onFailure = { e ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = e.message ?: "Gagal memuat ujian"
                    )
                }
            )
        }
    }

    fun onCameraCheckPassed() {
        _uiState.value = _uiState.value.copy(showCameraCheck = false, isExamReady = true)
        startTimer()
    }

    fun onExamFinished() {
        if (_uiState.value.isSubmitting) return
        _uiState.update { it.copy(isSubmitting = true) }
    }

    fun onMultiWindowModeChanged(isInMultiWindow: Boolean) {
        if (isInMultiWindow && _uiState.value.isExamReady && !_uiState.value.isFinished) {
            onAppSwitchDetected()
        }
    }

    private fun startTimer() {
        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            while (_uiState.value.remainingTimeSeconds > 0 && !_uiState.value.isFinished) {
                delay(1000L)
                _uiState.update { it.copy(remainingTimeSeconds = it.remainingTimeSeconds - 1) }
            }
            if (_uiState.value.remainingTimeSeconds <= 0 && !_uiState.value.isFinished) {
                finishExam(true)
            }
        }
    }

    val currentQuestion: Soal?
        get() = _uiState.value.ujian?.soal?.getOrNull(_uiState.value.currentIndex)

    fun selectAnswer(questionId: Int, answerIndex: Int) {
        if (_uiState.value.isReviewMode || _uiState.value.isFinished) return
        _uiState.update {
            it.copy(selectedAnswers = it.selectedAnswers + (questionId to answerIndex))
        }
    }

    fun toggleFlag(questionId: Int) {
        _uiState.update {
            val updated = it.flaggedQuestions.toMutableSet()
            if (updated.contains(questionId)) updated.remove(questionId) else updated.add(questionId)
            it.copy(flaggedQuestions = updated)
        }
    }

    fun navigateTo(index: Int) {
        val ujian = _uiState.value.ujian ?: return
        val clamped = index.coerceIn(0, ujian.soal.size - 1)
        _uiState.update { it.copy(currentIndex = clamped) }
    }

    fun nextQuestion() {
        val ujian = _uiState.value.ujian ?: return
        val next = _uiState.value.currentIndex + 1
        if (next < ujian.soal.size) navigateTo(next)
    }

    fun previousQuestion() {
        val prev = _uiState.value.currentIndex - 1
        if (prev >= 0) navigateTo(prev)
    }

    fun finishExam(isAutoSubmit: Boolean = false) {
        if (_uiState.value.isFinished) return
        timerJob?.cancel()
        graceCountdownJob?.cancel()
        _uiState.update {
            it.copy(
                isFinished = true,
                isAutoSubmitted = isAutoSubmit,
                isAppInBackground = false,
                showGraceOverlay = false,
                graceCountdownSeconds = 0
            )
        }
    }

    fun resetExam() {
        _uiState.update {
            it.copy(
                isFinished = false,
                isAutoSubmitted = false,
                isSubmitting = false,
                isReviewMode = true,
                currentIndex = 0
            )
        }
    }

    fun backToSummary() {
        _uiState.update {
            it.copy(isReviewMode = false, isFinished = true)
        }
    }

    fun getScoreResult(): ScoreResult {
        val state = _uiState.value
        val ujian = state.ujian ?: return ScoreResult(0, 0, 0, "", emptyMap(), emptySet())

        var correct = 0
        state.selectedAnswers.forEach { (questionId, answerIndex) ->
            val soal = ujian.soal.find { it.id == questionId }
            if (soal != null && soal.jawabanBenar == answerIndex) correct++
        }
        val total = ujian.soal.size
        val score = if (total > 0) (correct * 100 / total) else 0
        return ScoreResult(total, correct, score, ujian.mapel, state.selectedAnswers, state.flaggedQuestions, state.isAutoSubmitted)
    }

    fun submitResult() {
        if (_uiState.value.isSubmitting) return
        _uiState.update { it.copy(isSubmitting = true) }

        val state = _uiState.value
        val ujian = state.ujian ?: return

        val total = ujian.soal.size
        var correct = 0
        state.selectedAnswers.forEach { (questionId, answerIndex) ->
            val soal = ujian.soal.find { it.id == questionId }
            if (soal != null && soal.jawabanBenar == answerIndex) correct++
        }
        val score = if (total > 0) (correct * 100 / total) else 0
        authRepository.markExamCompleted(ujian.id)
        authRepository.saveExamResult(ujian.id, score, total, correct, state.selectedAnswers.size)

        val user = authRepository.getSavedUser()
        val salah = (state.selectedAnswers.size - correct).coerceAtLeast(0)
        val kosong = (total - state.selectedAnswers.size).coerceAtLeast(0)
        val studentResult = StudentExamResult(
            studentNis = user?.nis ?: "unknown",
            studentName = user?.nama ?: "Siswa",
            examId = ujian.id,
            mapel = ujian.mapel,
            score = score,
            total = total,
            correct = correct,
            wrong = salah,
            unanswered = kosong,
            submittedAt = LocalDate.now().toString()
        )
        dummyRepo.saveStudentResult(studentResult)

        viewModelScope.launch {
            val salah = state.selectedAnswers.size - correct
            val kosong = total - state.selectedAnswers.size

            val uid = user?.nis ?: "unknown"
            val answerResult = examRepository.submitAnswers(
                examId = ujian.id,
                studentUid = uid,
                jawaban = state.selectedAnswers,
                flagged = state.flaggedQuestions,
                waktuPengerjaan = (ujian.durasiMenit * 60) - state.remainingTimeSeconds
            )
            if (answerResult.isFailure) {
                android.util.Log.e("HalamanUjian", "Gagal submit jawaban", answerResult.exceptionOrNull())
            }
            val saveResult = examRepository.saveResult(
                examId = ujian.id,
                studentUid = uid,
                score = score,
                totalSoal = total,
                benar = correct,
                salah = salah.coerceAtLeast(0),
                kosong = kosong.coerceAtLeast(0)
            )
            if (saveResult.isFailure) {
                android.util.Log.e("HalamanUjian", "Gagal simpan nilai", saveResult.exceptionOrNull())
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        timerJob?.cancel()
        graceCountdownJob?.cancel()
    }
}

data class ScoreResult(
    val totalQuestions: Int,
    val correctAnswers: Int,
    val score: Int,
    val examTitle: String,
    val selectedAnswers: Map<Int, Int>,
    val flaggedQuestions: Set<Int>,
    val isAutoSubmitted: Boolean = false
)
