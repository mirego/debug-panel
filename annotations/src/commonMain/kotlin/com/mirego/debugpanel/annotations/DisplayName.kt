package com.mirego.debugpanel.annotations

@Target(AnnotationTarget.FIELD)
@Retention(AnnotationRetention.SOURCE)
annotation class DisplayName(val value: String)
