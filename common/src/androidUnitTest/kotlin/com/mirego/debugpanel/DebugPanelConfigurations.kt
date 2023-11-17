package com.mirego.debugpanel

import com.mirego.debugpanel.annotations.DebugPanel
import com.mirego.debugpanel.annotations.DisplayName
import com.mirego.debugpanel.annotations.Identifier
import com.mirego.debugpanel.config.DebugPanelButton
import com.mirego.debugpanel.config.DebugPanelLabel
import com.mirego.debugpanel.config.DebugPanelPicker
import com.mirego.debugpanel.config.DebugPanelTextField
import com.mirego.debugpanel.config.DebugPanelToggle
import com.mirego.debugpanel.usecase.TestEnum

@Suppress("unused")
@DebugPanel("TestRepository", "com.mirego.debugpanel", includeResetButton = false)
data class TestRepositoryConfig(
    val toggle: DebugPanelToggle,
    val action: DebugPanelButton,
    var textField: DebugPanelTextField,
    val picker: DebugPanelPicker
)

@Suppress("unused")
@DebugPanel("TestRepositoryIdentifier", "com.mirego.debugpanel", includeResetButton = false)
data class TestRepositoryIdentifierConfig(
    @Identifier("TOGGLE_IDENTIFIER") val toggle: DebugPanelToggle
)

@Suppress("unused")
@DebugPanel("TestUseCase", "com.mirego.debugpanel", includeResetButton = false)
data class TestUseCaseConfig(
    val toggle: DebugPanelToggle,
    val button: DebugPanelButton,
    var textField: DebugPanelTextField,
    val picker: DebugPanelPicker,
    val label: DebugPanelLabel,
    val enum: TestEnum
)

@Suppress("unused")
@DebugPanel("TestUseCaseDisplayName", "com.mirego.debugpanel", includeResetButton = false)
data class TestUseCaseDisplayNameConfig(
    @DisplayName("Test toggle") val toggle: DebugPanelToggle,
    @DisplayName("Test button") val button: DebugPanelButton,
    @DisplayName("Test text field") var textField: DebugPanelTextField,
    @DisplayName("Test picker") val picker: DebugPanelPicker,
    @DisplayName("Test label") val label: DebugPanelLabel,
    @DisplayName("Test enum") val enum: TestEnum
)

@Suppress("unused")
@DebugPanel("TestUseCaseIdentifier", "com.mirego.debugpanel", includeResetButton = false)
data class TestUseCaseIdentifierConfig(
    @Identifier("TOGGLE_KEY") val toggle: DebugPanelToggle,
    @Identifier("BUTTON_KEY") val button: DebugPanelButton,
    @Identifier("TEXT_FIELD_KEY") var textField: DebugPanelTextField,
    @Identifier("PICKER_KEY") val picker: DebugPanelPicker,
    @Identifier("LABEL_KEY") val label: DebugPanelLabel,
    @Identifier("ENUM_KEY") val enum: TestEnum
)
