package com.mirego.debugpanelprocessor

import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSDeclarationContainer
import com.google.devtools.ksp.symbol.KSType

internal sealed interface Attribute {
    val displayName: String?
    val name: String
    val type: KSType

    data class Label(
        override val displayName: String?,
        override val name: String,
        override val type: KSType
    ) : Attribute

    data class TextField(
        override val displayName: String?,
        override val name: String,
        override val type: KSType
    ) : Attribute

    data class Picker(
        override val displayName: String?,
        override val name: String,
        override val type: KSType
    ) : Attribute

    data class EnumPicker(
        override val displayName: String?,
        override val name: String,
        override val type: KSType
    ) : Attribute {
        val values: Sequence<String> = (type.declaration as KSDeclarationContainer)
            .declarations
            .mapNotNull { (it as? KSClassDeclaration)?.toString() }
    }

    data class Toggle(
        override val displayName: String?,
        override val name: String,
        override val type: KSType
    ) : Attribute

    data class Function(
        override val displayName: String?,
        override val name: String,
        override val type: KSType
    ) : Attribute
}
