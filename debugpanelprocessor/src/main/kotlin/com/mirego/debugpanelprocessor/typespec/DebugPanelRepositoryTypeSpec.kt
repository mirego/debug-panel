package com.mirego.debugpanelprocessor.typespec

import com.mirego.debugpanelprocessor.Attribute
import com.mirego.debugpanelprocessor.Consts
import com.mirego.debugpanelprocessor.Consts.FLOW
import com.mirego.debugpanelprocessor.capitalize
import com.squareup.kotlinpoet.BOOLEAN
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.plusParameter
import com.squareup.kotlinpoet.STRING

internal object DebugPanelRepositoryTypeSpec {
    fun create(className: ClassName, attributes: Sequence<Attribute>) = InterfaceImplementation.create(
        interfaceClassName = className,
        functions = createFunctions(attributes),
        configureInterface = { addSuperinterface(ClassName(Consts.REPOSITORY_PACKAGE_NAME, Consts.REPOSITORY_NAME)) },
        configureImplementation = { superclass(ClassName(Consts.REPOSITORY_PACKAGE_NAME, Consts.REPOSITORY_IMPL_NAME)) }
    )

    private fun createFunctions(attributes: Sequence<Attribute>): Iterable<InterfaceImplementation.Function> = attributes.mapNotNull { attribute ->
        val returnType = when (attribute) {
            is Attribute.Function, is Attribute.Label -> return@mapNotNull null
            is Attribute.Picker, is Attribute.EnumPicker, is Attribute.TextField -> STRING
            is Attribute.Toggle -> BOOLEAN
        }

        @Suppress("KotlinConstantConditions")
        val baseRepositoryFunctionName = when (attribute) {
            is Attribute.Toggle -> "getToggleValue"
            is Attribute.TextField -> "getTextFieldValue"
            is Attribute.Picker, is Attribute.EnumPicker -> "getPickerValue"
            is Attribute.Function, is Attribute.Label -> return@mapNotNull null
        }

        InterfaceImplementation.Function(
            name = "get${attribute.name.capitalize()}",
            returnType = FLOW.plusParameter(returnType.copy(nullable = true)),
            code = "return $baseRepositoryFunctionName(\"${attribute.name}\")"
        )
    }.asIterable()
}
