package com.mirego.debugpanel.processor

import com.google.devtools.ksp.symbol.ClassKind
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSPropertyDeclaration
import com.google.devtools.ksp.symbol.KSType
import com.mirego.debugpanel.annotations.DebugProperty
import com.mirego.debugpanel.annotations.DisplayName
import com.mirego.debugpanel.annotations.Identifier
import com.squareup.kotlinpoet.BOOLEAN
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.STRING
import com.squareup.kotlinpoet.ksp.toClassName

internal object ComponentFactory {
    private sealed interface Type {
        data object Label : Type

        data object TextField : Type

        data object Picker : Type

        data object DatePicker : Type

        data object Toggle : Type

        data object Button : Type

        data class EnumPicker(
            val enumType: KSType
        ) : Type
    }

    fun createAllComponents(configDeclaration: KSClassDeclaration, debugPropertyDeclarations: Sequence<KSPropertyDeclaration>): Sequence<Component> {
        val configProperties = configDeclaration.getAllProperties()
            .mapNotNull {
                val propertyName = it.simpleName.getShortName()
                val type = it.type.resolve()
                val className = type.toClassName()
                val propertyType: Type = when {
                    className == TOGGLE_CLASS_NAME -> Type.Toggle
                    className == TEXT_FIELD_CLASS_NAME -> Type.TextField
                    className == LABEL_CLASS_NAME -> Type.Label
                    className == PICKER_CLASS_NAME -> Type.Picker
                    className == DATE_PICKER_CLASS_NAME -> Type.DatePicker
                    (type.declaration as? KSClassDeclaration)?.classKind == ClassKind.ENUM_CLASS -> Type.EnumPicker(type)
                    className == BUTTON_CLASS_NAME -> Type.Button
                    else -> return@mapNotNull null
                }

                it.toComponent(type = propertyType, name = propertyName, isDebugProperty = false)
            }

        val debugProperties = debugPropertyDeclarations.map {
            val propertyName = it.findAnnotation(DebugProperty::class)!!.findArgument("name") as String
            val type = it.type.resolve().let { type ->
                if (type.declaration.simpleName.getShortName() == Consts.FLOW.simpleName) {
                    type.arguments.first().type!!.resolve()
                } else {
                    type
                }
            }
            val propertyType = when {
                type.toClassName() == STRING -> Type.TextField
                type.toClassName() == BOOLEAN -> Type.Toggle
                (type.declaration as? KSClassDeclaration)?.classKind == ClassKind.ENUM_CLASS -> Type.EnumPicker(type)
                else -> throw IllegalArgumentException("Debug properties can only be used on String, Boolean and Enum types")
            }

            it.toComponent(type = propertyType, name = propertyName, isDebugProperty = true)
        }

        return configProperties + debugProperties
    }

    private fun KSPropertyDeclaration.toComponent(type: Type, name: String, isDebugProperty: Boolean): Component {
        val identifier = findAnnotation(Identifier::class)?.arguments?.first()?.value as String?
        val displayName = findAnnotation(DisplayName::class)?.arguments?.first()?.value as String?
        val requiresInitialValue = !isDebugProperty

        return when (type) {
            Type.DatePicker -> Component.DatePicker(identifier, displayName, name, requiresInitialValue)
            is Type.EnumPicker -> Component.EnumPicker(identifier, displayName, name, requiresInitialValue, type.enumType)
            Type.Button -> Component.Button(identifier, displayName, name, requiresInitialValue)
            Type.Label -> Component.Label(identifier, displayName, name, requiresInitialValue)
            Type.Picker -> Component.Picker(identifier, displayName, name, requiresInitialValue)
            Type.TextField -> Component.TextField(identifier, displayName, name, requiresInitialValue)
            Type.Toggle -> Component.Toggle(identifier, displayName, name, requiresInitialValue)
        }
    }

    private val LABEL_CLASS_NAME = ClassName(Consts.CONFIG_PACKAGE_NAME, "DebugPanelLabel")
    private val PICKER_CLASS_NAME = ClassName(Consts.CONFIG_PACKAGE_NAME, "DebugPanelPicker")
    private val DATE_PICKER_CLASS_NAME = ClassName(Consts.CONFIG_PACKAGE_NAME, "DebugPanelDatePicker")
    private val BUTTON_CLASS_NAME = ClassName(Consts.CONFIG_PACKAGE_NAME, "DebugPanelButton")
    private val TOGGLE_CLASS_NAME = ClassName(Consts.CONFIG_PACKAGE_NAME, "DebugPanelToggle")
    private val TEXT_FIELD_CLASS_NAME = ClassName(Consts.CONFIG_PACKAGE_NAME, "DebugPanelTextField")
}
