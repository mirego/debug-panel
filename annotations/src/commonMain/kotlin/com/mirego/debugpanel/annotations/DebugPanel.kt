package com.mirego.debugpanel.annotations

@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.SOURCE)
annotation class DebugPanel(val prefix: String, val packageName: String, val includeResetButton: Boolean)
