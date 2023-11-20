package com.mirego.debugpanel.viewmodel

import com.mirego.trikot.viewmodels.declarative.components.VMDButtonViewModel
import com.mirego.trikot.viewmodels.declarative.components.VMDPickerViewModel
import com.mirego.trikot.viewmodels.declarative.components.VMDTextFieldViewModel
import com.mirego.trikot.viewmodels.declarative.components.VMDTextViewModel
import com.mirego.trikot.viewmodels.declarative.components.VMDToggleViewModel
import com.mirego.trikot.viewmodels.declarative.components.impl.VMDContentPickerItemViewModelImpl
import com.mirego.trikot.viewmodels.declarative.content.VMDIdentifiableContent
import com.mirego.trikot.viewmodels.declarative.content.VMDTextContent

sealed interface DebugPanelItemViewModel : VMDIdentifiableContent {
    data class Toggle(
        override val identifier: String,
        val viewModel: VMDToggleViewModel<VMDTextContent>
    ) : DebugPanelItemViewModel

    data class TextField(
        override val identifier: String,
        val viewModel: VMDTextFieldViewModel
    ) : DebugPanelItemViewModel

    data class Button(
        override val identifier: String,
        val viewModel: VMDButtonViewModel<VMDTextContent>
    ) : DebugPanelItemViewModel

    data class Label(
        override val identifier: String,
        val label: VMDTextViewModel,
        val viewModel: VMDTextViewModel
    ) : DebugPanelItemViewModel

    data class Picker(
        override val identifier: String,
        val label: VMDTextViewModel,
        val viewModel: VMDPickerViewModel<VMDContentPickerItemViewModelImpl<VMDTextContent>>
    ) : DebugPanelItemViewModel

    data class DatePicker(
        override val identifier: String,
        val label: VMDTextViewModel,
        val viewModel: VMDTextFieldViewModel
    ) : DebugPanelItemViewModel
}
