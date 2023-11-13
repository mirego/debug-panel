package com.mirego.debugpanel.sample.android

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.lifecycleScope
import com.mirego.debugpanel.sample.ApplicationViewModelImpl
import com.mirego.debugpanel.sample.RootViewModel
import com.mirego.debugpanel.sample.RootViewModelImpl
import com.mirego.trikot.viewmodels.declarative.util.CoroutineScopeProvider
import kotlinx.coroutines.CoroutineExceptionHandler

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

@Composable
fun RootView(viewModel: RootViewModel) {
    Text("Debug Panel sample")
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    DebugPanelTheme {
        RootView(
            RootViewModelImpl(
                CoroutineScopeProvider.provideMainWithSuperviserJob(
                    CoroutineExceptionHandler { _, exception ->
                        println("CoroutineExceptionHandler got $exception")
                    }
                )
            )
        )
    }
}
