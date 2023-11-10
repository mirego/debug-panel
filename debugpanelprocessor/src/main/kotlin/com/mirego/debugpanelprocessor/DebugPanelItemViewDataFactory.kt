package com.mirego.debugpanelprocessor

internal object DebugPanelItemViewDataFactory {
    fun createToggle(attribute: Attribute.Toggle): String =
        "DebugPanelItemViewData.Toggle(\"${attribute.name}\", \"${attribute.displayName ?: attribute.name}\", repository.getCurrentToggleValue(\"${attribute.name}\", initial${attribute.name.capitalize()}))"

    fun createTextField(attribute: Attribute.TextField): String =
        "DebugPanelItemViewData.TextField(\"${attribute.name}\", \"${attribute.displayName ?: attribute.name}\", repository.getCurrentTextFieldValue(\"${attribute.name}\", initial${attribute.name.capitalize()}))"

    fun createLabel(attribute: Attribute.Label): String =
        "DebugPanelItemViewData.Label(\"${attribute.name}\", \"${attribute.name}\", ${attribute.name})"

    fun createPicker(attribute: Attribute.Picker): String =
        "DebugPanelItemViewData.Picker(\"${attribute.name}\", \"${attribute.displayName ?: attribute.name}\", repository.getCurrentPickerValue(\"${attribute.name}\"), ${attribute.name})"

    fun createPicker(attribute: Attribute.EnumPicker): String {
        val items = """
            listOf(
                ${attribute.values.joinToString(", ") { "DebugPanelPickerItem(\"$it\", \"${it.lowercase().capitalize()}\")" }}
            )
        """.trimIndent()
        return "DebugPanelItemViewData.Picker(\"${attribute.name}\", \"${attribute.displayName ?: attribute.name}\", repository.getCurrentPickerValue(\"${attribute.name}\"), $items)"
    }

    fun createButton(attribute: Attribute.Function): String =
        "DebugPanelItemViewData.Button(\"${attribute.name}\", \"${attribute.displayName ?: attribute.name}\", ${attribute.name})"
}
