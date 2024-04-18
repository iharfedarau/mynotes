package project.note.domain.utils

import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZoneOffset

fun LocalDateTime.toLong(): Long {
    return toInstant(ZoneOffset.UTC).toEpochMilli()
}

fun LocalDateTime.systemZoneMs(): Long {
    return atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
}

fun Long.toLocalDateTime(): LocalDateTime {
    return LocalDateTime.ofInstant( Instant.ofEpochMilli(this), ZoneOffset.UTC)
}

fun currentUTCTime(): LocalDateTime {
    return LocalDateTime.now()
}

fun currentSystemTime(): LocalDateTime {
    return LocalDateTime.now(ZoneId.systemDefault())
}