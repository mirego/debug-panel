package com.mirego.debugpanel.config

import kotlin.reflect.KClass

sealed interface DebugPanelComponentType {
    val dataType: KClass<*>
}
