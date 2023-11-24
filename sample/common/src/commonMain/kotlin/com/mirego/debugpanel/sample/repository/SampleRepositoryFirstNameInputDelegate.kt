package com.mirego.debugpanel.sample.repository

import com.mirego.debugpanel.service.DebugPanelSettings
import kotlin.reflect.KProperty

object SampleRepositoryFirstNameInputDelegate {
    operator fun getValue(parent: SampleRepositoryImpl, property: KProperty<*>): String =
        DebugPanelSettings.observableSettings.getString("firstNameInput", parent.firstNameInputInternal)
            .takeIf { it.isNotEmpty() }
            ?: parent.firstNameInputInternal
}
