package com.mirego.debugpanel.sample.repository

import com.mirego.debugpanel.service.settings
import kotlin.reflect.KProperty

object SampleRepositoryProperties {
    operator fun getValue(sampleRepositoryImpl: SampleRepositoryImpl, property: KProperty<*>): String = when (property.name) {
        "firstNameInput" -> settings.getString("firstNameInput", sampleRepositoryImpl.firstNameInputInternal)
            .takeIf { it.isNotEmpty() }
            ?: sampleRepositoryImpl.firstNameInputInternal
        else -> throw IllegalArgumentException("Unhandled property: " + property.name)
    }
}
