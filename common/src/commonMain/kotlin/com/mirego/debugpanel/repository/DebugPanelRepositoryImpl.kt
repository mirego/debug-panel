package com.mirego.debugpanel.repository

import com.mirego.debugpanel.service.DebugPanelSettings
import com.mirego.debugpanel.usecase.DebugPanelItemViewData

abstract class DebugPanelRepositoryImpl : DebugPanelRepository {
    override fun getCurrentToggleValue(identifier: String) =
        DebugPanelSettings.observableSettings.getBooleanOrNull(identifier)

    protected fun getToggleValue(identifier: String) =
        DebugPanelSettings.flowSettings.getBooleanOrNullFlow(identifier)

    override fun onToggleUpdated(viewData: DebugPanelItemViewData.Toggle, isOn: Boolean) {
        DebugPanelSettings.observableSettings.putBoolean(viewData.identifier, isOn)
    }

    override fun getCurrentTextFieldValue(identifier: String) =
        DebugPanelSettings.observableSettings.getStringOrNull(identifier)

    protected fun getTextFieldValue(identifier: String) =
        DebugPanelSettings.flowSettings.getStringOrNullFlow(identifier)

    override fun onTextFieldUpdated(viewData: DebugPanelItemViewData.TextField, text: String) {
        DebugPanelSettings.observableSettings.putString(viewData.identifier, text)
    }

    override fun getCurrentPickerValue(identifier: String) =
        DebugPanelSettings.observableSettings.getStringOrNull(identifier)

    protected fun getPickerValue(identifier: String) =
        DebugPanelSettings.flowSettings.getStringOrNullFlow(identifier)

    override fun onPickerUpdated(viewData: DebugPanelItemViewData.Picker, identifier: String) {
        DebugPanelSettings.observableSettings.putString(viewData.identifier, identifier)
    }

    override fun getCurrentDatePickerValue(identifier: String) =
        DebugPanelSettings.observableSettings.getLongOrNull(identifier)

    protected fun getDatePickerValue(identifier: String) =
        DebugPanelSettings.flowSettings.getLongOrNullFlow(identifier)

    override fun onDatePickerUpdated(viewData: DebugPanelItemViewData.DatePicker, date: Long) {
        DebugPanelSettings.observableSettings.putLong(viewData.identifier, date)
    }

    protected fun removeKeys(vararg keys: String) {
        keys.forEach { DebugPanelSettings.observableSettings.remove(it) }
    }
}
