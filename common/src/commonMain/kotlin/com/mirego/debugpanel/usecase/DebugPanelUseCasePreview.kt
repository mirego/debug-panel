package com.mirego.debugpanel.usecase

abstract class DebugPanelUseCasePreview : DebugPanelUseCase {
    override fun getCurrentToggleValue(identifier: String, defaultValue: Boolean) = false

    override fun onToggleUpdated(viewData: DebugPanelItemViewData.Toggle, isOn: Boolean) {}

    override fun getCurrentTextFieldValue(identifier: String, defaultValue: String) = ""

    override fun onTextFieldUpdated(viewData: DebugPanelItemViewData.TextField, text: String) {}

    override fun getCurrentPickerValue(identifier: String) = null

    override fun onPickerUpdated(viewData: DebugPanelItemViewData.Picker, identifier: String) {}
}
