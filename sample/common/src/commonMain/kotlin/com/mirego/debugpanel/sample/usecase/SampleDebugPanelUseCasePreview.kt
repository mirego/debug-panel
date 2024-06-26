package com.mirego.debugpanel.sample.usecase

import com.mirego.debugpanel.config.DebugPanelPickerItem
import com.mirego.debugpanel.sample.Language
import com.mirego.debugpanel.usecase.DebugPanelUseCasePreview
import com.mirego.debugpanel.usecase.DebugPanelViewData
import com.mirego.debugpanel.usecase.SampleDebugPanelComponentsVisibility
import com.mirego.debugpanel.usecase.SampleDebugPanelUseCase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

class SampleDebugPanelUseCasePreview : SampleDebugPanelUseCase, DebugPanelUseCasePreview() {
    override fun createViewData(
        initialPreviewMode: Boolean,
        initialLastNameInput: String,
        initialEnvironments: String?,
        initialLanguage: Language?,
        initialDate: Long?,
        firstName: Flow<String>,
        environments: List<DebugPanelPickerItem>,
        reset: () -> Unit,
        componentsVisibility: Flow<SampleDebugPanelComponentsVisibility>,
    ): Flow<DebugPanelViewData> = flowOf(createViewData())

    override fun getPreviewMode(): Flow<Boolean?> = flowOf(null)

    override fun getLastNameInput(): Flow<String?> = flowOf(null)

    override fun getEnvironments(): Flow<String?> = flowOf(null)

    override fun getLanguage(): Flow<String?> = flowOf(null)

    override fun getDate(): Flow<Long?> = flowOf(null)

    override fun getCurrentPreviewMode(): Boolean? = null

    override fun getCurrentLastNameInput(): String? = null

    override fun getCurrentEnvironments(): String? = null

    override fun getCurrentLanguage(): String? = null

    override fun getCurrentDate(): Long? = null

    override fun resetSettings() = Unit
}
