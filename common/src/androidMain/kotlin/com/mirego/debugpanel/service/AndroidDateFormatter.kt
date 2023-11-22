package com.mirego.debugpanel.service

import android.icu.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class AndroidDateFormatter : DateFormatter {

    private val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.US)

    override fun format(date: Long): String = dateFormat.format(Date(date))
}

internal actual fun createDateFormatter(): DateFormatter = AndroidDateFormatter()
