package com.mirego.debugpanel.viewmodel.preview

import com.mirego.debugpanel.usecase.DebugPanelUseCasePreview
import com.mirego.debugpanel.viewmodel.DebugPanelViewModelImpl
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.flowOf

class DebugPanelPreviewsFactory {
    private fun createCoroutineScope() = CoroutineScope(Dispatchers.Main.immediate + SupervisorJob())

    private val useCase = DebugPanelUseCasePreview()

    fun debugPanel(): DebugPanelViewModelImpl = DebugPanelViewModelImpl(createCoroutineScope(), useCase, flowOf(useCase.createViewData()))
}
