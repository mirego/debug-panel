package com.mirego.debugpanel.annotations

@Target(AnnotationTarget.FIELD)
@Retention(AnnotationRetention.SOURCE)
annotation class Identifier(val value: String)
