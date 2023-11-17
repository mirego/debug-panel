package com.mirego.debugpanelprocessor.typespec

import com.mirego.debugpanelprocessor.Attribute
import com.mirego.debugpanelprocessor.Consts
import com.mirego.debugpanelprocessor.Consts.FLOW
import com.mirego.debugpanelprocessor.ResolvedConfiguration
import com.mirego.debugpanelprocessor.capitalize
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.plusParameter
import com.squareup.kotlinpoet.UNIT
import com.squareup.kotlinpoet.asTypeName

internal object DebugPanelRepositoryTypeSpec {
    fun create(className: ClassName, attributes: Sequence<Attribute>) = InterfaceImplementation.create(
        interfaceClassName = className,
        functions = createFunctions(attributes),
        configureInterface = { addSuperinterface(ClassName(Consts.REPOSITORY_PACKAGE_NAME, Consts.REPOSITORY_NAME)) },
        configureImplementation = { superclass(ClassName(Consts.REPOSITORY_PACKAGE_NAME, Consts.REPOSITORY_IMPL_NAME)) }
    )

    private fun createFunctions(attributes: Sequence<Attribute>): Iterable<InterfaceImplementation.Function> {
        val attributeGetters = attributes.mapNotNull { attribute ->
            val returnType = attribute.persistedType?.asTypeName() ?: return@mapNotNull null
            val baseRepositoryFunctionName = "get${attribute.attributeTypeName}Value"

            InterfaceImplementation.Function(
                name = "get${attribute.name.capitalize()}",
                returnType = FLOW.plusParameter(returnType.copy(nullable = true)),
                code = "return $baseRepositoryFunctionName(\"${attribute.safeIdentifier}\")"
            )
        }

        val resetSettings = InterfaceImplementation.Function(
            name = "resetSettings",
            returnType = UNIT,
            code = attributes
                .filter { it.persistedType != null }
                .joinToString("\n") {
                    "settings.remove(\"${it.safeIdentifier}\")"
                }
        )

        return (attributeGetters + resetSettings).asIterable()
    }
}
