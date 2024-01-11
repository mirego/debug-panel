package com.mirego.debugpanel.processor

import com.google.devtools.ksp.symbol.KSAnnotation
import com.google.devtools.ksp.symbol.KSClassDeclaration

internal data class ResolvedConfiguration(
    val declaration: KSClassDeclaration,
    val annotation: KSAnnotation,
    val components: Sequence<Component>,
    val prefix: String,
    val packageName: String
)
