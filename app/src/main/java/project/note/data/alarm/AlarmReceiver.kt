package project.note.data.alarm

import android.app.NotificationManager
import android.app.PendingIntent
import android.app.TaskStackBuilder
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import project.note.NoteApplication
import project.note.R
import project.note.presentation.MainActivity

class AlarmReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        intent?.let { int ->
            val message = int.getStringExtra(MESSAGE_KEY) ?: return

            context?.let { ctx ->
                createNotification(message, ctx)
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
            .setContentTitle("Note Alarm")
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
        val notification = builder.build()

        notificationManager.notify(1, notification)
    }

    companion object {
        const val MESSAGE_KEY = "EXTRA_MESSAGE"
    }
}