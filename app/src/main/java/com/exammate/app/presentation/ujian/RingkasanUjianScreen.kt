package com.exammate.app.presentation.ujian

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.HourglassEmpty
import androidx.compose.material.icons.filled.QuestionMark
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.exammate.app.presentation.component.ExammateButton
import com.exammate.app.presentation.component.ExammateOutlinedButton
import com.exammate.app.presentation.theme.Error
import com.exammate.app.presentation.theme.Primary
import com.exammate.app.presentation.theme.Success
import com.exammate.app.presentation.theme.TextSecondary
import com.exammate.app.presentation.theme.Warning

@Composable
fun RingkasanUjianScreen(
    examId: Int,
    selectedAnswers: Map<Int, Int>,
    flaggedQuestions: Set<Int>,
    onSubmit: () -> Unit,
    onBack: () -> Unit,
    viewModel: RingkasanUjianViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.loadData(examId, selectedAnswers, flaggedQuestions)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .size(100.dp)
                .clip(CircleShape)
                .background(Success.copy(alpha = 0.1f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.CheckCircle,
                contentDescription = null,
                modifier = Modifier.size(56.dp),
                tint = Success
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Ujian Selesai",
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Bold
        )

        Text(
            text = uiState.ujian?.mapel ?: "",
            style = MaterialTheme.typography.titleMedium,
            color = TextSecondary
        )

        Spacer(modifier = Modifier.height(32.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                StatRingkasan(
                    icon = Icons.Default.QuestionMark,
                    iconBg = Primary.copy(alpha = 0.1f),
                    iconTint = Primary,
                    label = "Total Soal",
                    value = "${uiState.totalSoal}"
                )

                Spacer(modifier = Modifier.height(16.dp))
                androidx.compose.material3.HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
                Spacer(modifier = Modifier.height(16.dp))

                StatRingkasan(
                    icon = Icons.Default.CheckCircle,
                    iconBg = Success.copy(alpha = 0.1f),
                    iconTint = Success,
                    label = "Dijawab",
                    value = "${uiState.dijawab}"
                )

                Spacer(modifier = Modifier.height(16.dp))
                androidx.compose.material3.HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
                Spacer(modifier = Modifier.height(16.dp))

                StatRingkasan(
                    icon = Icons.Default.RadioButtonUnchecked,
                    iconBg = Error.copy(alpha = 0.1f),
                    iconTint = Error,
                    label = "Belum Dijawab",
                    value = "${uiState.belumDijawab}"
                )

                Spacer(modifier = Modifier.height(16.dp))
                androidx.compose.material3.HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
                Spacer(modifier = Modifier.height(16.dp))

                StatRingkasan(
                    icon = Icons.Default.CheckCircle,
                    iconBg = Warning.copy(alpha = 0.1f),
                    iconTint = Warning,
                    label = "Ragu-ragu",
                    value = "${uiState.raguRagu}"
                )
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        ExammateButton(
            text = "Kirim Jawaban",
            onClick = onSubmit
        )

        Spacer(modifier = Modifier.height(12.dp))

        ExammateOutlinedButton(
            text = "Kembali",
            onClick = onBack,
            textColor = TextSecondary
        )
    }
}

@Composable
private fun StatRingkasan(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    iconBg: Color,
    iconTint: Color,
    label: String,
    value: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(iconBg),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(20.dp),
                tint = iconTint
            )
        }
        Spacer(modifier = Modifier.width(16.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.weight(1f)
        )
        Text(
            text = value,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
    }
}
