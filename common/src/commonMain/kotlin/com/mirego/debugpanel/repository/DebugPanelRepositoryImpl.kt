package com.mirego.debugpanel.repository

import com.mirego.debugpanel.settings
import com.mirego.debugpanel.usecase.DebugPanelItemViewData
import com.russhwolf.settings.coroutines.toFlowSettings

abstract class DebugPanelRepositoryImpl : DebugPanelRepository {
    private val flowSettings = settings.toFlowSettings()

    override fun getCurrentToggleValue(identifier: String, defaultValue: Boolean) =
        settings.getBoolean(identifier, defaultValue)

    protected fun getToggleValue(identifier: String) =
        flowSettings.getBooleanOrNullFlow(identifier)

    override fun onToggleUpdated(viewData: DebugPanelItemViewData.Toggle, isOn: Boolean) {
        settings.putBoolean(viewData.identifier, isOn)
    }

    override fun getCurrentTextFieldValue(identifier: String, defaultValue: String) =
        settings.getString(identifier, defaultValue)

    protected fun getTextFieldValue(identifier: String) =
        flowSettings.getStringOrNullFlow(identifier)

    override fun onTextFieldUpdated(viewData: DebugPanelItemViewData.TextField, text: String) {
        settings.putString(viewData.identifier, text)
    }

    override fun getCurrentPickerValue(identifier: String) =
        settings.getStringOrNull(identifier)

    protected fun getPickerValue(identifier: String) =
        flowSettings.getStringOrNullFlow(identifier)

    override fun onPickerUpdated(viewData: DebugPanelItemViewData.Picker, identifier: String) {
        settings.putString(viewData.identifier, identifier)
    }
}