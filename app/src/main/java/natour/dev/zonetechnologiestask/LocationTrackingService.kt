package natour.dev.zonetechnologiestask

import android.app.Notification
import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat

const val TAG = "MainActivity"

class LocationTrackingService: Service() {
    override fun onBind(intent: Intent?): IBinder? {
        Log.d(TAG, "onBind: Service is BOUND")
//        startService()
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        startForeground(1, startService())

        return START_STICKY
    }

    private fun startService(): Notification {

        val notification = NotificationCompat.Builder(this, "tracking_channel")
            .setContentTitle("Hi mom")
            .setContentText("This is the greatest notification of all time")
            .setSmallIcon(R.drawable.zone)
            .build()
        startForeground(1, notification)


        return notification
    }
}