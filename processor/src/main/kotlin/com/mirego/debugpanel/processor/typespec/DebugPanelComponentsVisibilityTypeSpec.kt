package com.mirego.debugpanel.processor.typespec

import com.mirego.debugpanel.processor.Component
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.PropertySpec
import com.squareup.kotlinpoet.TypeSpec

internal object DebugPanelComponentsVisibilityTypeSpec {
    fun create(name: String, components: Sequence<Component>) = TypeSpec.classBuilder(name)
        .addModifiers(KModifier.DATA)
        .primaryConstructor(
            FunSpec.constructorBuilder()
                .apply {
                    components.forEach {
                        addParameter(it.name, Boolean::class)
                    }
                }
                .build()
        )
        .apply {
            components.forEach {
                addProperty(
                    PropertySpec.builder(it.name, Boolean::class)
                        .initializer(it.name)
                        .build()
                )
            }
        }
        .build()
}