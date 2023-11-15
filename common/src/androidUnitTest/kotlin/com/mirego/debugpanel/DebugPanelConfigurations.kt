package com.mirego.debugpanel

import com.mirego.debugpanel.annotations.DebugPanel
import com.mirego.debugpanel.annotations.DisplayName
import com.mirego.debugpanel.usecase.TestEnum

@Suppress("unused")
@DebugPanel("TestRepository")
data class TestRepositoryConfig(
    val toggle: Boolean,
    val action: () -> Unit,
    var textField: String,
    val picker: List<DebugPanelPickerItem>
)

@Suppress("unused")
@DebugPanel("TestUseCase")
data class TestUseCaseConfig(
    val toggle: Boolean,
    val button: () -> Unit,
    var textField: String,
    val picker: List<DebugPanelPickerItem>,
    val label: String,
    val enum: TestEnum
)

@Suppress("unused")
@DebugPanel("TestUseCaseDisplayName")
data class TestUseCaseDisplayNameConfig(
    @DisplayName("Test toggle") val toggle: Boolean,
    @DisplayName("Test button") val button: () -> Unit,
    @DisplayName("Test text field") var textField: String,
    @DisplayName("Test picker") val picker: List<DebugPanelPickerItem>,
    @DisplayName("Test label") val label: String,
    @DisplayName("Test enum") val enum: TestEnum
)
