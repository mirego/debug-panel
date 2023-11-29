package com.mirego.debugpanelprocessor

import com.google.devtools.ksp.symbol.KSPropertyDeclaration
import com.google.devtools.ksp.symbol.KSType

data class Property(
    val declaration: KSPropertyDeclaration,
    val component: Component,
    val name: String
) {
    sealed interface Component {
        data object Label : Component

        data object TextField : Component

        data object Picker : Component

        data object DatePicker : Component

        data object Toggle : Component

        data object Button : Component

        data class EnumPicker(
            val type: KSType
        ) : Component
    }
}
