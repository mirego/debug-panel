package com.mirego.debugpanel.sample

import com.mirego.debugpanel.annotations.DebugPanel
import com.mirego.debugpanel.annotations.DisplayName
import com.mirego.debugpanel.config.DebugPanelButton
import com.mirego.debugpanel.config.DebugPanelLabel
import com.mirego.debugpanel.config.DebugPanelPicker
import com.mirego.debugpanel.config.DebugPanelTextField
import com.mirego.debugpanel.config.DebugPanelToggle

@Suppress("unused")
@DebugPanel("Sample")
data class DebugPanelConfig(
    val previewMode: DebugPanelToggle,
    @DisplayName("First name:") val firstName: DebugPanelLabel,
    @DisplayName("Last name input") var lastNameInput: DebugPanelTextField,
    @DisplayName("Environment") val environments: DebugPanelPicker,
    @DisplayName("Reset onboarding") val resetOnboarding: DebugPanelButton,
    @DisplayName("Language") val language: Language
)
