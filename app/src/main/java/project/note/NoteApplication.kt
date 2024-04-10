package project.note

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.graphics.Color
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class NoteApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        val channelId = "note_channel_id"
        val channelName = "Note Alarm"
        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val channel =
            NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_HIGH)

        channel.description = "My channel description";
        channel.enableLights(true);
        channel.lightColor = Color.RED;
        channel.enableVibration(true);

        notificationManager.createNotificationChannel(channel)
    }
}