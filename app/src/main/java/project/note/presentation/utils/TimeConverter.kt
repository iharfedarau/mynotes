package project.note.presentation.utils

import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter


fun LocalDateTime.toLong(): Long {
    return toInstant(ZoneOffset.UTC).toEpochMilli()
}

fun Long.toLocalDateTime(): LocalDateTime {
    return LocalDateTime.ofInstant( Instant.ofEpochMilli(this), ZoneOffset.UTC)
}

fun Long.toLocalDate(): LocalDate {
    return Instant.ofEpochMilli(this)
        .atOffset(ZoneOffset.UTC)
        .toLocalDate()
}

fun  Long.toFormattedDateTime(): String {
    val dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")
    return toLocalDateTime().format(dateTimeFormatter)
}