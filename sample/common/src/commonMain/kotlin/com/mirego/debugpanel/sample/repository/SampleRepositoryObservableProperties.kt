package com.mirego.debugpanel.sample.repository

import com.mirego.debugpanel.service.settings
import com.russhwolf.settings.coroutines.toFlowSettings
import kotlin.reflect.KProperty
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf

object SampleRepositoryObservableProperties {
    private val flowSettings = settings.toFlowSettings()

    operator fun getValue(sampleRepositoryImpl: SampleRepositoryImpl, property: KProperty<*>): Flow<String> = when (property.name) {
        "lastNameInput" -> flowSettings.getStringOrNullFlow("lastNameInput")
            .flatMapLatest { settingsValue ->
                settingsValue
                    ?.takeIf { it.isNotEmpty() }
                    ?.let { flowOf(it) }
                    ?: sampleRepositoryImpl.lastNameInputInternal
            }
        else -> throw IllegalArgumentException("Unhandled property: " + property.name)
    }
}
