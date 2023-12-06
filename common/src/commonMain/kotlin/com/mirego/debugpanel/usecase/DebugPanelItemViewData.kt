package com.mirego.debugpanel.usecase

import com.mirego.debugpanel.config.DebugPanelPickerItem
import com.mirego.trikot.viewmodels.declarative.content.VMDIdentifiableContent
import kotlinx.coroutines.flow.Flow

sealed interface DebugPanelItemViewData : VMDIdentifiableContent {
    val label: String
    val isDirty: Flow<Boolean>

    data class Toggle(
        override val identifier: String,
        override val label: String,
        val initialValue: Boolean?,
        override val isDirty: Flow<Boolean>
    ) : DebugPanelItemViewData

    data class TextField(
        override val identifier: String,
        override val label: String,
        val initialValue: String?,
        override val isDirty: Flow<Boolean>
    ) : DebugPanelItemViewData

    data class Label(
        override val identifier: String,
        override val label: String,
        val value: Flow<String>,
        override val isDirty: Flow<Boolean>
    ) : DebugPanelItemViewData

    data class Picker(
        override val identifier: String,
        override val label: String,
        val initialValue: String?,
        val items: List<DebugPanelPickerItem>,
        override val isDirty: Flow<Boolean>
    ) : DebugPanelItemViewData

    data class DatePicker(
        override val identifier: String,
        override val label: String,
        val initialValue: Long?,
        override val isDirty: Flow<Boolean>
    ) : DebugPanelItemViewData

    data class Button(
        override val identifier: String,
        override val label: String,
        val action: () -> Unit,
        override val isDirty: Flow<Boolean>
    ) : DebugPanelItemViewData
}
