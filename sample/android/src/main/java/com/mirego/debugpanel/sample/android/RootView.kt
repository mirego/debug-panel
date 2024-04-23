package com.mirego.debugpanel.sample.android

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mirego.debugpanel.DebugPanelTheme
import com.mirego.debugpanel.DebugPanelView
import com.mirego.debugpanel.sample.usecase.SampleDebugPanelUseCasePreview
import com.mirego.debugpanel.sample.viewmodel.RootViewModel
import com.mirego.debugpanel.sample.viewmodel.RootViewModelImpl
import com.mirego.trikot.viewmodels.declarative.util.CoroutineScopeProvider
import kotlinx.coroutines.CoroutineExceptionHandler

@Composable
fun RootView(viewModel: RootViewModel) {
    Column(
        Modifier
            .padding(16.dp)
            .fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Text(
            viewModel.title,
            fontSize = 24.sp,
            fontWeight = FontWeight.Medium,
        )

        DebugPanelView(viewModel.debugPanel)
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFFFFF)
@Composable
private fun Preview() {
    DebugPanelTheme {
        RootView(
            RootViewModelImpl(
                CoroutineScopeProvider.provideMainWithSuperviserJob(
                    CoroutineExceptionHandler { _, exception ->
                        println("CoroutineExceptionHandler got $exception")
                    },
                ),
                SampleDebugPanelUseCasePreview(),
            ),
        )
    }
}
