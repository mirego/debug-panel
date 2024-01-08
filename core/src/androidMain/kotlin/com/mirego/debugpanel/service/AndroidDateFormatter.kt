package com.mirego.debugpanel.service

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

class AndroidDateFormatter : DateFormatter {

    private val dateFormat = SimpleDateFormat(DateFormatter.DEFAULT_DATE_FORMAT, Locale.getDefault()).apply {
        timeZone = TimeZone.getTimeZone("UTC")
    }

    override fun format(date: Long): String = dateFormat.format(Date(date))
}

internal actual fun createDateFormatter(): DateFormatter = AndroidDateFormatter()
