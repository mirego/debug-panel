package com.mirego.debugpanel.sample

import com.mirego.debugpanel.DebugPanelPickerItem
import com.mirego.debugpanel.annotations.DebugPanel
import com.mirego.debugpanel.annotations.DisplayName

@Suppress("unused")
@DebugPanel("Sample")
data class DebugPanelConfig(
    val previewMode: Boolean,
    @DisplayName("First name") val firstName: String,
    @DisplayName("Last name input") var lastNameInput: String,
    @DisplayName("Environment") val environments: List<DebugPanelPickerItem>,
    @DisplayName("Reset onboarding") val resetOnboarding: () -> Unit,
    @DisplayName("Language") val language: Language
)
