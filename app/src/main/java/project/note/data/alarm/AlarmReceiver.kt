package project.note.data.alarm

import android.app.NotificationManager
import android.app.PendingIntent
import android.app.TaskStackBuilder
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import project.note.NoteApplication
import project.note.R
import project.note.domain.alarm.AlarmScheduler
import project.note.domain.repository.NoteRepository
import project.note.presentation.MainActivity
import javax.inject.Inject

@AndroidEntryPoint
class AlarmReceiver : BroadcastReceiver() {
    @Inject
    lateinit var repository: NoteRepository

    @Inject
    lateinit var alarmScheduler: AlarmScheduler

    override fun onReceive(context: Context, intent: Intent) {
        when (intent.action) {
            Intent.ACTION_BOOT_COMPLETED -> {
                println("Boot completed")

                rescheduleAllAlarms()
            }

            else -> {
                println("Notification received!!!")
                val message = intent.getStringExtra(MESSAGE_KEY) ?: return
                createNotification(message, context)
            }
        }
    }

    private fun createNotification(message: String, context: Context) {
        val pendingIntent = TaskStackBuilder.create(context).run {
            addNextIntentWithParentStack(Intent(context, MainActivity::class.java))
            getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT)
        }

        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val builder = NotificationCompat.Builder(context, NoteApplication.NOTE_CHANNEL_ID)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle(context.getString(R.string.note_notification_title))
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
        val notification = builder.build()

        notificationManager.notify(1, notification)
    }

    private fun rescheduleAllAlarms() {
        val pendingResult = goAsync()

        @OptIn(DelicateCoroutinesApi::class)
        GlobalScope.launch(Dispatchers.IO) {
            try {
                repository.getAllAlarms().forEach { alarmItem ->
                    alarmScheduler.schedule(alarmItem)
                }
            } finally {
                pendingResult.finish()
            }
        }
    }

    companion object {
        const val MESSAGE_KEY = "EXTRA_MESSAGE"
    }
}