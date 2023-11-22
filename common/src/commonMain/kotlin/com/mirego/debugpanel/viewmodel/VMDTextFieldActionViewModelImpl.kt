package com.mirego.debugpanel.viewmodel

import com.mirego.trikot.viewmodels.declarative.components.impl.VMDTextFieldViewModelImpl
import com.mirego.trikot.viewmodels.declarative.viewmodel.internal.VMDFlowProperty
import com.mirego.trikot.viewmodels.declarative.viewmodel.internal.emit
import kotlinx.coroutines.CoroutineScope

open class VMDTextFieldActionViewModelImpl(coroutineScope: CoroutineScope) : VMDTextFieldViewModelImpl(coroutineScope), VMDTextFieldActionViewModel {
    private val actionDelegate = emit({}, this, coroutineScope)
    override var action: () -> Unit by actionDelegate

    override val propertyMapping: Map<String, VMDFlowProperty<*>> by lazy {
        super.propertyMapping.toMutableMap().also {
            it[this::action.name] = actionDelegate
        }
    }
}
