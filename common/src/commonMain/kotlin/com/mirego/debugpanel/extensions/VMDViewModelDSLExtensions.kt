package com.mirego.debugpanel.extensions

import com.mirego.debugpanel.viewmodel.VMDTextFieldActionViewModelImpl
import com.mirego.trikot.viewmodels.declarative.components.impl.VMDTextFieldViewModelImpl
import com.mirego.trikot.viewmodels.declarative.viewmodel.VMDViewModelDSL

fun VMDViewModelDSL.textFieldAction(text: String = "", placeholder: String = "", action: () -> Unit, closure: VMDTextFieldViewModelImpl.() -> Unit = {}) = VMDTextFieldActionViewModelImpl(coroutineScope).apply {
    this.text = text
    this.placeholder = placeholder
    this.action = action
    closure()
}
