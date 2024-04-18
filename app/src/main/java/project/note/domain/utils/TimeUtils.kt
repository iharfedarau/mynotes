package project.note.domain.utils

import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneOffset

fun LocalDateTime.toLong(): Long {
    return toInstant(ZoneOffset.UTC).toEpochMilli()
}

fun Long.toLocalDateTime(): LocalDateTime {
    return LocalDateTime.ofInstant( Instant.ofEpochMilli(this), ZoneOffset.UTC)
}