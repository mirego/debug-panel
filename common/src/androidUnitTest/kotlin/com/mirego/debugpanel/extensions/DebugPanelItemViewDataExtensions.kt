package com.mirego.debugpanel.extensions

import com.mirego.debugpanel.usecase.DebugPanelItemViewData
import kotlin.test.assertNotNull

val DebugPanelItemViewData.toggle: DebugPanelItemViewData.Toggle
    get() = assertNotNull(this as? DebugPanelItemViewData.Toggle)

val DebugPanelItemViewData.button: DebugPanelItemViewData.Button
    get() = assertNotNull(this as? DebugPanelItemViewData.Button)

val DebugPanelItemViewData.textField: DebugPanelItemViewData.TextField
    get() = assertNotNull(this as? DebugPanelItemViewData.TextField)

val DebugPanelItemViewData.picker: DebugPanelItemViewData.Picker
    get() = assertNotNull(this as? DebugPanelItemViewData.Picker)

val DebugPanelItemViewData.label: DebugPanelItemViewData.Label
    get() = assertNotNull(this as? DebugPanelItemViewData.Label)

val DebugPanelItemViewData.datePicker: DebugPanelItemViewData.DatePicker
    get() = assertNotNull(this as? DebugPanelItemViewData.DatePicker)
