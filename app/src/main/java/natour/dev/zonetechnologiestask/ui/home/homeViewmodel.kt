package natour.dev.zonetechnologiestask.ui.home

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import natour.dev.zonetechnologiestask.data.LocationRepository
import natour.dev.zonetechnologiestask.domain.model.Device
import natour.dev.zonetechnologiestask.domain.model.LocationModel
import natour.dev.zonetechnologiestask.network.TraccarApi
import natour.dev.zonetechnologiestask.network.TraccarApiClient
import natour.dev.zonetechnologiestask.ui.services.TAG

class homeViewmodel : ViewModel() {

    private val apiService: TraccarApi = TraccarApiClient.api


    val locationList: LiveData<List<LocationModel>> = LocationRepository.locationList

    fun pushLocation(lat: Double, lon: Double) = viewModelScope.launch {
        LocationRepository.pushLocation(lat, lon)
    }

    fun registerNewDevice(deviceId: String) = viewModelScope.launch {

        val requestBody = Device(uniqueId = deviceId, name = android.os.Build.MODEL)
        try {
            val res = apiService.registerDevice(requestBody)
            Log.d(TAG, "registerNewDevice: success=${res.isSuccessful}")
        } catch (e: Exception) {
            Log.e(TAG, "registerNewDevice error", e)
        }
    }

}
