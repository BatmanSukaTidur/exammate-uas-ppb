package com.exammate.app.presentation.teacher

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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Subject
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.exammate.app.presentation.component.ExammateButton
import com.exammate.app.presentation.component.ExammateTextField
import com.exammate.app.presentation.theme.Primary
import com.exammate.app.presentation.theme.Success
import com.exammate.app.presentation.theme.TextSecondary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TeacherCreateExamScreen(
    onBack: () -> Unit,
    viewModel: TeacherCreateExamViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    if (uiState.isSuccess) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Buat Ujian", color = Color.White) },
                    navigationIcon = {
                        IconButton(onClick = { viewModel.reset(); onBack() }) {
                            Icon(Icons.Default.ArrowBack, contentDescription = null, tint = Color.White)
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = Primary)
                )
            }
        ) { padding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = null,
                        modifier = Modifier.size(80.dp),
                        tint = Success
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Ujian Berhasil Dibuat!",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Token: ${uiState.token}",
                        style = MaterialTheme.typography.titleLarge,
                        color = Primary,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                    ExammateButton(text = "Kembali", onClick = { viewModel.reset(); onBack() })
                }
            }
        }
        return
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Buat Ujian Baru", color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = null, tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Primary)
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            ExammateTextField(
                value = uiState.namaUjian,
                onValueChange = { viewModel.onNamaUjianChange(it) },
                label = "Nama Ujian",
                leadingIcon = Icons.Default.Description
            )

            Spacer(modifier = Modifier.height(16.dp))

            ExammateTextField(
                value = uiState.mapel,
                onValueChange = { viewModel.onMapelChange(it) },
                label = "Mata Pelajaran",
                leadingIcon = Icons.Default.Subject
            )

            Spacer(modifier = Modifier.height(16.dp))

            ExammateTextField(
                value = uiState.token,
                onValueChange = { viewModel.onTokenChange(it) },
                label = "Token Ujian",
                leadingIcon = Icons.Default.Lock
            )

            Spacer(modifier = Modifier.height(16.dp))

            ExammateTextField(
                value = uiState.durasiMenit,
                onValueChange = { viewModel.onDurasiChange(it) },
                label = "Durasi (menit)",
                leadingIcon = Icons.Default.Schedule
            )

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Pilih Bank Soal",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold
            )

            Spacer(modifier = Modifier.height(8.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        uiState.categories.take(2).forEach { cat ->
                            FilterChip(
                                selected = uiState.selectedCategoryId == cat.id,
                                onClick = { viewModel.onCategorySelected(cat.id) },
                                label = {
                                    Text(
                                        cat.nama,
                                        style = MaterialTheme.typography.labelMedium
                                    )
                                },
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                    if (uiState.categories.size > 2) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            uiState.categories.drop(2).forEach { cat ->
                                FilterChip(
                                    selected = uiState.selectedCategoryId == cat.id,
                                    onClick = { viewModel.onCategorySelected(cat.id) },
                                    label = {
                                        Text(
                                            cat.nama,
                                            style = MaterialTheme.typography.labelMedium
                                        )
                                    },
                                    modifier = Modifier.weight(1f)
                                )
                            }
                        }
                    }
                }
            }

            if (uiState.selectedCategoryId != null) {
                val cat = uiState.categories.find { it.id == uiState.selectedCategoryId }
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "${cat?.soal?.size ?: 0} soal dipilih",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary
                )
            }

            if (uiState.errorMessage != null) {
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = uiState.errorMessage ?: "",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.error
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            ExammateButton(
                text = "Buat Ujian",
                onClick = { viewModel.createExam() },
                isLoading = uiState.isLoading
            )
        }
    }
}
