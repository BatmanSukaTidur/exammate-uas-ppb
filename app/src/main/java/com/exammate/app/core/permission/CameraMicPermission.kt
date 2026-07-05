package com.exammate.app.core.permission

import android.Manifest
import android.content.pm.PackageManager
import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
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
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.exammate.app.presentation.component.ExammateButton
import com.exammate.app.presentation.theme.Primary
import com.exammate.app.presentation.theme.Success
import com.exammate.app.presentation.theme.Warning
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.withContext

@Composable
fun CameraMicPermissionScreen(
    onPermissionGranted: () -> Unit,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    var cameraGranted by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) ==
                    PackageManager.PERMISSION_GRANTED
        )
    }
    var micGranted by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO) ==
                    PackageManager.PERMISSION_GRANTED
        )
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        cameraGranted = permissions[Manifest.permission.CAMERA] == true
        micGranted = permissions[Manifest.permission.RECORD_AUDIO] == true
    }

    val allGranted = cameraGranted && micGranted

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = Icons.Default.Videocam,
                contentDescription = null,
                modifier = Modifier.size(80.dp),
                tint = Primary
            )

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Aktifkan Kamera & Mikrofon",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Untuk mengikuti ujian, kamu wajib mengaktifkan kamera dan mikrofon sebagai bentuk pengawasan selama ujian berlangsung.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(32.dp))

            PermissionItem(
                icon = Icons.Default.CameraAlt,
                title = "Kamera",
                description = "Digunakan untuk memantau aktivitas selama ujian",
                isGranted = cameraGranted
            )

            Spacer(modifier = Modifier.height(16.dp))

            PermissionItem(
                icon = Icons.Default.Mic,
                title = "Mikrofon",
                description = "Digunakan untuk mendeteksi suara sekitar selama ujian",
                isGranted = micGranted
            )

            // Live camera preview when granted
            if (allGranted) {
                Spacer(modifier = Modifier.height(16.dp))
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.Black)
                ) {
                    CameraPreviewView(lifecycleOwner = lifecycleOwner)
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            if (allGranted) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Success.copy(alpha = 0.1f)
                    )
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                                .background(Success),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "\u2713",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = MaterialTheme.typography.titleMedium.fontSize
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = "Kamera & Mikrofon telah diaktifkan",
                            style = MaterialTheme.typography.titleSmall,
                            color = Success,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            } else {
                ExammateButton(
                    text = "Aktifkan Kamera & Mikrofon",
                    onClick = {
                        permissionLauncher.launch(
                            arrayOf(
                                Manifest.permission.CAMERA,
                                Manifest.permission.RECORD_AUDIO
                            )
                        )
                    }
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (allGranted) {
                ExammateButton(
                    text = "Mulai Ujian",
                    onClick = onPermissionGranted
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            androidx.compose.material3.TextButton(onClick = onBack) {
                Text(
                    text = "Kembali",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
fun CameraPreviewView(lifecycleOwner: androidx.lifecycle.LifecycleOwner) {
    val context = LocalContext.current

    AndroidView(
        factory = { ctx ->
            val previewView = PreviewView(ctx).apply {
                scaleType = PreviewView.ScaleType.FILL_CENTER
                implementationMode = PreviewView.ImplementationMode.COMPATIBLE
            }

            val future = ProcessCameraProvider.getInstance(context)
            future.addListener({
                try {
                    val cameraProvider = future.get()
                    cameraProvider.unbindAll()
                    val preview = Preview.Builder().build()
                    preview.setSurfaceProvider(previewView.surfaceProvider)
                    cameraProvider.bindToLifecycle(lifecycleOwner, CameraSelector.DEFAULT_FRONT_CAMERA, preview)
                } catch (e: Exception) {
                    Log.e("CameraPreview", "Gagal mengaktifkan kamera", e)
                }
            }, ContextCompat.getMainExecutor(context))

            previewView
        },
        modifier = Modifier.fillMaxSize()
    )

    DisposableEffect(lifecycleOwner) {
        onDispose {
            ProcessCameraProvider.getInstance(context).addListener({
                try { ProcessCameraProvider.getInstance(context).get().unbindAll() } catch (_: Exception) { }
            }, ContextCompat.getMainExecutor(context))
        }
    }
}

@Composable
fun MicLevelIndicator(modifier: Modifier = Modifier) {
    var amplitude by remember { mutableFloatStateOf(0f) }

    LaunchedEffect(Unit) {
        withContext(Dispatchers.IO) {
            try {
                val sampleRate = 44100
                val bufferSize = AudioRecord.getMinBufferSize(
                    sampleRate,
                    AudioFormat.CHANNEL_IN_MONO,
                    AudioFormat.ENCODING_PCM_16BIT
                )
                if (bufferSize == AudioRecord.ERROR || bufferSize == AudioRecord.ERROR_BAD_VALUE) return@withContext

                val audioRecord = AudioRecord(
                    MediaRecorder.AudioSource.MIC,
                    sampleRate,
                    AudioFormat.CHANNEL_IN_MONO,
                    AudioFormat.ENCODING_PCM_16BIT,
                    bufferSize
                )

                audioRecord.startRecording()
                val buffer = ShortArray(bufferSize)
                while (isActive) {
                    val read = audioRecord.read(buffer, 0, buffer.size)
                    if (read > 0) {
                        var sum = 0.0
                        for (i in 0 until read) {
                            sum += buffer[i] * buffer[i]
                        }
                        val rms = kotlin.math.sqrt(sum / read)
                        amplitude = (rms / 32768.0).toFloat().coerceIn(0f, 1f)
                    }
                    delay(150)
                }
                audioRecord.stop()
                audioRecord.release()
            } catch (_: kotlinx.coroutines.CancellationException) {
                // Normal cancellation when composable leaves composition
            } catch (e: Exception) {
                Log.e("MicLevel", "AudioRecord error", e)
            }
        }
    }

    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Default.Mic,
            contentDescription = null,
            modifier = Modifier.size(16.dp),
            tint = if (amplitude > 0.01f) Success else Color.Gray
        )
        Spacer(modifier = Modifier.width(4.dp))
        Box(
            modifier = Modifier
                .width(60.dp)
                .height(8.dp)
                .clip(RoundedCornerShape(4.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(amplitude.coerceIn(0.05f, 1f))
                    .matchParentSize()
                    .clip(RoundedCornerShape(4.dp))
                    .background(
                        when {
                            amplitude < 0.3f -> Success
                            amplitude < 0.6f -> Warning
                            else -> com.exammate.app.presentation.theme.Error
                        }
                    )
            )
        }
    }
}

@Composable
private fun PermissionItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    description: String,
    isGranted: Boolean
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isGranted) {
                Success.copy(alpha = 0.05f)
            } else {
                MaterialTheme.colorScheme.surface
            }
        ),
        border = BorderStroke(
            width = 1.dp,
            color = if (isGranted) Success.copy(alpha = 0.3f) else MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
        )
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(
                        if (isGranted) Success.copy(alpha = 0.1f)
                        else Primary.copy(alpha = 0.1f)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    modifier = Modifier.size(24.dp),
                    tint = if (isGranted) Success else Primary
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold
                )
                if (description.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = description,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            if (isGranted) {
                Box(
                    modifier = Modifier
                        .size(24.dp)
                        .clip(CircleShape)
                        .background(Success),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "\u2713",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp
                    )
                }
            }
        }
    }
}
