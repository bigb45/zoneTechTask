package natour.dev.zonetechnologiestask.core.util

import android.util.Log
import natour.dev.zonetechnologiestask.ui.services.TAG
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

object UniqueIdUtil {

    @OptIn(ExperimentalUuidApi::class)
    fun generateRandomId(): String {
         val randomUuid = Uuid.random().toString().slice(0..7)
        Log.d(TAG, "generateRandomId: $randomUuid")
        return randomUuid

    }
}