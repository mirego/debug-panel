package com.mirego.debugpanel.usecase

import com.mirego.debugpanel.config.DebugPanelPickerItem
import kotlinx.coroutines.flow.flowOf

open class DebugPanelUseCasePreview : DebugPanelUseCase {
    override fun getCurrentToggleValue(identifier: String, defaultValue: Boolean) = false

    override fun onToggleUpdated(viewData: DebugPanelItemViewData.Toggle, isOn: Boolean) {}

    override fun getCurrentTextFieldValue(identifier: String, defaultValue: String) = ""

    override fun onTextFieldUpdated(viewData: DebugPanelItemViewData.TextField, text: String) {}

    override fun getCurrentPickerValue(identifier: String) = null

    override fun onPickerUpdated(viewData: DebugPanelItemViewData.Picker, identifier: String) {}

    override fun getCurrentDatePickerValue(identifier: String) = null

    fun createViewData() = DebugPanelViewData(
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
}
