package com.mirego.debugpanelprocessor

internal object DebugPanelItemViewDataFactory {
    fun createToggle(attribute: Attribute.Toggle): String {
        val identifier = attribute.safeIdentifier
        val label = attribute.safeDisplayName
        val initialValue = "repository.getCurrentToggleValue(\"${attribute.name}\", ${attribute.initialValueParamName})"

        return "DebugPanelItemViewData.Toggle(\"$identifier\", \"$label\", $initialValue)"
    }

    fun createTextField(attribute: Attribute.TextField): String {
        val identifier = attribute.safeIdentifier
        val placeholder = attribute.safeDisplayName
        val initialValue = "repository.getCurrentTextFieldValue(\"${attribute.name}\", ${attribute.initialValueParamName})"

        return "DebugPanelItemViewData.TextField(\"$identifier\", \"$placeholder\", $initialValue)"
    }

    fun createLabel(attribute: Attribute.Label): String {
        val identifier = attribute.safeIdentifier
        val label = attribute.safeDisplayName
        val value = attribute.name

        return "DebugPanelItemViewData.Label(\"$identifier\", \"$label\", $value)"
    }

    fun createPicker(attribute: Attribute.Picker): String {
        val identifier = attribute.safeIdentifier
        val label = attribute.safeDisplayName
        val initialValue = "repository.getCurrentPickerValue(\"${attribute.name}\") ?: ${attribute.initialValueParamName}"
        val items = attribute.name

        return "DebugPanelItemViewData.Picker(\"$identifier\", \"$label\", $initialValue, $items)"
    }

    fun createPicker(attribute: Attribute.EnumPicker): String {
        val identifier = attribute.safeIdentifier
        val label = attribute.safeDisplayName
        val initialValue = "repository.getCurrentPickerValue(\"${attribute.name}\") ?: ${attribute.initialValueParamName}?.name"
        val items = """
            listOf(
                ${attribute.values.joinToString(", ") { "DebugPanelPickerItem(\"$it\", \"${it.lowercase().capitalize()}\")" }}
            )
        """.trimIndent()

        return "DebugPanelItemViewData.Picker(\"$identifier\", \"$label\", $initialValue, $items)"
    }

    fun createButton(attribute: Attribute.Function): String {
        val identifier = attribute.safeIdentifier
        val label = attribute.safeDisplayName
        val action = attribute.name

        return "DebugPanelItemViewData.Button(\"$identifier\", \"$label\", $action)"
    }

    private val Attribute.safeIdentifier
        get() = identifier ?: name

    private val Attribute.safeDisplayName
        get() = displayName ?: name

    private val Attribute.initialValueParamName
        get() = "initial${name.capitalize()}"
}
