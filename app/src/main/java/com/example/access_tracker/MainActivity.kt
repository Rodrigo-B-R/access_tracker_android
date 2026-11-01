package com.example.access_tracker

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.access_tracker.navigation.AppNavHost
import com.example.access_tracker.ui.theme.Access_trackerTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Access_trackerTheme {
                AppNavHost()
            }
        }
    }
}
