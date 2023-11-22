package com.mirego.debugpanel.viewmodel

import com.mirego.trikot.viewmodels.declarative.components.VMDTextFieldViewModel

interface VMDTextFieldActionViewModel : VMDTextFieldViewModel {
    val action: () -> Unit
}
