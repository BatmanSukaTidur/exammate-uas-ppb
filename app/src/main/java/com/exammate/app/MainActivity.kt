package com.exammate.app

import android.content.res.Configuration
import android.os.Bundle
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.exammate.app.presentation.ExammateApp
import com.exammate.app.presentation.theme.ExammateTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        window.setFlags(
            WindowManager.LayoutParams.FLAG_SECURE,
            WindowManager.LayoutParams.FLAG_SECURE
        )

        enableEdgeToEdge()

        setContent {
            ExammateTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    ExammateApp()
                }
            }
        }
    }

    override fun onMultiWindowModeChanged(isInMultiWindow: Boolean, newConfig: Configuration) {
        super.onMultiWindowModeChanged(isInMultiWindow, newConfig)
        if (isInMultiWindow) {
            moveTaskToBack(true)
        }
    }

    override fun onPictureInPictureModeChanged(isInPictureInPictureMode: Boolean, newConfig: Configuration) {
        super.onPictureInPictureModeChanged(isInPictureInPictureMode, newConfig)
        if (isInPictureInPictureMode) {
            moveTaskToBack(true)
        }
    }
}
