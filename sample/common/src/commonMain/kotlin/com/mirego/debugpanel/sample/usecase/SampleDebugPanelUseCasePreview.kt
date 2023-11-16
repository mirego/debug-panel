package com.mirego.debugpanel.sample.usecase

import com.mirego.debugpanel.config.DebugPanelPickerItem
import com.mirego.debugpanel.sample.Language
import com.mirego.debugpanel.usecase.DebugPanelItemViewData
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
    ): DebugPanelViewData = DebugPanelViewData(
        listOf(
            DebugPanelItemViewData.Toggle(
                identifier = "toggle",
                label = "Toggle",
                initialValue = true
            ),
            DebugPanelItemViewData.TextField(
                identifier = "textField",
                placeholder = "Text field",
                initialValue = ""
            ),
            DebugPanelItemViewData.Label(
                identifier = "label",
                label = "Label:",
                value = flowOf("Value")
            ),
            DebugPanelItemViewData.Picker(
                identifier = "picker",
                label = "Picker",
                initialValue = "",
                items = listOf(DebugPanelPickerItem("id", "Item"))
            ),
            DebugPanelItemViewData.Button(
                identifier = "button",
                label = "Tap me",
                action = {}
            )
        )
    )

    override fun getPreviewMode(): Flow<Boolean?> = flowOf(null)

    override fun getLastNameInput(): Flow<String?> = flowOf(null)

    override fun getEnvironments(): Flow<String?> = flowOf(null)

    override fun getLanguage(): Flow<String?> = flowOf(null)
}
