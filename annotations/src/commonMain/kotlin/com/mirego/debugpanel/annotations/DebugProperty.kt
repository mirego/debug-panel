package com.mirego.debugpanel.annotations

@Target(AnnotationTarget.PROPERTY)
@Retention(AnnotationRetention.SOURCE)
annotation class DebugProperty(val name: String)
