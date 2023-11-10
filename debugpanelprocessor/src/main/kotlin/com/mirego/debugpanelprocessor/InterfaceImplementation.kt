package com.mirego.debugpanelprocessor

import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.ParameterSpec
import com.squareup.kotlinpoet.TypeName
import com.squareup.kotlinpoet.TypeSpec

data class InterfaceImplementation(
    val int: TypeSpec,
    val impl: TypeSpec
) {
    data class Function(
        val name: String,
        val returnType: TypeName,
        val code: String,
        val params: Iterable<ParameterSpec> = emptyList()
    )

    companion object {
        fun create(
            interfaceClassName: ClassName,
            functions: Iterable<Function> = emptyList(),
            configureInterface: TypeSpec.Builder.() -> TypeSpec.Builder = { this },
            configureImplementation: TypeSpec.Builder.() -> TypeSpec.Builder = { this }
        ): InterfaceImplementation {
            val int = configureInterface(
                TypeSpec.interfaceBuilder(interfaceClassName.simpleName)
                    .addFunctions(
                        functions.map { function ->
                            FunSpec.builder(function.name)
                                .addModifiers(KModifier.ABSTRACT)
                                .addParameters(function.params)
                                .returns(function.returnType)
                                .build()
                        }
                    )
            ).build()

            val impl = configureImplementation(
                TypeSpec.classBuilder(interfaceClassName.simpleName + "Impl")
                    .addSuperinterface(interfaceClassName)
                    .addFunctions(
                        functions.map { function ->
                            FunSpec.builder(function.name)
                                .addModifiers(KModifier.OVERRIDE)
                                .addParameters(function.params)
                                .returns(function.returnType)
                                .addCode(function.code)
                                .build()
                        }
                    )
            ).build()

            return InterfaceImplementation(int, impl)
        }
    }
}
