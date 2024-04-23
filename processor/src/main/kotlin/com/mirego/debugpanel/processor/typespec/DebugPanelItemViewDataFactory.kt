package com.mirego.debugpanel.processor.typespec

import com.mirego.debugpanel.processor.Component
import com.mirego.debugpanel.processor.capitalize

internal object DebugPanelItemViewDataFactory {
    private const val ITEM_VIEW_DATA_CLASS_NAME = "DebugPanelItemViewData"

    fun createToggle(component: Component.Toggle): String {
        val identifier = component.safeIdentifier
        val label = component.safeDisplayName
        val initialValue = "repository.getCurrentToggleValue(\"$identifier\")${getFallbackValuePart(component)}"
        val isDirty = component.isDirtyParam

        return """$ITEM_VIEW_DATA_CLASS_NAME.Toggle(⇥
            |"$identifier",
            |"$label",
            |$initialValue,
            |$isDirty
            |⇤)"""
    }

    fun createTextField(component: Component.TextField): String {
        val identifier = component.safeIdentifier
        val placeholder = component.safeDisplayName
        val initialValue = "repository.getCurrentTextFieldValue(\"$identifier\")${getFallbackValuePart(component)}"
        val isDirty = component.isDirtyParam

        return """$ITEM_VIEW_DATA_CLASS_NAME.TextField(⇥
            |"$identifier",
            |"$placeholder",
            |$initialValue,
            |$isDirty
            |⇤)"""
    }

    fun createLabel(component: Component.Label): String {
        val identifier = component.safeIdentifier
        val label = component.safeDisplayName
        val value = component.name

        return """$ITEM_VIEW_DATA_CLASS_NAME.Label(⇥
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
        val isDirty = component.isDirtyParam

        return """$ITEM_VIEW_DATA_CLASS_NAME.Picker(⇥
            |"$identifier",
            |"$label",
            |$initialValue,
            |$items,
            |$isDirty
            |⇤)"""
    }

    fun createDatePicker(component: Component.DatePicker): String {
        val identifier = component.safeIdentifier
        val label = component.safeDisplayName
        val initialValue = "repository.getCurrentDatePickerValue(\"$identifier\")${getFallbackValuePart(component)}"
        val isDirty = component.isDirtyParam

        return """$ITEM_VIEW_DATA_CLASS_NAME.DatePicker(⇥
            |"$identifier",
            |"$label",
            |$initialValue,
            |$isDirty
            |⇤)"""
    }

    fun createPicker(component: Component.EnumPicker): String {
        val identifier = component.safeIdentifier
        val label = component.safeDisplayName
        val initialValue = "repository.getCurrentPickerValue(\"$identifier\")${getEnumFallbackValuePart(component)}"
        val items = """listOf(⇥
            |${component.values.joinToString(",\n") { "DebugPanelPickerItem(\"$it\", \"${it.lowercase().capitalize()}\")" }}
            |⇤)"""
        val isDirty = component.isDirtyParam

        return """$ITEM_VIEW_DATA_CLASS_NAME.Picker(⇥
            |"$identifier",
            |"$label",
            |$initialValue,
            |$items,
            |$isDirty
            |⇤)"""
    }

    fun createButton(component: Component.Button): String {
        val identifier = component.safeIdentifier
        val label = component.safeDisplayName
        val action = component.name

        return createButton(identifier, label, action)
    }

    private fun createButton(
        identifier: String,
        label: String,
        action: String,
    ): String =
        """$ITEM_VIEW_DATA_CLASS_NAME.Button(⇥
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

    private val Component.isDirtyParam: String
        get() {
            val componentValueFunctionName = DebugPanelRepositoryTypeSpec.getComponentValueFunctionName(this)
            return "repository.$componentValueFunctionName().map { it != null }"
        }
}
