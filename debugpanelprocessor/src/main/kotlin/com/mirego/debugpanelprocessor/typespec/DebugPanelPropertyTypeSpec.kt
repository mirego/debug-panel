package com.mirego.debugpanelprocessor.typespec

import com.google.devtools.ksp.symbol.ClassKind
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSType
import com.mirego.debugpanelprocessor.Consts
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import com.squareup.kotlinpoet.STRING
import com.squareup.kotlinpoet.TypeSpec
import com.squareup.kotlinpoet.asClassName
import com.squareup.kotlinpoet.ksp.toClassName
import com.squareup.kotlinpoet.ksp.toTypeName
import kotlin.reflect.KProperty

internal object DebugPanelPropertyTypeSpec {
    fun create(typeSpecName: String, parentDeclaration: KSClassDeclaration, returnType: KSType, identifier: String, name: String): TypeSpecWithImports {
        val code = when {
            returnType.toTypeName() == STRING -> """
                |return DebugPanelSettings.observableSettings.getString(
                |⇥⇥⇥"$identifier",
                |parent.$name
                |⇤)
                |.takeIf { it.isNotEmpty() }
                |?: parent.$name
            """.trimMargin()
            (returnType.declaration as? KSClassDeclaration)?.classKind == ClassKind.ENUM_CLASS -> """
                |return DebugPanelSettings.observableSettings.getStringOrNull(
                |⇥⇥⇥"$identifier"
                |⇤)
                |?.let { ${returnType.toClassName().simpleName}.valueOf(it) } 
                |?: parent.$name
            """.trimMargin()
            else -> """
                |return DebugPanelSettings.observableSettings.get$returnType(
                |⇥⇥⇥"$identifier",
                |parent.$name
                |⇤)
            """.trimMargin()
        }

        return TypeSpecWithImports(
            TypeSpec.objectBuilder(typeSpecName)
                .addFunction(
                    FunSpec.builder("getValue")
                        .addModifiers(KModifier.OPERATOR)
                        .addParameter("parent", parentDeclaration.toClassName())
                        .addParameter("property", KProperty::class.asClassName().parameterizedBy(Consts.WILDCARD))
                        .addCode(code)
                        .returns(returnType.toTypeName())
                        .build()
                )
                .build(),
            listOf(Import(Consts.SERVICE_PACKAGE_NAME, "DebugPanelSettings"))
        )
    }
}
