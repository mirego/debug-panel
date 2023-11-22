package com.mirego.debugpanelprocessor.typespec

import com.mirego.debugpanelprocessor.Attribute
import com.mirego.debugpanelprocessor.capitalize

internal object DebugPanelItemViewDataFactory {
    fun createToggle(attribute: Attribute.Toggle): String {
        val identifier = attribute.safeIdentifier
        val label = attribute.safeDisplayName
        val initialValue = "repository.getCurrentToggleValue(\"$identifier\", ${attribute.initialValueParamName})"

        return "DebugPanelItemViewData.Toggle(\n\"$identifier\",\n\"$label\",\n$initialValue\n)"
    }

    fun createTextField(attribute: Attribute.TextField): String {
        val identifier = attribute.safeIdentifier
        val placeholder = attribute.safeDisplayName
        val initialValue = "repository.getCurrentTextFieldValue(\"$identifier\", ${attribute.initialValueParamName})"

        return "DebugPanelItemViewData.TextField(\n\"$identifier\",\n\"$placeholder\",\n$initialValue\n)"
    }

    fun createLabel(attribute: Attribute.Label): String {
        val identifier = attribute.safeIdentifier
        val label = attribute.safeDisplayName
        val value = attribute.name

        return "DebugPanelItemViewData.Label(\n\"$identifier\",\n\"$label\",\n$value\n)"
    }

    fun createPicker(attribute: Attribute.Picker): String {
        val identifier = attribute.safeIdentifier
        val label = attribute.safeDisplayName
        val initialValue = "repository.getCurrentPickerValue(\"$identifier\") ?: ${attribute.initialValueParamName}"
        val items = attribute.name

        return "DebugPanelItemViewData.Picker(\n\"$identifier\",\n\"$label\",\n$initialValue,\n$items\n)"
    }

    fun createDatePicker(attribute: Attribute.DatePicker): String {
        val identifier = attribute.safeIdentifier
        val label = attribute.safeDisplayName
        val initialValue = "repository.getCurrentDatePickerValue(\"$identifier\") ?: ${attribute.initialValueParamName}"

        return "DebugPanelItemViewData.DatePicker(\n\"$identifier\",\n\"$label\",\n$initialValue\n)"
    }

    fun createPicker(attribute: Attribute.EnumPicker): String {
        val identifier = attribute.safeIdentifier
        val label = attribute.safeDisplayName
        val initialValue = "repository.getCurrentPickerValue(\"$identifier\") ?: ${attribute.initialValueParamName}?.name"
        val items = """
            listOf(
                ${attribute.values.joinToString(",\n") { "DebugPanelPickerItem(\"$it\", \"${it.lowercase().capitalize()}\")" }}
            )
        """.trimIndent()

        return "DebugPanelItemViewData.Picker(\n\"$identifier\",\n\"$label\",\n$initialValue,\n$items\n)"
    }

    fun createButton(attribute: Attribute.Function): String {
        val identifier = attribute.safeIdentifier
        val label = attribute.safeDisplayName
        val action = attribute.name

        return createButton(identifier, label, action)
    }

    fun createButton(identifier: String, label: String, action: String): String =
        "DebugPanelItemViewData.Button(\n\"$identifier\",\n\"$label\",\n$action\n)"

    private val Attribute.initialValueParamName
        get() = "initial${name.capitalize()}"
}
