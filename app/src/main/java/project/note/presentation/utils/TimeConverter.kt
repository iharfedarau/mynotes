package project.note.presentation.utils

import project.note.domain.utils.toLocalDateTime
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter

fun Long.toLocalDate(): LocalDate {
    return Instant.ofEpochMilli(this)
        .atOffset(ZoneOffset.UTC)
        .toLocalDate()
}

fun Long.toFormattedDateTime(): String {
    val dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")
    return toLocalDateTime().format(dateTimeFormatter)
}