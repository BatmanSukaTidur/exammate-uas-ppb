package com.exammate.app.presentation.login

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.exammate.app.presentation.component.ExammateButton
import com.exammate.app.presentation.component.ExammateTextField

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(
    onRegisterSuccess: () -> Unit,
    onBack: () -> Unit,
    viewModel: RegisterViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    if (uiState.isSuccess) {
        AlertDialog(
            onDismissRequest = onRegisterSuccess,
            title = {
                Text("Pendaftaran Berhasil", fontWeight = FontWeight.Bold)
            },
            text = {
                Text("Akun Anda telah berhasil dibuat. Silakan login untuk melanjutkan.")
            },
            confirmButton = {
                Button(onClick = onRegisterSuccess) {
                    Text("OK")
                }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Daftar Akun") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Kembali")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Buat Akun Baru",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = "Isi data diri kamu untuk mendaftar",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(8.dp))

            ExammateTextField(
                value = uiState.nis,
                onValueChange = { viewModel.onNisChange(it) },
                label = "NIS",
                leadingIcon = Icons.Default.Badge,
                isError = uiState.nisError != null,
                errorMessage = uiState.nisError
            )

            ExammateTextField(
                value = uiState.nama,
                onValueChange = { viewModel.onNamaChange(it) },
                label = "Nama Lengkap",
                leadingIcon = Icons.Default.Person,
                isError = uiState.namaError != null,
                errorMessage = uiState.namaError
            )

            ExammateTextField(
                value = uiState.email,
                onValueChange = { viewModel.onEmailChange(it) },
                label = "Email",
                leadingIcon = Icons.Default.Email,
                isError = uiState.emailError != null,
                errorMessage = uiState.emailError
            )

            ExammateTextField(
                value = uiState.password,
                onValueChange = { viewModel.onPasswordChange(it) },
                label = "Password",
                leadingIcon = Icons.Default.Lock,
                visualTransformation = PasswordVisualTransformation(),
                isError = uiState.passwordError != null,
                errorMessage = uiState.passwordError
            )

            ExammateTextField(
                value = uiState.kelas,
                onValueChange = { viewModel.onKelasChange(it) },
                label = "Kelas",
                leadingIcon = Icons.Default.Badge,
                isError = uiState.kelasError != null,
                errorMessage = uiState.kelasError
            )

            Spacer(modifier = Modifier.height(8.dp))

            ExammateButton(
                text = "Daftar",
                onClick = { viewModel.register() },
                isLoading = uiState.isLoading
            )
        }
    }
}
