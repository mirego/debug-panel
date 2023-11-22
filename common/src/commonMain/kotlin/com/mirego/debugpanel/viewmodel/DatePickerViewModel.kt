package com.mirego.debugpanel.viewmodel

import com.mirego.trikot.viewmodels.declarative.components.VMDTextFieldViewModel

interface DatePickerViewModel : VMDTextFieldViewModel {
    val action: () -> Unit
    var showPicker: (() -> Unit)?

    var date: Long
}
