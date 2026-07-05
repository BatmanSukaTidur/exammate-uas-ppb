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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.material.icons.filled.Star
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
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.exammate.app.presentation.component.ExammateButton
import com.exammate.app.presentation.component.ExammateOutlinedButton
import com.exammate.app.presentation.theme.Error
import com.exammate.app.presentation.theme.Primary
import com.exammate.app.presentation.theme.Success
import com.exammate.app.presentation.theme.TextSecondary
import com.exammate.app.presentation.theme.Warning

@Composable
fun HasilUjianScreen(
    examId: Int,
    score: Int,
    total: Int,
    correct: Int,
    selectedAnswers: Map<Int, Int>,
    onBackToHome: () -> Unit,
    viewModel: HasilUjianViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.loadData(examId, score, total, correct, selectedAnswers)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(32.dp))

        Text(
            text = "Hasil Ujian",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = uiState.ujian?.mapel ?: "",
            style = MaterialTheme.typography.titleMedium,
            color = TextSecondary
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Score Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(
                modifier = Modifier.padding(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(100.dp)
                        .clip(CircleShape)
                        .background(
                            when {
                                score >= 85 -> Success.copy(alpha = 0.1f)
                                score >= 70 -> Primary.copy(alpha = 0.1f)
                                score >= 50 -> Warning.copy(alpha = 0.1f)
                                else -> Error.copy(alpha = 0.1f)
                            }
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "$score",
                        style = MaterialTheme.typography.displayLarge,
                        fontWeight = FontWeight.Bold,
                        color = when {
                            score >= 85 -> Success
                            score >= 70 -> Primary
                            score >= 50 -> Warning
                            else -> Error
                        }
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "dari 100",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextSecondary
                )

                Spacer(modifier = Modifier.height(12.dp))

                val badgeText = when {
                    score >= 85 -> "Sangat Baik"
                    score >= 70 -> "Baik"
                    score >= 50 -> "Cukup"
                    else -> "Perlu Belajar Lagi"
                }
                val badgeColor = when {
                    score >= 85 -> Success
                    score >= 70 -> Primary
                    score >= 50 -> Warning
                    else -> Error
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(badgeColor.copy(alpha = 0.1f))
                        .padding(horizontal = 16.dp, vertical = 6.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp),
                            tint = badgeColor
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = badgeText,
                            style = MaterialTheme.typography.labelLarge,
                            color = badgeColor,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Stats
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                StatHasil(
                    icon = Icons.Default.CheckCircle,
                    iconBg = Success.copy(alpha = 0.1f),
                    iconTint = Success,
                    label = "Benar",
                    value = "${uiState.correct}"
                )

                Spacer(modifier = Modifier.height(16.dp))
                androidx.compose.material3.HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
                Spacer(modifier = Modifier.height(16.dp))

                StatHasil(
                    icon = Icons.Default.Cancel,
                    iconBg = Error.copy(alpha = 0.1f),
                    iconTint = Error,
                    label = "Salah",
                    value = "${uiState.wrong}"
                )

                Spacer(modifier = Modifier.height(16.dp))
                androidx.compose.material3.HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
                Spacer(modifier = Modifier.height(16.dp))

                StatHasil(
                    icon = Icons.Default.RadioButtonUnchecked,
                    iconBg = TextSecondary.copy(alpha = 0.1f),
                    iconTint = TextSecondary,
                    label = "Kosong",
                    value = "${uiState.empty}"
                )
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        ExammateButton(
            text = "Kembali ke Beranda",
            onClick = onBackToHome,
            modifier = Modifier.padding(horizontal = 0.dp)
        )

        Spacer(modifier = Modifier.height(12.dp))

        ExammateOutlinedButton(
            text = "Lihat Pembahasan",
            onClick = { }
        )

        Spacer(modifier = Modifier.height(32.dp))
    }
}

@Composable
private fun StatHasil(
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
