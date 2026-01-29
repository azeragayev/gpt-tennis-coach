package com.aaa.gptenniscoach

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.aaa.gptenniscoach.nav.AppNavGraph
import com.aaa.gptenniscoach.ui.theme.TennisCoachTheme
import dagger.hilt.android.AndroidEntryPoint

/**
 * Main activity serving as the entry point for the Tennis Coach application.
 */
@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    /**
     * Initializes the activity and sets up the Compose UI with the app navigation graph.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            TennisCoachTheme {
                AppNavGraph()
            }
        }
    }
}
