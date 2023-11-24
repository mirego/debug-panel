package com.mirego.debugpanel.sample

import com.mirego.debugpanel.annotations.DebugPanel
import com.mirego.debugpanel.annotations.DisplayName
import com.mirego.debugpanel.annotations.Identifier
import com.mirego.debugpanel.config.DebugPanelButton
import com.mirego.debugpanel.config.DebugPanelDatePicker
import com.mirego.debugpanel.config.DebugPanelLabel
import com.mirego.debugpanel.config.DebugPanelPicker
import com.mirego.debugpanel.config.DebugPanelTextField
import com.mirego.debugpanel.config.DebugPanelToggle

@Suppress("unused")
@DebugPanel("Sample", "com.mirego.debugpanel", includeResetButton = true)
data class DebugPanelConfig(
    @Identifier("PREVIEW_MODE") val previewMode: DebugPanelToggle,
    @DisplayName("First name input") val firstNameInput: DebugPanelTextField,
    @DisplayName("Last name input") val lastNameInput: DebugPanelTextField,
    @DisplayName("Environment") val environments: DebugPanelPicker,
    @DisplayName("Reset onboarding") val resetOnboarding: DebugPanelButton,
    @DisplayName("Language") val language: Language,
    @DisplayName("Date picker") val date: DebugPanelDatePicker
)
