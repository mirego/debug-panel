package com.mirego.debugpanel.processor

import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSDeclarationContainer
import com.google.devtools.ksp.symbol.KSType
import kotlin.reflect.KClass

internal sealed interface Component {
    val identifier: String?
    val displayName: String?
    val name: String
    val persistedType: KClass<*>?
    val componentTypeName: String
    val requiresInitialValue: Boolean

    val safeIdentifier
        get() = identifier ?: name

    val safeDisplayName
        get() = displayName ?: name

    data class Label(
        override val identifier: String?,
        override val displayName: String?,
        override val name: String,
        override val requiresInitialValue: Boolean,
    ) : Component {
        override val persistedType = null
        override val componentTypeName = "Label"
    }

    data class TextField(
        override val identifier: String?,
        override val displayName: String?,
        override val name: String,
        override val requiresInitialValue: Boolean,
    ) : Component {
        override val persistedType = String::class
        override val componentTypeName = "TextField"
    }

    data class Picker(
        override val identifier: String?,
        override val displayName: String?,
        override val name: String,
        override val requiresInitialValue: Boolean,
    ) : Component {
        override val persistedType = String::class
        override val componentTypeName = "Picker"
    }

    data class DatePicker(
        override val identifier: String?,
        override val displayName: String?,
        override val name: String,
        override val requiresInitialValue: Boolean,
    ) : Component {
        override val persistedType = Long::class
        override val componentTypeName = "DatePicker"
    }

    data class EnumPicker(
        override val identifier: String?,
        override val displayName: String?,
        override val name: String,
        override val requiresInitialValue: Boolean,
        val type: KSType,
    ) : Component {
        val values: Sequence<String> = (type.declaration as KSDeclarationContainer)
            .declarations
            .mapNotNull { (it as? KSClassDeclaration)?.toString() }

        override val persistedType = String::class
        override val componentTypeName = "Picker"
    }

    data class Toggle(
        override val identifier: String?,
        override val displayName: String?,
        override val name: String,
        override val requiresInitialValue: Boolean,
    ) : Component {
        override val persistedType = Boolean::class
        override val componentTypeName = "Toggle"
    }

    data class Button(
        override val identifier: String?,
        override val displayName: String?,
        override val name: String,
        override val requiresInitialValue: Boolean,
    ) : Component {
        override val persistedType = null
        override val componentTypeName = "Button"
    }
}
