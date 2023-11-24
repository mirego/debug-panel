package com.mirego.debugpanel.sample.repository

import com.mirego.debugpanel.service.DebugPanelSettings
import kotlin.reflect.KProperty

object SampleRepositoryFirstNameInputDelegate {
    operator fun getValue(sampleRepositoryImpl: SampleRepositoryImpl, property: KProperty<*>): String =
        DebugPanelSettings.observableSettings.getString("firstNameInput", sampleRepositoryImpl.firstNameInputInternal)
            .takeIf { it.isNotEmpty() }
            ?: sampleRepositoryImpl.firstNameInputInternal
}
