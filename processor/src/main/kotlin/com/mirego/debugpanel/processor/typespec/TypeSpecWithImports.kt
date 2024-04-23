package com.mirego.debugpanel.processor.typespec

import com.squareup.kotlinpoet.TypeSpec

internal data class TypeSpecWithImports(
    val typeSpec: TypeSpec,
    val imports: List<Import> = emptyList(),
)
