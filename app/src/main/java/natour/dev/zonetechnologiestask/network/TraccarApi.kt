package natour.dev.zonetechnologiestask.network

import android.util.Log
import natour.dev.zonetechnologiestask.core.util.Constants.DEVICE_ENDPOINT
import natour.dev.zonetechnologiestask.domain.model.Device
import natour.dev.zonetechnologiestask.ui.services.TAG
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface TraccarApi {
    @GET("devices/")
    suspend fun getDeviceById(deviceId: String) {
        Log.d(TAG, "getDeviceById: getting device by id")
    }

    @POST(DEVICE_ENDPOINT)
    suspend fun registerDevice(
        @Body device: Device
    ): Response<Void>

    @GET("")
    suspend fun pushNewLocation() {

    }

    @GET(DEVICE_ENDPOINT)
    suspend fun getDevices(): List<Device>
}