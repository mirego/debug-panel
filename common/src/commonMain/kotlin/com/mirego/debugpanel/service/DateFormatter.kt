package com.mirego.debugpanel.service

interface DateFormatter {
    fun format(date: Long): String
}

internal expect fun createDateFormatter(): DateFormatter

internal val dateFormatter = createDateFormatter()
