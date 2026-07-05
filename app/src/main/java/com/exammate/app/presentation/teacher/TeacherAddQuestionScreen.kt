package com.exammate.app.presentation.teacher

import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material.icons.filled.Quiz
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
fun TeacherAddQuestionScreen(
    categoryId: Int,
    onBack: () -> Unit,
    viewModel: TeacherAddQuestionViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val optionLabels = listOf("A", "B", "C", "D", "E")

    LaunchedEffect(categoryId) {
        viewModel.loadCategory(categoryId)
    }

    if (uiState.isSuccess) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Tambah Soal", color = Color.White) },
                    navigationIcon = {
                        IconButton(onClick = { viewModel.reset(); onBack() }) {
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
                    .padding(padding),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = null,
                    modifier = Modifier.size(80.dp),
                    tint = Success
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Soal Berhasil Ditambahkan!",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(24.dp))
                ExammateButton(text = "Kembali", onClick = { viewModel.reset(); onBack() })
            }
        }
        return
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Tambah Soal - ${uiState.category?.nama ?: ""}",
                        color = Color.White
                    )
                },
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
                value = uiState.pertanyaan,
                onValueChange = { viewModel.onPertanyaanChange(it) },
                label = "Pertanyaan",
                leadingIcon = Icons.Default.Quiz,
                minLines = 2,
                maxLines = 4,
                singleLine = false
            )

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Opsi Jawaban",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold
            )

            Text(
                text = "Pilih jawaban yang benar dengan radio button",
                style = MaterialTheme.typography.bodySmall,
                color = TextSecondary
            )

            Spacer(modifier = Modifier.height(12.dp))

            optionLabels.forEachIndexed { index, label ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(
                        selected = uiState.jawabanBenar == index,
                        onClick = { viewModel.onJawabanBenarChange(index) }
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "$label.",
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.width(24.dp)
                    )
                    ExammateTextField(
                        value = uiState.opsi.getOrElse(index) { "" },
                        onValueChange = { viewModel.onOpsiChange(index, it) },
                        label = "Opsi $label",
                        modifier = Modifier.weight(1f)
                    )
                }
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
                text = "Simpan Soal",
                onClick = { viewModel.saveQuestion() }
            )

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}
