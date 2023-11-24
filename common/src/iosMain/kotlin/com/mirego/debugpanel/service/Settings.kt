package com.mirego.debugpanel.service

import com.russhwolf.settings.NSUserDefaultsSettings
import com.russhwolf.settings.ObservableSettings
import platform.Foundation.NSUserDefaults

actual val settings: ObservableSettings = NSUserDefaultsSettings(NSUserDefaults.standardUserDefaults)
