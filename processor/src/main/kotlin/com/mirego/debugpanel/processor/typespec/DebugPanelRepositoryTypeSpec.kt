package com.mirego.debugpanel.processor.typespec

import com.mirego.debugpanel.processor.Component
import com.mirego.debugpanel.processor.Consts
import com.mirego.debugpanel.processor.Consts.FLOW
import com.mirego.debugpanel.processor.capitalize
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.plusParameter
import com.squareup.kotlinpoet.UNIT
import com.squareup.kotlinpoet.asTypeName

internal object DebugPanelRepositoryTypeSpec {
    fun create(className: ClassName, components: Sequence<Component>) = InterfaceImplementation.create(
        interfaceClassName = className,
        functions = createFunctions(components),
        configureInterface = { addSuperinterface(ClassName(Consts.REPOSITORY_PACKAGE_NAME, Consts.REPOSITORY_NAME)) },
        configureImplementation = {
            addModifiers(KModifier.OPEN)
                .superclass(ClassName(Consts.REPOSITORY_PACKAGE_NAME, Consts.REPOSITORY_IMPL_NAME))
        }
    )

    private fun createFunctions(components: Sequence<Component>): Iterable<InterfaceImplementation.Function> {
        val componentGettersFlow = components.mapNotNull { component ->
            val returnType = component.persistedType?.asTypeName() ?: return@mapNotNull null
            val baseRepositoryFunctionName = "get${component.componentTypeName}Value"

            InterfaceImplementation.Function(
                name = getComponentValueFunctionName(component),
                returnType = FLOW.plusParameter(returnType.copy(nullable = true)),
                code = "return $baseRepositoryFunctionName(\"${component.safeIdentifier}\")"
            )
        }

        val componentGetters = components.mapNotNull { component ->
            val returnType = component.persistedType?.asTypeName() ?: return@mapNotNull null
            val baseRepositoryFunctionName = "getCurrent${component.componentTypeName}Value"

            InterfaceImplementation.Function(
                name = "getCurrent${component.name.capitalize()}",
                returnType = returnType.copy(nullable = true),
                code = "return $baseRepositoryFunctionName(\"${component.safeIdentifier}\")"
            )
        }

        val resetSettings = InterfaceImplementation.Function(
            name = "resetSettings",
            returnType = UNIT,
            code = components
                .filter { it.persistedType != null }
                .joinToString(",\n") { "\"${it.safeIdentifier}\"" }
                .let { keys ->
                    """
                    |removeKeys(⇥
                    |$keys
                    |⇤)
                    """.trimMargin()
                }
        )

        return (componentGettersFlow + componentGetters + resetSettings).asIterable()
    }

    fun getComponentValueFunctionName(component: Component): String =
        "get${component.name.capitalize()}"
}
