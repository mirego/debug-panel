package com.mirego.debugpanel

import com.russhwolf.settings.NSUserDefaultsSettings
import com.russhwolf.settings.ObservableSettings
import platform.Foundation.NSUserDefaults

actual val settings: ObservableSettings = NSUserDefaultsSettings(NSUserDefaults.standardUserDefaults)
