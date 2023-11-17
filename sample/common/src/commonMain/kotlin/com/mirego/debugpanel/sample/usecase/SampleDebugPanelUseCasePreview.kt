package com.mirego.debugpanel.sample.usecase

import com.mirego.debugpanel.config.DebugPanelPickerItem
import com.mirego.debugpanel.sample.Language
import com.mirego.debugpanel.usecase.DebugPanelUseCasePreview
import com.mirego.debugpanel.usecase.DebugPanelViewData
import com.mirego.debugpanel.usecase.SampleDebugPanelUseCase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

class SampleDebugPanelUseCasePreview : SampleDebugPanelUseCase, DebugPanelUseCasePreview() {
    override fun createViewData(
        initialPreviewMode: Boolean,
        initialLastNameInput: String,
        initialEnvironments: String?,
        initialLanguage: Language?,
        firstName: Flow<String>,
        environments: List<DebugPanelPickerItem>,
        resetOnboarding: () -> Unit
    ): DebugPanelViewData = createViewData()

    override fun getPreviewMode(): Flow<Boolean?> = flowOf(null)

    override fun getLastNameInput(): Flow<String?> = flowOf(null)

    override fun getEnvironments(): Flow<String?> = flowOf(null)

    override fun getLanguage(): Flow<String?> = flowOf(null)

    override fun resetSettings() = Unit
}
