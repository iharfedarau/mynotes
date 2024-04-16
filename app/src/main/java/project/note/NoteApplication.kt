package project.note

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.graphics.Color
import android.os.Build
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class NoteApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        // If application is running on Oreo or later
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createAlarmChannel()
        }
    }

    private fun createAlarmChannel() {
        val channelName = "Note Alarm"
        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val channel =
            NotificationChannel(
                NOTE_CHANNEL_ID,
                channelName,
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                enableLights(true)
                lightColor = Color.RED
                enableVibration(true)
            }

        notificationManager.createNotificationChannel(channel)
    }

    companion object {
        const val NOTE_CHANNEL_ID = "note_channel_id"
    }
}