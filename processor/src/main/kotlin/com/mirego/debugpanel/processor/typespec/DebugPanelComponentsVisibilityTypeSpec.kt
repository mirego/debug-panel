package com.mirego.debugpanel.processor.typespec

import com.mirego.debugpanel.processor.Component
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.ParameterSpec
import com.squareup.kotlinpoet.PropertySpec
import com.squareup.kotlinpoet.TypeSpec

internal object DebugPanelComponentsVisibilityTypeSpec {
    fun create(name: String, components: Sequence<Component>) = TypeSpec.classBuilder(name)
        .addModifiers(KModifier.DATA)
        .primaryConstructor(
            FunSpec.constructorBuilder()
                .apply {
                    components.forEach { component ->
                        addParameter(
                            ParameterSpec.builder(component.name, Boolean::class)
                                .defaultValue("true")
                                .build()
                        )
                    }
                }
                .build()
        )
        .apply {
            components.forEach { component ->
                addProperty(
                    PropertySpec.builder(component.name, Boolean::class)
                        .initializer(component.name)
                        .build()
                )
            }
        }
        .build()
}
