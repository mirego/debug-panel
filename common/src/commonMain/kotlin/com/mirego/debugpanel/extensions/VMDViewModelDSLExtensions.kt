package com.mirego.debugpanel.extensions

import com.mirego.debugpanel.viewmodel.DatePickerViewModelImpl
import com.mirego.trikot.viewmodels.declarative.viewmodel.VMDViewModelDSL

fun VMDViewModelDSL.datePicker(
    initialDate: Long?,
    closure: DatePickerViewModelImpl.() -> Unit = {}
) = DatePickerViewModelImpl(
    coroutineScope,
    initialDate
).apply {
    action = { showPicker?.invoke() }
    closure()
}
