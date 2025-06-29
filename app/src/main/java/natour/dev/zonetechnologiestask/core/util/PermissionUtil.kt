package natour.dev.zonetechnologiestask.core.util

import android.Manifest
import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts

object PermissionUtil {

    fun requestLocationPermissions(
        activity: ComponentActivity,
        onResult: (granted: Boolean) -> Unit
    ): ActivityResultLauncher<Array<String>> {
        return activity.registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { results ->
            val fine = results[Manifest.permission.ACCESS_FINE_LOCATION] ?: false
            val coarse = results[Manifest.permission.ACCESS_COARSE_LOCATION] ?: false
            onResult(fine || coarse)
        }
    }
}