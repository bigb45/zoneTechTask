package natour.dev.zonetechnologiestask.ui.firstFragment

import android.Manifest
import android.Manifest.permission.ACCESS_COARSE_LOCATION
import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.Manifest.permission.POST_NOTIFICATIONS
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.LocationManager
import android.os.Bundle
import android.os.Looper
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.annotation.RequiresPermission
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.gms.location.Granularity
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import natour.dev.zonetechnologiestask.CustomAlertDialog
import natour.dev.zonetechnologiestask.R
import natour.dev.zonetechnologiestask.core.util.PermissionUtil.requestLocationPermissions
import natour.dev.zonetechnologiestask.core.util.SharedPreferencesUtil
import natour.dev.zonetechnologiestask.databinding.FragmentFirstBinding
import natour.dev.zonetechnologiestask.domain.model.LocationModel
import natour.dev.zonetechnologiestask.locationListAdapter.LocationListAdapter
import natour.dev.zonetechnologiestask.ui.services.LocationTrackingService
import natour.dev.zonetechnologiestask.ui.services.TAG



class FirstFragment : Fragment() {

    private var _binding: FragmentFirstBinding? = null
    private val binding get() = _binding!!
    private val viewmodel: FirstFragmentViewmodel by viewModels()


    private lateinit var locationPermissionLauncher: ActivityResultLauncher<Array<String>>
    private lateinit var previousUpdatesAdapter: LocationListAdapter

    private var isTracking = false

    @RequiresPermission(allOf = [ACCESS_FINE_LOCATION, ACCESS_COARSE_LOCATION])
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFirstBinding.inflate(inflater, container, false)
        isTracking = SharedPreferencesUtil.getValue(
            getString(R.string.sharedPreferencesIsTrackingKey), false
        )

        if (hasLocationPermissions()) {
            startLocationTracking()
        } else {
            locationPermissionLauncher.launch(
                arrayOf(ACCESS_FINE_LOCATION, ACCESS_COARSE_LOCATION)
            )
        }

        return binding.root
    }

    private fun startLocationTracking(): Boolean {
        val isGpsEnabled = isLocationEnabled()
        if (!isGpsEnabled) {
            CustomAlertDialog.newInstance(
                message = "Please enable location services!",
                positiveText = "Ok",
                onPositive = {
                    Log.d(TAG, "User prompted to enable location services")
                }
            ).show(parentFragmentManager, "enable_gps")
            return false
        }

        val intent = Intent(requireContext(), LocationTrackingService::class.java)
        ContextCompat.startForegroundService(requireContext(), intent)
        return true
    }

    @RequiresPermission(POST_NOTIFICATIONS)
    private fun stopLocationTracking() {
        val intent = Intent(requireContext(), LocationTrackingService::class.java)
        requireContext().stopService(intent)
        val notification = NotificationCompat.Builder(requireContext(), "tracking_channel")
            .setContentTitle("Tracking stopped")
            .setContentText("Your location is not being shared.")
            .setSmallIcon(R.drawable.zone)
            .build()
        NotificationManagerCompat.from(requireContext()).notify(1, notification)


    }


    @RequiresPermission(allOf = [ACCESS_FINE_LOCATION, ACCESS_COARSE_LOCATION, POST_NOTIFICATIONS])
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.buttonFirst.text = if (isTracking) "stop tracking" else "start tracking"

        binding.buttonFirst.setOnClickListener {
            toggleTracking()
        }
        
        observeViewmodel()
        setupAdapter()
        setupPermissionLauncher()

    }

    @RequiresPermission(POST_NOTIFICATIONS)
    private fun toggleTracking() {
        if (isTracking) {
            stopLocationTracking()
            isTracking = false
        } else {
            val started = startLocationTracking()
            if (started) isTracking = true
        }
        SharedPreferencesUtil.setValue(getString(R.string.sharedPreferencesIsTrackingKey), isTracking)
        updateUi()
    }


        private fun setupAdapter() {
            previousUpdatesAdapter = LocationListAdapter()
            val list = listOf(LocationModel(id="a58f2678", timestamp="2025-06-29T14:34:24.694Z", lon="35.20166", lat="31.91297"), LocationModel(id="70a6c8c8", timestamp="2025-06-29T14:34:44.384Z", lon="35.20166", lat="31.91297"))

            previousUpdatesAdapter.submitList(emptyList())
            binding.locationUpdates.layoutManager = LinearLayoutManager(requireContext())
            binding.locationUpdates.adapter = previousUpdatesAdapter
        }

        @RequiresPermission(allOf = [ACCESS_FINE_LOCATION, ACCESS_COARSE_LOCATION])
        private fun setupPermissionLauncher() {
            locationPermissionLauncher = requestLocationPermissions(requireActivity()) { granted ->
                if (granted) {
                    startLocationTracking()
                } else {
                    showRequiredPermissionsDialog()
                }
            }
        }

    private fun observeViewmodel() {
        Log.d(TAG, "observeViewmodel: observing!")
        viewmodel.locationList.observe(viewLifecycleOwner) {
            onLocationListUpdate(it)
        }
    }

    private fun onLocationListUpdate(locationList: List<LocationModel>) {
        Log.d(TAG, "RecyclerView adapter item count before: ${previousUpdatesAdapter.itemCount}")
        previousUpdatesAdapter.submitList(locationList)

    }

    private fun hasLocationPermissions(): Boolean {
        val fineGranted = ContextCompat.checkSelfPermission(
            requireContext(), ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
        val coarseGranted = ContextCompat.checkSelfPermission(
            requireContext(), ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
        return fineGranted || coarseGranted
    }

    private fun showRequiredPermissionsDialog() {
        CustomAlertDialog.newInstance(
            message = "Please allow location access from the settings!",
            positiveText = "Open settings",
            onPositive = {
                val settingsIntent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).also {
                    val uri = "package:${context?.packageName}".toUri()
                    it.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                    it.data = uri

                }
                startActivity(settingsIntent)
            }
        ).show(parentFragmentManager, "enable gps")
    }


    private fun updateUi() {
        binding.buttonFirst.text = if (isTracking) "stop tracking" else "start tracking"
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun isLocationEnabled(): Boolean {
        val locationManager = requireContext().getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
    }

}
