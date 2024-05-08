package project.note.data.alarm

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.util.Log
import project.note.domain.alarm.AlarmScheduler
import project.note.domain.alarm.AlarmItem
import java.time.OffsetDateTime
import java.time.ZoneOffset

class AlarmSchedulerImpl(private val context: Context) : AlarmScheduler {
    private val alarmManager = context.getSystemService(AlarmManager::class.java)

    override fun schedule(alarmItem: AlarmItem) {
        val currentUTCTime = OffsetDateTime.now(ZoneOffset.UTC).toEpochSecond() * 1000

        if (currentUTCTime < alarmItem.date) {
            val intent = Intent(context, AlarmReceiver::class.java).apply {
                putExtra(AlarmReceiver.MESSAGE_KEY, alarmItem.message)
            }

            alarmManager.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                alarmItem.date,
                PendingIntent.getBroadcast(
                    context,
                    alarmItem.hashCode(),
                    intent,
                    PendingIntent.FLAG_UPDATE_CURRENT
                )
            )
        } else {
            Log.w("NoteApp", "Scheduled time is lower then the current time!")
        }
    }

    override fun cancel(alarmItem: AlarmItem) {
        alarmManager.cancel(
            PendingIntent.getBroadcast(
                context,
                alarmItem.hashCode(),
                Intent(context, AlarmReceiver::class.java),
                PendingIntent.FLAG_UPDATE_CURRENT
            )
        )
    }
}