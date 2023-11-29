package com.mirego.debugpanelprocessor.typespec

import com.google.devtools.ksp.symbol.ClassKind
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSType
import com.mirego.debugpanelprocessor.Consts
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import com.squareup.kotlinpoet.STRING
import com.squareup.kotlinpoet.TypeSpec
import com.squareup.kotlinpoet.asClassName
import com.squareup.kotlinpoet.ksp.toClassName
import com.squareup.kotlinpoet.ksp.toTypeName
import kotlin.reflect.KProperty

internal object DebugPanelObservablePropertyTypeSpec {
    fun create(typeSpecName: String, parent: KSClassDeclaration, returnType: KSType, propertyName: String, name: String): TypeSpecWithImports {
        val argument = returnType.arguments.first()
        val argumentName = argument.toTypeName() as ClassName

        val code = when {
            argumentName == STRING -> """
                |return DebugPanelSettings.flowSettings.getStringOrNullFlow("$propertyName")
                |⇥⇥⇥.flatMapLatest { settingsValue ->
                |⇥settingsValue⇥
                |?.takeIf { it.isNotEmpty() }
                |?.let { flowOf(it) }
                |?: parent.$name
                |⇤⇤}
            """.trimMargin()
            (argument.type?.resolve()?.declaration as? KSClassDeclaration)?.classKind == ClassKind.ENUM_CLASS -> """
                |return DebugPanelSettings.flowSettings.getStringOrNullFlow("$propertyName")
                |⇥⇥⇥.flatMapLatest { settingsValue ->
                |⇥settingsValue⇥
                |?.takeIf { it.isNotEmpty() }
                |?.let { flowOf(${argumentName.simpleName}.valueOf(it)) }
                |?: parent.$name
                |⇤⇤}
            """.trimMargin()
            else -> """
                |return DebugPanelSettings.flowSettings.get${argumentName.simpleName}OrNullFlow("$propertyName")
                |⇥⇥⇥.flatMapLatest { settingsValue ->
                |⇥settingsValue⇥
                |?.let { flowOf(it) }
                |?: parent.$name
                |⇤⇤}
            """.trimMargin()
        }

        return TypeSpecWithImports(
            TypeSpec.objectBuilder(typeSpecName)
                .addFunction(
                    FunSpec.builder("getValue")
                        .addModifiers(KModifier.OPERATOR)
                        .addParameter("parent", parent.toClassName())
                        .addParameter("property", KProperty::class.asClassName().parameterizedBy(Consts.WILDCARD))
                        .addCode(code)
                        .returns(returnType.toTypeName())
                        .build()
                )
                .build(),
            listOf(
                Import(Consts.SERVICE_PACKAGE_NAME, "DebugPanelSettings"),
                Import("kotlinx.coroutines.flow", "flatMapLatest"),
                Import("kotlinx.coroutines.flow", "flowOf")
            )
        )
    }
}
