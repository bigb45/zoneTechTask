package natour.dev.zonetechnologiestask

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context

class ZoneTechnologiesTask: Application() {
    override fun onCreate() {
        super.onCreate()
        val channel = NotificationChannel("tracking_channel", "Traccar tracking service", NotificationManager.IMPORTANCE_HIGH)
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)

    }
}