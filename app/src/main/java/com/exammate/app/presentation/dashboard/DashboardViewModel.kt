package com.exammate.app.presentation.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.exammate.app.data.model.DashboardStats
import com.exammate.app.data.model.Ujian
import com.exammate.app.data.model.User
import com.exammate.app.domain.repository.AuthRepository
import com.exammate.app.domain.repository.ExamRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class DashboardUiState(
    val user: User? = null,
    val stats: DashboardStats = DashboardStats(0, 0, 0.0),
    val ujianTerdekat: Ujian? = null,
    val upcomingExams: List<Ujian> = emptyList(),
    val isLoading: Boolean = true
)

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val examRepository: ExamRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(DashboardUiState())
    val uiState: StateFlow<DashboardUiState> = _uiState.asStateFlow()

    init {
        loadData()
    }

    private fun loadData() {
        viewModelScope.launch {
            val user = authRepository.getSavedUser()

            val result = examRepository.fetchAllExams()
            result.fold(
                onSuccess = { exams ->
                    val selesai = exams.filter { it.status.name == "SELESAI" }
                    val belumDikerjakan = exams.filter {
                        !authRepository.isExamCompleted(it.id) &&
                        it.status.name != "SELESAI"
                    }
                    val berlangsung = belumDikerjakan.filter { it.status.name == "BERLANGSUNG" }
                    val mendatang = belumDikerjakan.filter { it.status.name == "MENDATANG" }
                    val rata = authRepository.getAverageScore()

                    _uiState.value = DashboardUiState(
                        user = user,
                        stats = DashboardStats(
                            totalUjian = exams.size,
                            ujianSelesai = selesai.size,
                            nilaiRataRata = rata
                        ),
                        ujianTerdekat = (berlangsung + mendatang).firstOrNull(),
                        upcomingExams = berlangsung + mendatang,
                        isLoading = false
                    )
                },
                onFailure = {
                    _uiState.value = DashboardUiState(
                        user = user,
                        isLoading = false
                    )
                }
            )
        }
    }
}
