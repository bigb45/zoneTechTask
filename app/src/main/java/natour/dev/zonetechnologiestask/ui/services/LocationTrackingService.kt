package natour.dev.zonetechnologiestask.ui.services

import android.Manifest.permission.ACCESS_COARSE_LOCATION
import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.app.Notification
import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.os.Looper
import android.util.Log
import androidx.annotation.RequiresPermission
import androidx.core.app.NotificationCompat
import androidx.core.content.ContentProviderCompat.requireContext
import com.google.android.gms.location.Granularity
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import natour.dev.zonetechnologiestask.CustomAlertDialog
import natour.dev.zonetechnologiestask.R
import natour.dev.zonetechnologiestask.core.util.SharedPreferencesUtil
import natour.dev.zonetechnologiestask.core.util.UniqueIdUtil
import natour.dev.zonetechnologiestask.data.LocationRepository
import natour.dev.zonetechnologiestask.domain.model.LocationModel
import java.time.Instant

const val TAG = "MainActivity"
class LocationTrackingService : Service() {

    private lateinit var locationCallback: LocationCallback
    private var isTrackingActive = false

    override fun onBind(intent: Intent?): IBinder? = null

    @RequiresPermission(allOf = [ACCESS_FINE_LOCATION, ACCESS_COARSE_LOCATION])
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        startForeground(1, createNotification())
        startLocationTracking()
        return START_STICKY
    }

    private fun createNotification(): Notification {
        return NotificationCompat.Builder(this, "tracking_channel")
            .setContentTitle("Tracking your location")
            .setContentText("Location updates are being sent to your local Traccar server.")
            .setSmallIcon(R.drawable.zone)
            .build()
    }

    @RequiresPermission(allOf = [ACCESS_FINE_LOCATION, ACCESS_COARSE_LOCATION])
    private fun startLocationTracking() {
        if (isTrackingActive) return

        val locationRequest = LocationRequest.Builder(500L)
            .setPriority(Priority.PRIORITY_HIGH_ACCURACY)
            .setGranularity(Granularity.GRANULARITY_FINE)
            .setMinUpdateDistanceMeters(10f)
            .setMinUpdateIntervalMillis(500L)
            .build()

        val client = LocationServices.getFusedLocationProviderClient(this)

        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                val lat = locationResult.lastLocation?.latitude ?: 0.0
                val lon = locationResult.lastLocation?.longitude ?: 0.0

                val newLocation = LocationModel(
                    id = UniqueIdUtil.generateRandomId(),
                    lat = lat.toString(),
                    lon = lon.toString(),
                    timestamp = Instant.now().toString()

                )
               GlobalScope.launch(Dispatchers.IO) {
                   LocationRepository.pushLocation(
                       lat = lat,
                       lon = lon
                   )
               }
            }
        }

        client.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper())
        isTrackingActive = true
        Log.d(TAG, "startLocationTracking: requested updates")
    }

    private fun stopLocationTracking() {
        if (!isTrackingActive) return
        val client = LocationServices.getFusedLocationProviderClient(this)
        client.removeLocationUpdates(locationCallback)
        isTrackingActive = false
        stopForeground(true)
        stopSelf()
        Log.d(TAG, "stopLocationTracking: stopped")
    }

    fun stopTracking() {
        stopLocationTracking()
    }
}
