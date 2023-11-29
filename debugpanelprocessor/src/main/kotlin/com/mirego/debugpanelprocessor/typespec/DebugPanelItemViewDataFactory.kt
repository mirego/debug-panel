package com.mirego.debugpanelprocessor.typespec

import com.mirego.debugpanelprocessor.Component
import com.mirego.debugpanelprocessor.capitalize

internal object DebugPanelItemViewDataFactory {
    fun createToggle(component: Component.Toggle): String {
        val identifier = component.safeIdentifier
        val label = component.safeDisplayName
        val initialValue = "repository.getCurrentToggleValue(\"$identifier\")${getFallbackValuePart(component)}"

        return """DebugPanelItemViewData.Toggle(⇥
            |"$identifier",
            |"$label",
            |$initialValue
            |⇤)"""
    }

    fun createTextField(component: Component.TextField): String {
        val identifier = component.safeIdentifier
        val placeholder = component.safeDisplayName
        val initialValue = "repository.getCurrentTextFieldValue(\"$identifier\")${getFallbackValuePart(component)}"

        return """DebugPanelItemViewData.TextField(⇥
            |"$identifier",
            |"$placeholder",
            |$initialValue
            |⇤)"""
    }

    fun createLabel(component: Component.Label): String {
        val identifier = component.safeIdentifier
        val label = component.safeDisplayName
        val value = component.name

        return """DebugPanelItemViewData.Label(⇥
            |"$identifier",
            |"$label",
            |$value
            |⇤)"""
    }

    fun createPicker(component: Component.Picker): String {
        val identifier = component.safeIdentifier
        val label = component.safeDisplayName
        val initialValue = "repository.getCurrentPickerValue(\"$identifier\")${getFallbackValuePart(component)}"
        val items = component.name

        return """DebugPanelItemViewData.Picker(⇥
            |"$identifier",
            |"$label",
            |$initialValue,
            |$items
            |⇤)"""
    }

    fun createDatePicker(component: Component.DatePicker): String {
        val identifier = component.safeIdentifier
        val label = component.safeDisplayName
        val initialValue = "repository.getCurrentDatePickerValue(\"$identifier\")${getFallbackValuePart(component)}"

        return """DebugPanelItemViewData.DatePicker(⇥
            |"$identifier",
            |"$label",
            |$initialValue
            |⇤)"""
    }

    fun createPicker(component: Component.EnumPicker): String {
        val identifier = component.safeIdentifier
        val label = component.safeDisplayName
        val initialValue = "repository.getCurrentPickerValue(\"$identifier\")${getEnumFallbackValuePart(component)}"
        val items = """listOf(⇥
            |${component.values.joinToString(",\n") { "DebugPanelPickerItem(\"$it\", \"${it.lowercase().capitalize()}\")" }}
            |⇤)"""

        return """DebugPanelItemViewData.Picker(⇥
            |"$identifier",
            |"$label",
            |$initialValue,
            |$items
            |⇤)"""
    }

    fun createButton(component: Component.Button): String {
        val identifier = component.safeIdentifier
        val label = component.safeDisplayName
        val action = component.name

        return createButton(identifier, label, action)
    }

    fun createButton(identifier: String, label: String, action: String): String =
        """DebugPanelItemViewData.Button(⇥
            |"$identifier",
            |"$label",
            |$action
            |⇤)"""

    private fun getFallbackValuePart(component: Component): String =
        " ?: ${component.initialValueParamName}"
            .takeIf { component.requiresInitialValue }
            .orEmpty()

    private fun getEnumFallbackValuePart(component: Component): String =
        " ?: ${component.initialValueParamName}?.name"
            .takeIf { component.requiresInitialValue }
            .orEmpty()

    private val Component.initialValueParamName
        get() = "initial${name.capitalize()}"
}
