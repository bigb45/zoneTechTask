package natour.dev.zonetechnologiestask

import android.Manifest.permission.ACCESS_COARSE_LOCATION
import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.Manifest.permission.POST_NOTIFICATIONS
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Looper
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresPermission
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.gms.location.Granularity
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import natour.dev.zonetechnologiestask.databinding.FragmentFirstBinding
import natour.dev.zonetechnologiestask.locationListAdapter.LocationListAdapter
import natour.dev.zonetechnologiestask.model.LocationUpdateTileData

/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */

val listItems = listOf(
    LocationUpdateTileData(
        "1",
        "now",
        "32.8974",
        "32.8974",
    ),   LocationUpdateTileData(
        "2",
        "now",
        "32.8974",
        "32.8974",
    ),   LocationUpdateTileData(
        "3",
        "now",
        "32.8974",
        "32.8974",
    ),   LocationUpdateTileData(
        "4",
        "now",
        "32.8974",
        "32.8974",
    ),   LocationUpdateTileData(
        "5",
        "now",
        "32.8974",
        "32.8974",
    ),  LocationUpdateTileData(
        "6",
        "now",
        "32.8974",
        "32.8974",
    ),  LocationUpdateTileData(
        "7",
        "now",
        "32.8974",
        "32.8974",
    ), LocationUpdateTileData(
        "8",
        "now",
        "32.8974",
        "32.8974",
    ), LocationUpdateTileData(
        "9",
        "now",
        "32.8974",
        "32.8974",
    ), LocationUpdateTileData(
        "10",
        "now",
        "32.8974",
        "32.8974",
    ), LocationUpdateTileData(
        "11",
        "now",
        "32.8974",
        "32.8974",
    ),
)
class FirstFragment : Fragment() {

    private var _binding: FragmentFirstBinding? = null
    private lateinit var locationCallback: LocationCallback
    private lateinit var previousUpdatesAdapter: LocationListAdapter

    private val binding get() = _binding!!

    @RequiresPermission(allOf = [ACCESS_FINE_LOCATION, ACCESS_COARSE_LOCATION])
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {


        _binding = FragmentFirstBinding.inflate(inflater, container, false)

        setupAdapter()
        val hasPermission = checkHasPermissions()
        Log.d(TAG, "onCreateView: has permissions: $hasPermission")
        if (hasPermission) {
            startLocationTracking()
        }
        return binding.root

    }

    private fun setupAdapter() {
        previousUpdatesAdapter = LocationListAdapter()
        previousUpdatesAdapter.submitList(listItems)
        binding.locationUpdates.layoutManager = LinearLayoutManager(requireContext())

        binding.locationUpdates.adapter = previousUpdatesAdapter

    }


//    private val locationPermission =
//        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { results ->
//            val fineGranted = results[ACCESS_FINE_LOCATION] ?: false
//            val coarseGranted = results[ACCESS_COARSE_LOCATION] ?: false
//            updateUi(fineGranted && coarseGranted)
//            if (fineGranted || coarseGranted) {
//                // continue
//                Log.d(TAG, "fine granted: $fineGranted, coarse granted: $coarseGranted")
//            } else {
//                Log.d(TAG, "permissions not granted!")
//                showRequiredPermissionsDialog()
//            }
//        }


    @RequiresPermission(allOf = [ACCESS_FINE_LOCATION, ACCESS_COARSE_LOCATION])
    private fun startLocationTracking() {
        Log.d(TAG, "startLocationTracking: starting tracking...")
        val locationRequest =
            LocationRequest.Builder(500L).setPriority(Priority.PRIORITY_HIGH_ACCURACY)
                .setGranularity(Granularity.GRANULARITY_FINE).setMinUpdateDistanceMeters(10F)
                .setMinUpdateIntervalMillis(500L)
                .build()
        val client = LocationServices.getFusedLocationProviderClient(requireContext())

        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                super.onLocationResult(locationResult)
                Log.d(TAG, "onLocationResult: ${locationResult.lastLocation}")
            }
        }

        client.lastLocation.addOnSuccessListener {
            Log.d(TAG, "lastLocation: $it")
        }
        client.requestLocationUpdates(
            locationRequest, locationCallback, Looper.getMainLooper()
        )
        Log.d(TAG, "startLocationTracking: requested updates")
    }

    private fun checkHasPermissions(): Boolean {
        val fineLocationGranted = ContextCompat.checkSelfPermission(
            requireContext(), ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
        val coarseLocationGranted = ContextCompat.checkSelfPermission(
            requireContext(), ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
        return fineLocationGranted && coarseLocationGranted
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.buttonFirst.setOnClickListener {
//            findNavController().navigate(R.id.action_FirstFragment_to_SecondFragment)
            startLocationTracking()

        }
    }

//    private fun updateUi(hasPermission: Boolean) {
//
//        if (!hasPermission) {
//            binding.textviewFirst.text = "Permission not granted."
//
//        }
//        binding.buttonFirst.setOnClickListener {
//            if (hasPermission) {
//                Log.d(TAG, "updateUi: clicked button")
////                findNavController().navigate(R.id.action_FirstFragment_to_SecondFragment)
//            } else {
//                val settingsIntent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).also {
//                    val uri = "package:${context?.packageName}".toUri()
//                    it.flags = Intent.FLAG_ACTIVITY_NEW_TASK
//                    it.data = uri
//
//                }
//                startActivity(settingsIntent)
//            }
//        }
//    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}