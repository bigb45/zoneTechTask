package natour.dev.zonetechnologiestask.domain.usecase

import android.util.Log
import natour.dev.zonetechnologiestask.network.TraccarApi
import natour.dev.zonetechnologiestask.ui.services.TAG

class CheckDeviceRegisteredUseCase(private val api: TraccarApi) {

    suspend operator fun invoke(uniqueId: String): Boolean {
        val allDevices = api.getDevices()
        Log.d(TAG, "invoke: all devices: $allDevices")
        Log.d(TAG, "invoke: device exists: ${allDevices.any { it.uniqueId == uniqueId }}")
        return allDevices.any { it.uniqueId == uniqueId }
    }
}

