package com.mirego.debugpanel.repository

import com.mirego.debugpanel.usecase.DebugPanelItemViewData

interface DebugPanelRepository {
    fun getCurrentToggleValue(identifier: String, defaultValue: Boolean): Boolean
    fun onToggleUpdated(viewData: DebugPanelItemViewData.Toggle, isOn: Boolean)

    fun getCurrentTextFieldValue(identifier: String, defaultValue: String): String
    fun onTextFieldUpdated(viewData: DebugPanelItemViewData.TextField, text: String)

    fun getCurrentPickerValue(identifier: String): String?
    fun onPickerUpdated(viewData: DebugPanelItemViewData.Picker, identifier: String)

    fun getCurrentDatePickerValue(identifier: String): Long?
}
