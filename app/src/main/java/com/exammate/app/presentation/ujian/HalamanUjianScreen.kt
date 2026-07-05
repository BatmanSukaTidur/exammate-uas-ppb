package com.exammate.app.presentation.ujian

import android.app.Activity
import android.content.Context
import android.view.ViewTreeObserver
import android.os.Build
import android.view.WindowManager
import androidx.activity.compose.BackHandler
import androidx.camera.core.CameraSelector
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.FlagCircle
import androidx.compose.material.icons.filled.HourglassEmpty
import androidx.compose.material.icons.filled.NavigateBefore
import androidx.compose.material.icons.filled.NavigateNext
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.core.view.WindowInsetsCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.lifecycleScope
import com.exammate.app.core.permission.CameraMicPermissionScreen
import com.exammate.app.core.permission.CameraPreviewView
import com.exammate.app.core.permission.MicLevelIndicator
import com.exammate.app.data.model.Soal
import com.exammate.app.presentation.theme.Error
import com.exammate.app.presentation.theme.Primary
import com.exammate.app.presentation.theme.PrimaryLight
import com.exammate.app.presentation.theme.Success
import com.exammate.app.presentation.theme.TextSecondary
import com.exammate.app.presentation.theme.Warning
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

class WindowFocusObserver(
    private val activity: Activity,
    private val viewModel: HalamanUjianViewModel
) {
    private var viewFocusListener: ViewTreeObserver.OnWindowFocusChangeListener? = null
    private val focusChangeListener: (Boolean) -> Unit = { hasFocus ->
        if (!hasFocus) viewModel.onAppSwitchDetected()
        else viewModel.onAppResumed()
    }

    @Suppress("DEPRECATION")
    fun start() {
        val decorView = activity.window.decorView

        viewFocusListener = ViewTreeObserver.OnWindowFocusChangeListener { hasFocus ->
            focusChangeListener(hasFocus)
        }
        decorView.viewTreeObserver.addOnWindowFocusChangeListener(viewFocusListener!!)
    }

    @Suppress("DEPRECATION")
    fun stop() {
        val decorView = activity.window.decorView
        viewFocusListener?.let { decorView.viewTreeObserver.removeOnWindowFocusChangeListener(it) }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HalamanUjianScreen(
    examId: Int,
    onFinish: (ScoreResult) -> Unit,
    onBack: () -> Unit,
    viewModel: HalamanUjianViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    val activity = context as? Activity
    var showExitDialog by remember { mutableStateOf(false) }
    var showSummary by remember { mutableStateOf(false) }
    var summaryResult by remember { mutableStateOf<ScoreResult?>(null) }

    LaunchedEffect(Unit) {
        viewModel.startExam(examId)
    }

    LaunchedEffect(uiState.isFinished) {
        if (uiState.isFinished && !uiState.showCameraCheck) {
            if (uiState.isAutoSubmitted) {
                viewModel.submitResult()
            }
            val result = viewModel.getScoreResult()
            summaryResult = result
            showSummary = true
        }
    }

    // Exit confirmation dialog
    if (showExitDialog) {
        AlertDialog(
            onDismissRequest = { showExitDialog = false },
            title = {
                Text("Keluar Ujian?", fontWeight = FontWeight.Bold)
            },
            text = {
                Text("Jika kamu keluar, ujian akan di-submit otomatis dan tidak bisa dilanjutkan kembali.")
            },
            confirmButton = {
        Button(
            onClick = {
                showExitDialog = false
                viewModel.finishExam(true)
            },
            colors = ButtonDefaults.buttonColors(containerColor = Error)
        ) {
            Text("Ya, Keluar")
        }
            },
            dismissButton = {
                TextButton(onClick = { showExitDialog = false }) {
                    Text("Batal")
                }
            }
        )
    }

    // Camera check screen
    if (uiState.showCameraCheck) {
        CameraMicPermissionScreen(
            onPermissionGranted = { viewModel.onCameraCheckPassed() },
            onBack = onBack
        )
        return
    }

    if (uiState.isLoading) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                CircularProgressIndicator(color = Primary)
                Spacer(modifier = Modifier.height(16.dp))
                Text("Memuat soal...", color = TextSecondary)
            }
        }
        return
    }

    if (uiState.errorMessage != null) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(
                    imageVector = Icons.Default.Warning,
                    contentDescription = null,
                    modifier = Modifier.size(64.dp),
                    tint = Error
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = uiState.errorMessage ?: "",
                    style = MaterialTheme.typography.titleMedium
                )
                Spacer(modifier = Modifier.height(16.dp))
                Button(onClick = onBack) {
                    Text("Kembali")
                }
            }
        }
        return
    }

    val ujian = uiState.ujian ?: return
    val question = ujian.soal.getOrNull(uiState.currentIndex) ?: return
    val currentAnswer = uiState.selectedAnswers[question.id]
    val isFlagged = uiState.flaggedQuestions.contains(question.id)

    val lifecycleOwner = LocalLifecycleOwner.current
    val windowFocusObserver = remember {
        activity?.let { WindowFocusObserver(it, viewModel) }
    }

    // Deteksi lifecycle + window focus (tab switching, home, recent apps)
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_PAUSE -> viewModel.onAppSwitchDetected()
                Lifecycle.Event.ON_STOP -> viewModel.onAppSwitchDetected()
                Lifecycle.Event.ON_RESUME -> viewModel.onAppResumed()
                else -> {}
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        windowFocusObserver?.start()
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
            windowFocusObserver?.stop()
        }
    }

    // Handle sistem back button
    BackHandler(enabled = (uiState.isExamReady && !uiState.isFinished) || showSummary || uiState.isReviewMode) {
        if (showSummary && !uiState.isAutoSubmitted) {
            viewModel.resetExam()
            showSummary = false
            summaryResult = null
        } else if (uiState.isReviewMode) {
            viewModel.backToSummary()
            showSummary = true
            summaryResult = viewModel.getScoreResult()
        } else if (uiState.isExamReady && !uiState.isFinished) {
            showExitDialog = true
        }
    }

    // Disable multi-window / split screen saat ujian aktif
    DisposableEffect(uiState.isExamReady, uiState.isFinished) {
        if (uiState.isExamReady && !uiState.isFinished) {
            activity?.window?.setFlags(
                android.view.WindowManager.LayoutParams.FLAG_SECURE,
                android.view.WindowManager.LayoutParams.FLAG_SECURE
            )
        }
        onDispose {
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = ujian.mapel,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        TimerDisplay(
                            remainingSeconds = uiState.remainingTimeSeconds
                        )
                    }
                },
                navigationIcon = {
                    IconButton(
                        onClick = {
                            if (showSummary) {
                                // must use overlay buttons
                            } else if (uiState.isReviewMode) {
                                viewModel.backToSummary()
                                showSummary = true
                                summaryResult = viewModel.getScoreResult()
                            } else if (uiState.isExamReady && !uiState.isFinished) {
                                showExitDialog = true
                            } else {
                                onBack()
                            }
                        }
                    ) {
                        Icon(
                            Icons.Default.Close,
                            contentDescription = "Tutup",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Primary
                )
            )
        },
        bottomBar = {
            ExamBottomBar(
                currentIndex = uiState.currentIndex,
                totalQuestions = ujian.soal.size,
                onPrevious = { viewModel.previousQuestion() },
                onNext = { viewModel.nextQuestion() },
                onFinish = { viewModel.finishExam() }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Question Number Navigator
            QuestionNumberNavigator(
                totalQuestions = ujian.soal.size,
                currentIndex = uiState.currentIndex,
                selectedAnswers = uiState.selectedAnswers,
                flaggedQuestions = uiState.flaggedQuestions,
                questions = ujian.soal,
                onSelect = { viewModel.navigateTo(it) }
            )

            // Camera PIP preview + indicator
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp),
                shape = RoundedCornerShape(8.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Success.copy(alpha = 0.1f)
                )
            ) {
                Row(
                    modifier = Modifier.padding(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Camera PIP preview
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color.Black)
                    ) {
                        CameraPreviewView(
                            lifecycleOwner = lifecycleOwner
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Videocam,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp),
                                tint = Success
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Kamera & Mikrofon aktif",
                                style = MaterialTheme.typography.labelSmall,
                                color = Success,
                                fontWeight = FontWeight.Medium
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            MicLevelIndicator()
                        }
                        Text(
                            text = "Pengawasan berjalan",
                            style = MaterialTheme.typography.labelSmall,
                            color = TextSecondary
                        )
                    }
                }
            }

            // Progress
            Text(
                text = "Soal ${uiState.currentIndex + 1} dari ${ujian.soal.size}",
                style = MaterialTheme.typography.labelLarge,
                color = TextSecondary,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            )

            // Question Content
            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp)
            ) {
                QuestionCard(
                    question = question,
                    questionNumber = uiState.currentIndex + 1,
                    selectedAnswer = currentAnswer,
                    onAnswerSelected = { index ->
                        if (!uiState.isReviewMode) viewModel.selectAnswer(question.id, index)
                    },
                    isReviewMode = uiState.isReviewMode
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Flag button
                FilledTonalButton(
                    onClick = { viewModel.toggleFlag(question.id) },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !uiState.isReviewMode
                ) {
                    Icon(
                        imageVector = if (isFlagged) Icons.Default.FlagCircle else Icons.Default.Flag,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(if (isFlagged) "Ragu-ragu" else "Tandai Ragu-ragu")
                }

                if (uiState.isReviewMode) {
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        onClick = {
                            viewModel.backToSummary()
                            showSummary = true
                            summaryResult = viewModel.getScoreResult()
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Kembali ke Ringkasan")
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))
            }
        }

        if (uiState.showGraceOverlay) {
            GraceOverlay(
                countdownSeconds = uiState.graceCountdownSeconds
            )
        }

        if (showSummary && summaryResult != null) {
            SummaryOverlay(
                result = summaryResult!!,
                isAutoSubmit = uiState.isAutoSubmitted,
                onSubmit = {
                    viewModel.submitResult()
                    showSummary = false
                    onFinish(summaryResult!!)
                },
                onBackToExam = {
                    viewModel.resetExam()
                    showSummary = false
                    summaryResult = null
                }
            )
        }
    }
    }
}

@Composable
private fun GraceOverlay(countdownSeconds: Int) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.6f))
            .clickable(enabled = false) { },
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(32.dp),
            shape = RoundedCornerShape(16.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    imageVector = Icons.Default.Warning,
                    contentDescription = null,
                    modifier = Modifier.size(48.dp),
                    tint = Warning
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Aplikasi terdeteksi meninggalkan ujian!",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "Submit otomatis dalam $countdownSeconds detik...",
                    style = MaterialTheme.typography.bodyLarge,
                    color = Error,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Kembali ke aplikasi untuk membatalkan",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Composable
private fun SummaryOverlay(
    result: ScoreResult,
    isAutoSubmit: Boolean,
    onSubmit: () -> Unit,
    onBackToExam: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.5f))
            .clickable(enabled = false) { },
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            shape = RoundedCornerShape(16.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                if (isAutoSubmit) {
                    Icon(
                        imageVector = Icons.Default.Warning,
                        contentDescription = null,
                        modifier = Modifier.size(48.dp),
                        tint = Warning
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "Ujian Diakhiri",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Ujian berakhir karena terdeteksi meninggalkan aplikasi atau waktu habis. Hubungi pengawas ujian jika ada kendala.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextSecondary,
                        textAlign = TextAlign.Center
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = null,
                        modifier = Modifier.size(48.dp),
                        tint = Success
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "Ujian Selesai",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        SummaryStat("Total Soal", "${result.totalQuestions}", Primary)
                        Spacer(modifier = Modifier.height(12.dp))
                        SummaryStat("Dijawab", "${result.selectedAnswers.size}", Success)
                        Spacer(modifier = Modifier.height(12.dp))
                        SummaryStat("Belum Dijawab", "${result.totalQuestions - result.selectedAnswers.size}", Error)
                        Spacer(modifier = Modifier.height(12.dp))
                        SummaryStat("Ragu-ragu", "${result.flaggedQuestions.size}", Warning)
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = onSubmit,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(if (isAutoSubmit) "Lihat Hasil Ujian" else "Kirim Jawaban")
                }

                if (!isAutoSubmit) {
                    Spacer(modifier = Modifier.height(12.dp))
                    TextButton(onClick = onBackToExam) {
                        Text("Kembali ke Soal")
                    }
                }
            }
        }
    }
}

@Composable
private fun SummaryStat(label: String, value: String, color: Color) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = TextSecondary
        )
        Text(
            text = value,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = color
        )
    }
}

@Composable
private fun TimerDisplay(remainingSeconds: Int) {
    val minutes = remainingSeconds / 60
    val seconds = remainingSeconds % 60
    val timeText = String.format("%02d:%02d", minutes, seconds)
    val isCritical = remainingSeconds < 300

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(top = 2.dp)
    ) {
        Icon(
            imageVector = Icons.Default.HourglassEmpty,
            contentDescription = null,
            modifier = Modifier.size(14.dp),
            tint = if (isCritical) Error else Color.White.copy(alpha = 0.8f)
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = timeText,
            style = MaterialTheme.typography.bodySmall,
            fontWeight = FontWeight.Bold,
            color = if (isCritical) Error else Color.White.copy(alpha = 0.8f)
        )
    }

    if (isCritical) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(top = 2.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Warning,
                contentDescription = null,
                modifier = Modifier.size(12.dp),
                tint = Error
            )
            Spacer(modifier = Modifier.width(2.dp))
            Text(
                text = "Waktu hampir habis!",
                style = MaterialTheme.typography.labelSmall,
                color = Error,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
private fun QuestionNumberNavigator(
    totalQuestions: Int,
    currentIndex: Int,
    selectedAnswers: Map<Int, Int>,
    flaggedQuestions: Set<Int>,
    questions: List<Soal>,
    onSelect: (Int) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState())
            .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        questions.forEachIndexed { index, question ->
            val isAnswered = selectedAnswers.containsKey(question.id)
            val isFlagged = flaggedQuestions.contains(question.id)
            val isCurrent = index == currentIndex

            val bgColor = when {
                isCurrent -> Primary
                isFlagged -> Warning
                isAnswered -> Success
                else -> MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
            }
            val textColor = when {
                isCurrent -> Color.White
                isFlagged -> Color.White
                isAnswered -> Color.White
                else -> TextSecondary
            }

            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(bgColor)
                    .then(
                        if (isCurrent) Modifier.border(2.dp, Primary, CircleShape)
                        else Modifier
                    )
                    .clickable { onSelect(index) },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "${index + 1}",
                    style = MaterialTheme.typography.labelMedium,
                    color = textColor,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
private fun QuestionCard(
    question: Soal,
    questionNumber: Int,
    selectedAnswer: Int?,
    onAnswerSelected: (Int) -> Unit,
    isReviewMode: Boolean = false
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text(
                text = question.pertanyaan,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )

            Spacer(modifier = Modifier.height(20.dp))

            if (isReviewMode) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Mode Review - Jawaban tidak bisa diubah",
                    style = MaterialTheme.typography.labelSmall,
                    color = Warning,
                    fontWeight = FontWeight.Medium
                )
                Spacer(modifier = Modifier.height(8.dp))
            }

            val optionLabels = listOf("A", "B", "C", "D", "E")
            question.opsi.forEachIndexed { index, optionText ->
                val isSelected = selectedAnswer == index
                OptionItem(
                    label = optionLabels.getOrElse(index) { "${index + 1}" },
                    text = optionText,
                    isSelected = isSelected,
                    onClick = { onAnswerSelected(index) },
                    isReviewMode = isReviewMode
                )
                if (index < question.opsi.size - 1) {
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }
    }
}

@Composable
private fun OptionItem(
    label: String,
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    isReviewMode: Boolean = false
) {
    val bgColor = if (isSelected) Primary.copy(alpha = 0.1f) else MaterialTheme.colorScheme.surface
    val borderColor = if (isSelected) Primary else MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
    val labelBg = if (isSelected) Primary else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
    val labelTextColor = if (isSelected) Color.White else TextSecondary

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(bgColor)
            .border(1.dp, borderColor, RoundedCornerShape(12.dp))
            .then(if (!isReviewMode) Modifier.clickable { onClick() } else Modifier)
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(32.dp)
                .clip(CircleShape)
                .background(labelBg),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelLarge,
                color = labelTextColor,
                fontWeight = FontWeight.Bold
            )
        }
        Spacer(modifier = Modifier.width(12.dp))
        Text(
            text = text,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun ExamBottomBar(
    currentIndex: Int,
    totalQuestions: Int,
    onPrevious: () -> Unit,
    onNext: () -> Unit,
    onFinish: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface)
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        OutlinedButton(
            onClick = onPrevious,
            enabled = currentIndex > 0,
            shape = RoundedCornerShape(12.dp)
        ) {
            Icon(Icons.Default.NavigateBefore, contentDescription = null)
            Spacer(modifier = Modifier.width(4.dp))
            Text("Sebelumnya")
        }

        if (currentIndex == totalQuestions - 1) {
            Button(
                onClick = onFinish,
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Primary)
            ) {
                Text("Selesai")
            }
        } else {
            Button(
                onClick = onNext,
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Primary)
            ) {
                Text("Selanjutnya")
                Spacer(modifier = Modifier.width(4.dp))
                Icon(Icons.Default.NavigateNext, contentDescription = null)
            }
        }
    }
}
