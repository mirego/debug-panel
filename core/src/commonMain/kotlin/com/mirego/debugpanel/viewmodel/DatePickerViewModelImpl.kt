package com.mirego.debugpanel.viewmodel

import com.mirego.trikot.viewmodels.declarative.components.impl.VMDTextFieldViewModelImpl
import com.mirego.trikot.viewmodels.declarative.viewmodel.internal.VMDFlowProperty
import com.mirego.trikot.viewmodels.declarative.viewmodel.internal.emit
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow

open class DatePickerViewModelImpl(
    coroutineScope: CoroutineScope,
    initialDate: Long?,
) : VMDTextFieldViewModelImpl(coroutineScope), DatePickerViewModel {
    private val actionDelegate = emit({}, this, coroutineScope)
    override var action: () -> Unit by actionDelegate

    private val dateDelegate = emit(initialDate, this, coroutineScope)
    override var date: Long? by dateDelegate

    override var showPicker: (() -> Unit)? = null

    fun bindText(flow: Flow<String>) {
        updateProperty(this::text, flow)
    }

    override val propertyMapping: Map<String, VMDFlowProperty<*>> by lazy {
        super.propertyMapping.toMutableMap().also {
            it[this::action.name] = actionDelegate
            it[this::date.name] = dateDelegate
        }
    }
}
