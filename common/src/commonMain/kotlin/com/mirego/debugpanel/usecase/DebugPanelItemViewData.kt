package com.mirego.debugpanel.usecase

import com.mirego.debugpanel.DebugPanelPickerItem
import com.mirego.trikot.viewmodels.declarative.content.VMDIdentifiableContent
import kotlinx.coroutines.flow.Flow

sealed interface DebugPanelItemViewData : VMDIdentifiableContent {
    data class Toggle(
        override val identifier: String,
        val label: String,
        val initialValue: Boolean
    ) : DebugPanelItemViewData

    data class TextField(
        override val identifier: String,
        val placeholder: String,
        val initialValue: String
    ) : DebugPanelItemViewData

    data class Label(
        override val identifier: String,
        val label: String,
        val value: Flow<String>?
    ) : DebugPanelItemViewData

    data class Picker(
        override val identifier: String,
        val label: String,
        val initialValue: String,
        val items: List<DebugPanelPickerItem>
    ) : DebugPanelItemViewData

    data class Button(
        override val identifier: String,
        val label: String,
        val action: () -> Unit
    ) : DebugPanelItemViewData
}
