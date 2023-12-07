package com.mirego.debugpanel.service

import com.russhwolf.settings.ObservableSettings
import com.russhwolf.settings.coroutines.FlowSettings
import com.russhwolf.settings.coroutines.toFlowSettings

object DebugPanelSettings {
    val observableSettings: ObservableSettings by lazy {
        settings
    }

    val flowSettings: FlowSettings by lazy {
        settings.toFlowSettings()
    }
}
