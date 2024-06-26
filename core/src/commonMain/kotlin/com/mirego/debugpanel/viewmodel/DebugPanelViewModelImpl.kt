package com.mirego.debugpanel.viewmodel

import com.mirego.debugpanel.extensions.datePicker
import com.mirego.debugpanel.service.dateFormatter
import com.mirego.debugpanel.usecase.DebugPanelItemViewData
import com.mirego.debugpanel.usecase.DebugPanelUseCase
import com.mirego.debugpanel.usecase.DebugPanelViewData
import com.mirego.trikot.viewmodels.declarative.components.impl.VMDContentPickerItemViewModelImpl
import com.mirego.trikot.viewmodels.declarative.content.VMDTextContent
import com.mirego.trikot.viewmodels.declarative.viewmodel.VMDViewModelImpl
import com.mirego.trikot.viewmodels.declarative.viewmodel.buttonWithText
import com.mirego.trikot.viewmodels.declarative.viewmodel.list
import com.mirego.trikot.viewmodels.declarative.viewmodel.picker
import com.mirego.trikot.viewmodels.declarative.viewmodel.text
import com.mirego.trikot.viewmodels.declarative.viewmodel.textField
import com.mirego.trikot.viewmodels.declarative.viewmodel.toggleWithText
import kotlin.time.Duration.Companion.milliseconds
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

open class DebugPanelViewModelImpl(
    coroutineScope: CoroutineScope,
    private val useCase: DebugPanelUseCase,
    viewDataFlow: Flow<DebugPanelViewData>,
) : VMDViewModelImpl(coroutineScope), DebugPanelViewModel {
    private val DebugPanelItemViewData.labelWithDirtyIndicator: Flow<String>
        get() = isDirty.map { isDirty ->
            label + "*".takeIf { isDirty }.orEmpty()
        }

    override val items = list(
        viewDataFlow.map { viewData ->
            viewData.items.map { item ->
                when (item) {
                    is DebugPanelItemViewData.Toggle -> createToggle(item)
                    is DebugPanelItemViewData.TextField -> createTextField(item)
                    is DebugPanelItemViewData.Button -> createButton(item)
                    is DebugPanelItemViewData.Label -> createLabel(item)
                    is DebugPanelItemViewData.Picker -> createPicker(item)
                    is DebugPanelItemViewData.DatePicker -> createDatePicker(item)
                }
            }
        },
    )

    private fun createToggle(viewData: DebugPanelItemViewData.Toggle) =
        DebugPanelItemViewModel.Toggle(
            identifier = viewData.identifier,
            viewModel = toggleWithText(viewData.label, viewData.initialValue ?: false) {
                bindContent(viewData.labelWithDirtyIndicator.map { VMDTextContent(it) })

                coroutineScope.launch {
                    flowForProperty(::isOn)
                        .drop(1)
                        .distinctUntilChanged()
                        .collect {
                            useCase.onToggleUpdated(viewData, it)
                        }
                }
            },
        )

    private fun createTextField(viewData: DebugPanelItemViewData.TextField) =
        DebugPanelItemViewModel.TextField(
            identifier = viewData.identifier,
            viewModel = textField(text = viewData.initialValue.orEmpty(), placeholder = viewData.label) {
                coroutineScope.launch {
                    flowForProperty(::text)
                        .drop(1)
                        .distinctUntilChanged()
                        .debounce(500.milliseconds)
                        .collect {
                            useCase.onTextFieldUpdated(viewData, it)
                        }
                }
            },
        )

    private fun createButton(viewData: DebugPanelItemViewData.Button) =
        DebugPanelItemViewModel.Button(
            identifier = viewData.identifier,
            viewModel = buttonWithText(viewData.label) {
                setAction(viewData.action)
            },
        )

    private fun createLabel(viewData: DebugPanelItemViewData.Label) =
        DebugPanelItemViewModel.Label(
            identifier = viewData.identifier,
            label = text(viewData.label),
            viewModel = text(viewData.identifier) {
                bindText(viewData.value)
            },
        )

    private fun createPicker(viewData: DebugPanelItemViewData.Picker): DebugPanelItemViewModel.Picker {
        val picker = picker(
            elements = viewData.items.map {
                VMDContentPickerItemViewModelImpl(
                    coroutineScope,
                    VMDTextContent(it.text),
                    it.identifier,
                )
            },
            initialSelectedId = viewData.initialValue,
        ) {
            coroutineScope.launch {
                flowForProperty(::selectedIndex)
                    .drop(1)
                    .distinctUntilChanged()
                    .collect { index ->
                        elements.getOrNull(index)?.identifier?.let {
                            useCase.onPickerUpdated(viewData, it)
                        }
                    }
            }
        }
        return DebugPanelItemViewModel.Picker(
            identifier = viewData.identifier,
            label = text(viewData.label) {
                bindText(viewData.labelWithDirtyIndicator)
            },
            selectedItem = text {
                val initialValue = text
                bindText(
                    picker.flowForProperty(picker::selectedIndex).map { index ->
                        picker.elements.getOrNull(index)?.content?.text ?: initialValue
                    },
                )
            },
            viewModel = picker,
        )
    }

    private fun createDatePicker(viewData: DebugPanelItemViewData.DatePicker) =
        DebugPanelItemViewModel.DatePicker(
            identifier = viewData.identifier,
            label = text(viewData.label) {
                bindText(viewData.labelWithDirtyIndicator)
            },
            viewModel = datePicker(initialDate = viewData.initialValue) {
                isEnabled = false
                bindText(
                    flowForProperty(::date).map { date ->
                        date?.let { dateFormatter.format(it) }.orEmpty()
                    },
                )

                coroutineScope.launch {
                    flowForProperty(::date)
                        .drop(1)
                        .distinctUntilChanged()
                        .filterNotNull()
                        .collect { date ->
                            useCase.onDatePickerUpdated(viewData, date)
                        }
                }
            },
        )
}
