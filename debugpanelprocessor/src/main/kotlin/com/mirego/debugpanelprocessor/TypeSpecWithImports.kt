package com.mirego.debugpanelprocessor

import com.squareup.kotlinpoet.TypeSpec

internal data class TypeSpecWithImports(
    val typeSpec: TypeSpec,
    val imports: List<Import>
)
