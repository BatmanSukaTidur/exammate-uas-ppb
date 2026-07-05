package com.exammate.app.presentation.login

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.exammate.app.presentation.component.ExammateButton
import com.exammate.app.presentation.component.ExammateOutlinedButton
import com.exammate.app.presentation.component.ExammateTextField
import com.exammate.app.presentation.theme.Primary

@Composable
fun LoginScreen(
    onLoginSuccess: (role: String) -> Unit,
    onRegisterClick: () -> Unit = {},
    viewModel: LoginViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var passwordVisible by remember { mutableStateOf(false) }
    var showForgotPasswordDialog by remember { mutableStateOf(false) }

    if (uiState.isLoggedIn) {
        onLoginSuccess(uiState.role)
        return
    }

    if (showForgotPasswordDialog) {
        AlertDialog(
            onDismissRequest = { showForgotPasswordDialog = false },
            title = { Text("Lupa Password", fontWeight = FontWeight.Bold) },
            text = { Text("Silakan hubungi admin sekolah untuk mereset password Anda.") },
            confirmButton = {
                TextButton(onClick = { showForgotPasswordDialog = false }) {
                    Text("OK")
                }
            }
        )
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Spacer(modifier = Modifier.height(48.dp))

            Box(
                modifier = Modifier
                    .size(100.dp)
                    .clip(RoundedCornerShape(24.dp))
                    .background(Primary),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "E",
                    style = MaterialTheme.typography.displayLarge,
                    color = MaterialTheme.colorScheme.onPrimary,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Selamat Datang",
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Masuk untuk melanjutkan ke aplikasi ujian online",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(32.dp))

            ExammateTextField(
                value = uiState.nis,
                onValueChange = { viewModel.onNisChange(it) },
                label = "NIS / Email",
                leadingIcon = Icons.Default.Email,
                isError = uiState.errorMessage != null
            )

            Spacer(modifier = Modifier.height(16.dp))

            ExammateTextField(
                value = uiState.password,
                onValueChange = { viewModel.onPasswordChange(it) },
                label = "Password",
                leadingIcon = Icons.Default.Lock,
                trailingIcon = if (passwordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                onTrailingIconClick = { passwordVisible = !passwordVisible },
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                isError = uiState.errorMessage != null,
                errorMessage = uiState.errorMessage
            )

            Spacer(modifier = Modifier.height(24.dp))

            ExammateButton(
                text = "Masuk",
                onClick = { viewModel.login() },
                isLoading = uiState.isLoading
            )

            Spacer(modifier = Modifier.height(16.dp))

            TextButton(onClick = { showForgotPasswordDialog = true }) {
                Text(
                    text = "Lupa Password?",
                    style = MaterialTheme.typography.labelLarge,
                    color = Primary
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            TextButton(onClick = onRegisterClick) {
                Text(
                    text = "Belum punya akun? Daftar",
                    style = MaterialTheme.typography.labelLarge,
                    color = Primary
                )
            }

            Spacer(modifier = Modifier.height(48.dp))
        }
    }
}
