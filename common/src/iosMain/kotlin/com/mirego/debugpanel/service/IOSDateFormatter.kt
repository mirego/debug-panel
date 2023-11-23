package com.mirego.debugpanel.service

import platform.Foundation.NSDate
import platform.Foundation.NSDateFormatter
import platform.Foundation.NSLocale

class IOSDateFormatter : DateFormatter {

    private val dateFormatter = NSDateFormatter().apply {
        locale = NSLocale("en_US")
        dateFormat = DateFormatter.DEFAULT_DATE_FORMAT
    }

    override fun format(date: Long): String = dateFormatter.stringFromDate(NSDate(date.toDouble()))
}

internal actual fun createDateFormatter(): DateFormatter = IOSDateFormatter()
