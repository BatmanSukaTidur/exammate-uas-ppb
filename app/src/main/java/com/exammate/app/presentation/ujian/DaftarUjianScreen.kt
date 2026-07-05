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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.HourglassEmpty
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults.SecondaryIndicator
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.exammate.app.data.model.StatusUjian
import com.exammate.app.data.model.Ujian
import com.exammate.app.presentation.theme.Primary
import com.exammate.app.presentation.theme.PrimaryLight
import com.exammate.app.presentation.theme.Success
import com.exammate.app.presentation.theme.TextSecondary
import com.exammate.app.presentation.theme.Warning

@Composable
fun DaftarUjianScreen(
    onStartExam: (Int) -> Unit,
    onViewResult: (Int) -> Unit = {},
    viewModel: DaftarUjianViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val tabs = listOf("Mendatang", "Berlangsung", "Selesai")

    Column(modifier = Modifier.fillMaxSize()) {
        // Header
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Primary)
                .padding(24.dp)
        ) {
            Column {
                Text(
                    text = "Daftar Ujian",
                    style = MaterialTheme.typography.headlineMedium,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Kelola jadwal ujian kamu",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.White.copy(alpha = 0.8f)
                )
            }
        }

        // Tabs
        TabRow(
            selectedTabIndex = uiState.selectedTab,
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = Primary,
            indicator = { tabPositions ->
                SecondaryIndicator(
                    modifier = Modifier.tabIndicatorOffset(tabPositions[uiState.selectedTab]),
                    height = 3.dp,
                    color = Primary
                )
            }
        ) {
            tabs.forEachIndexed { index, title ->
                Tab(
                    selected = uiState.selectedTab == index,
                    onClick = { viewModel.selectTab(index) },
                    text = {
                        Text(
                            text = title,
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = if (uiState.selectedTab == index) FontWeight.Bold else FontWeight.Normal,
                            color = if (uiState.selectedTab == index) Primary else TextSecondary
                        )
                    }
                )
            }
        }

        // Content
        val currentList = when (uiState.selectedTab) {
            0 -> uiState.ujianMendatang
            1 -> uiState.ujianBerlangsung
            else -> uiState.ujianSelesai
        }

        if (currentList.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Default.HourglassEmpty,
                        contentDescription = null,
                        modifier = Modifier.size(64.dp),
                        tint = TextSecondary.copy(alpha = 0.5f)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Tidak ada ujian",
                        style = MaterialTheme.typography.titleMedium,
                        color = TextSecondary
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(currentList) { ujian ->
                    UjianCard(
                        ujian = ujian,
                        onClick = {
                            when (ujian.status) {
                                StatusUjian.BERLANGSUNG -> onStartExam(ujian.id)
                                StatusUjian.SELESAI -> onViewResult(ujian.id)
                                else -> {}
                            }
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun UjianCard(ujian: Ujian, onClick: () -> Unit) {
    val statusColor = when (ujian.status) {
        StatusUjian.MENDATANG -> Warning
        StatusUjian.BERLANGSUNG -> Success
        StatusUjian.SELESAI -> TextSecondary
    }
    val statusText = when (ujian.status) {
        StatusUjian.MENDATANG -> "Mendatang"
        StatusUjian.BERLANGSUNG -> "Sedang Berlangsung"
        StatusUjian.SELESAI -> "Selesai"
    }

    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(52.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(PrimaryLight.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.MenuBook,
                    contentDescription = null,
                    modifier = Modifier.size(26.dp),
                    tint = Primary
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = ujian.mapel,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.CalendarMonth,
                        contentDescription = null,
                        modifier = Modifier.size(14.dp),
                        tint = TextSecondary
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = ujian.tanggal,
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Icon(
                        imageVector = Icons.Default.AccessTime,
                        contentDescription = null,
                        modifier = Modifier.size(14.dp),
                        tint = TextSecondary
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = ujian.jam,
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Schedule,
                        contentDescription = null,
                        modifier = Modifier.size(14.dp),
                        tint = TextSecondary
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "${ujian.durasiMenit} menit",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = null,
                        modifier = Modifier.size(14.dp),
                        tint = TextSecondary
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "${ujian.totalSoal} soal",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary
                    )
                }
            }
            Column(horizontalAlignment = Alignment.End) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(statusColor.copy(alpha = 0.1f))
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = statusText,
                        style = MaterialTheme.typography.labelSmall,
                        color = statusColor,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
    }
}
