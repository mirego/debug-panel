package com.mirego.debugpanelprocessor

import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSDeclarationContainer
import com.google.devtools.ksp.symbol.KSType
import kotlin.reflect.KClass

internal sealed interface Attribute {
    val identifier: String?
    val displayName: String?
    val name: String
    val persistedType: KClass<*>?
    val attributeTypeName: String

    val safeIdentifier
        get() = identifier ?: name

    val safeDisplayName
        get() = displayName ?: name

    data class Label(
        override val identifier: String?,
        override val displayName: String?,
        override val name: String
    ) : Attribute {
        override val persistedType = null
        override val attributeTypeName = "Label"
    }

    data class TextField(
        override val identifier: String?,
        override val displayName: String?,
        override val name: String
    ) : Attribute {
        override val persistedType = String::class
        override val attributeTypeName = "TextField"
    }

    data class Picker(
        override val identifier: String?,
        override val displayName: String?,
        override val name: String
    ) : Attribute {
        override val persistedType = String::class
        override val attributeTypeName = "Picker"
    }

    data class DatePicker(
        override val identifier: String?,
        override val displayName: String?,
        override val name: String
    ) : Attribute {
        override val persistedType = Long::class
        override val attributeTypeName = "DatePicker"
    }

    data class EnumPicker(
        override val identifier: String?,
        override val displayName: String?,
        override val name: String,
        val type: KSType
    ) : Attribute {
        val values: Sequence<String> = (type.declaration as KSDeclarationContainer)
            .declarations
            .mapNotNull { (it as? KSClassDeclaration)?.toString() }

        override val persistedType = String::class
        override val attributeTypeName = "Picker"
    }

    data class Toggle(
        override val identifier: String?,
        override val displayName: String?,
        override val name: String
    ) : Attribute {
        override val persistedType = Boolean::class
        override val attributeTypeName = "Toggle"
    }

    data class Function(
        override val identifier: String?,
        override val displayName: String?,
        override val name: String
    ) : Attribute {
        override val persistedType = null
        override val attributeTypeName = "Function"
    }
}
