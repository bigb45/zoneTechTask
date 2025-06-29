package natour.dev.zonetechnologiestask.ui.home

import android.Manifest.permission.ACCESS_COARSE_LOCATION
import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.Manifest.permission.POST_NOTIFICATIONS
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.LocationManager
import android.os.Bundle
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
import natour.dev.zonetechnologiestask.CustomAlertDialog
import natour.dev.zonetechnologiestask.R
import natour.dev.zonetechnologiestask.core.util.PermissionUtil.requestLocationPermissions
import natour.dev.zonetechnologiestask.core.util.SharedPreferencesUtil
import natour.dev.zonetechnologiestask.databinding.FragmentFirstBinding
import natour.dev.zonetechnologiestask.domain.model.LocationModel
import natour.dev.zonetechnologiestask.ui.locationListAdapter.LocationListAdapter
import natour.dev.zonetechnologiestask.ui.services.LocationTrackingService
import natour.dev.zonetechnologiestask.ui.services.TAG



class homeFragment : Fragment() {

    private var _binding: FragmentFirstBinding? = null
    private val binding get() = _binding!!
    private val viewmodel: homeViewmodel by viewModels()


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
                message = getString(R.string.please_enable_location_services),
                positiveText = getString(R.string.ok),
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
            .setContentTitle(getString(R.string.tracking_stopped))
            .setContentText(getString(R.string.your_location_is_not_being_shared))
            .setSmallIcon(R.drawable.zone)
            .build()
        NotificationManagerCompat.from(requireContext()).notify(1, notification)


    }


    @RequiresPermission(allOf = [ACCESS_FINE_LOCATION, ACCESS_COARSE_LOCATION, POST_NOTIFICATIONS])
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.buttonFirst.text = if (isTracking) getString(R.string.stop_tracking) else getString(
            R.string.start_tracking
        )

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
        viewmodel.locationList.observe(viewLifecycleOwner) {
            onLocationListUpdate(it)
        }
    }

    private fun onLocationListUpdate(locationList: List<LocationModel>) {
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
            message = getString(R.string.please_allow_location_access_from_the_settings),
            positiveText = getString(R.string.open_settings),
            onPositive = {
                val settingsIntent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).also {
                    val uri = "package:${context?.packageName}".toUri()
                    it.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                    it.data = uri

                }
                startActivity(settingsIntent)
            }
        ).show(parentFragmentManager, "enable_gps")
    }


    private fun updateUi() {
        binding.buttonFirst.text = if (isTracking) getString(R.string.stop_tracking) else getString(
            R.string.start_tracking
        )
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
