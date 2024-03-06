package com.codeskraps.core.domain.model

import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

abstract class Entry {
    private val formatter by lazy { DateTimeFormatter.ofPattern("dd/MM HH:mm:ss") }
    abstract fun time(): Long

    fun displayTime(): String {
        return runCatching {
            val instant = Instant.ofEpochMilli(time())
            val dateTime = LocalDateTime.ofInstant(instant, ZoneId.systemDefault())
            dateTime.format(formatter)
        }.getOrElse { "" }
    }
}