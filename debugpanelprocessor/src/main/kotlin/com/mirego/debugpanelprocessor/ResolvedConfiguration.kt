package com.mirego.debugpanelprocessor

import com.google.devtools.ksp.symbol.KSAnnotation
import com.google.devtools.ksp.symbol.KSClassDeclaration

internal data class ResolvedConfiguration(
    val declaration: KSClassDeclaration,
    val annotation: KSAnnotation,
    val attributes: Sequence<Attribute>,
    val prefix: String,
    val packageName: String,
    val includeResetButton: Boolean
)
