package com.mirego.debugpanel.sample.repository

import com.mirego.debugpanel.service.DebugPanelSettings
import kotlin.reflect.KProperty
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf

object SampleRepositoryLastNameInputDelegate {
    operator fun getValue(sampleRepositoryImpl: SampleRepositoryImpl, property: KProperty<*>): Flow<String> =
        DebugPanelSettings.flowSettings.getStringOrNullFlow("lastNameInput")
            .flatMapLatest { settingsValue ->
                settingsValue
                    ?.takeIf { it.isNotEmpty() }
                    ?.let { flowOf(it) }
                    ?: sampleRepositoryImpl.lastNameInputInternal
            }
}
