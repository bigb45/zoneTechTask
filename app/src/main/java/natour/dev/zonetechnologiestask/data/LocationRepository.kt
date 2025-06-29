package natour.dev.zonetechnologiestask.data


import android.os.Build
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import natour.dev.zonetechnologiestask.core.util.Constants.DEVICE_ID_KEY
import natour.dev.zonetechnologiestask.core.util.Constants.PUSH_LOCATION_PORT
import natour.dev.zonetechnologiestask.core.util.SharedPreferencesUtil
import natour.dev.zonetechnologiestask.core.util.UniqueIdUtil
import natour.dev.zonetechnologiestask.domain.model.Device
import natour.dev.zonetechnologiestask.domain.model.LocationModel
import natour.dev.zonetechnologiestask.domain.usecase.CheckDeviceRegisteredUseCase
import natour.dev.zonetechnologiestask.network.TraccarApi
import natour.dev.zonetechnologiestask.network.TraccarApiClient
import natour.dev.zonetechnologiestask.ui.services.TAG
import okhttp3.Call
import okhttp3.Callback
import okhttp3.HttpUrl
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import java.io.IOException
import java.time.Instant
object LocationRepository {

    private val _locationList = MutableLiveData<List<LocationModel>>(emptyList())
    val locationList: LiveData<List<LocationModel>> get() = _locationList

    private val apiService: TraccarApi = TraccarApiClient.api
    private val checkDeviceRegisteredUseCase = CheckDeviceRegisteredUseCase(apiService)

    private var isDeviceRegistered = false

    suspend fun pushLocation(lat: Double, lon: Double) {
        val deviceId = ensureDeviceId()
        sendLocationToTraccar(deviceId, lat, lon)
        addNewLocation(lat, lon)
    }

    private suspend fun ensureDeviceId(): String {
        var deviceId = SharedPreferencesUtil.getValue(DEVICE_ID_KEY, "")
        if (deviceId.isBlank() || !isDeviceRegistered) {
            val exists = checkDeviceRegisteredUseCase(deviceId)
            if (!exists) {
                deviceId = UniqueIdUtil.generateRandomId()
                SharedPreferencesUtil.setValue(DEVICE_ID_KEY, deviceId)
                registerNewDevice(deviceId)
            }
            isDeviceRegistered = true
        }
        return deviceId
    }

    private suspend fun registerNewDevice(deviceId: String) {
        val requestBody = Device(uniqueId = deviceId, name = Build.MODEL)
        try {
            val res = apiService.registerDevice(requestBody)
            Log.d("Traccar", "registerNewDevice: success=${res.isSuccessful}")
        } catch (e: Exception) {
            Log.e("Traccar", "registerNewDevice error", e)
        }
    }

    private fun sendLocationToTraccar(deviceId: String, lat: Double, lon: Double) {
        val url = HttpUrl.Builder()
            .scheme("http")
            .host("192.168.1.9") // replace with your server IP or domain
            .port(PUSH_LOCATION_PORT)
            .addQueryParameter("id", deviceId)
            .addQueryParameter("lat", lat.toString())
            .addQueryParameter("lon", lon.toString())
            .addQueryParameter("timestamp", Instant.now().toString())
            .build()

        val request = Request.Builder().url(url).get().build()
        OkHttpClient().newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.e("OsmAnd", "Failed to send: ${e.message}")
            }

            override fun onResponse(call: Call, response: Response) {
                Log.d("OsmAnd", "Sent, response=${response.code()}")
            }
        })
    }

    private fun addNewLocation(lat: Double, lon: Double) {
        val newLocation = LocationModel(
            id = UniqueIdUtil.generateRandomId(),
            lat = lat.toString(),
            lon = lon.toString(),
            timestamp = Instant.now().toString()
        )
        val updatedList = _locationList.value.orEmpty().toMutableList().apply {
            add(newLocation)
        }
        _locationList.postValue(updatedList)
    }
}
