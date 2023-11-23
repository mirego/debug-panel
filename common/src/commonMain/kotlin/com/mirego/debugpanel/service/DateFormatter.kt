package com.mirego.debugpanel.service

interface DateFormatter {
    fun format(date: Long): String

    companion object {
        const val DEFAULT_DATE_FORMAT = "yyyy-MM-dd"
    }
}

internal expect fun createDateFormatter(): DateFormatter

internal val dateFormatter = createDateFormatter()
