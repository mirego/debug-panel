package com.mirego.debugpanel.sample.usecase

import com.mirego.debugpanel.config.DebugPanelPickerItem
import com.mirego.debugpanel.usecase.DebugPanelUseCasePreview
import com.mirego.debugpanel.usecase.DebugPanelViewData
import com.mirego.debugpanel.usecase.SampleDebugPanelUseCase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

class SampleDebugPanelUseCasePreview : SampleDebugPanelUseCase, DebugPanelUseCasePreview() {
    override fun createViewData(
        initialPreviewMode: Boolean,
        initialEnvironments: String?,
        initialDate: Long?,
        environments: List<DebugPanelPickerItem>,
        resetOnboarding: () -> Unit
    ): DebugPanelViewData = createViewData()

    override fun getPreviewMode(): Flow<Boolean?> = flowOf(null)

    override fun getFirstNameInput(): Flow<String?> = flowOf(null)

    override fun getLastNameInput(): Flow<String?> = flowOf(null)

    override fun getEnvironments(): Flow<String?> = flowOf(null)

    override fun getLanguage(): Flow<String?> = flowOf(null)

    override fun getToggle(): Flow<Boolean?> = flowOf(null)

    override fun getToggleFlow(): Flow<Boolean?> = flowOf(null)

    override fun getDate(): Flow<Long?> = flowOf(null)

    override fun getCurrentPreviewMode(): Boolean? = null

    override fun getCurrentFirstNameInput(): String? = null

    override fun getCurrentLastNameInput(): String? = null

    override fun getCurrentEnvironments(): String? = null

    override fun getCurrentLanguage(): String? = null

    override fun getCurrentToggle(): Boolean? = null

    override fun getCurrentToggleFlow(): Boolean? = null

    override fun getCurrentDate(): Long? = null

    override fun resetSettings() = Unit
}
