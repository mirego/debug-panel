package com.mirego.debugpanel.sample.android

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.lifecycle.lifecycleScope
import com.mirego.debugpanel.sample.viewmodel.ApplicationViewModelImpl

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val applicationViewModel = getInitialViewModel {
            ApplicationViewModelImpl(lifecycleScope)
        }

        setContent {
            DebugPanelTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    RootView(applicationViewModel.rootViewModel)
                }
            }
        }
    }
}
