package com.mirego.debugpanel.service

import android.content.Context
import androidx.preference.PreferenceManager
import androidx.startup.Initializer
import com.russhwolf.settings.ObservableSettings
import com.russhwolf.settings.SharedPreferencesSettings

private var appContext: Context? = null

actual val settings: ObservableSettings by lazy {
    SharedPreferencesSettings(PreferenceManager.getDefaultSharedPreferences(appContext!!))
}

internal class SettingsInitializer : Initializer<Context> {
    override fun create(context: Context): Context = context.applicationContext.also { appContext = it }
    override fun dependencies(): List<Class<out Initializer<*>>> = emptyList()
}
