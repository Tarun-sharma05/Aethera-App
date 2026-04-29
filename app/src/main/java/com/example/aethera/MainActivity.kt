package com.example.aethera

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.aethera.presentation.navigation.AetheraNavGraph
import com.example.aethera.ui.theme.AetheraTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AetheraTheme {
                AetheraNavGraph()
            }
        }
    }
}
