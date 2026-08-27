package com.aviad.chordstv

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.aviad.chordstv.ui.navigation.AppNavigation
import com.aviad.chordstv.ui.theme.ChordsTvTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val container = (application as ChordsTvApp).container
        setContent {
            ChordsTvTheme {
                AppNavigation(container = container)
            }
        }
    }
}
