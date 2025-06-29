package natour.dev.zonetechnologiestask.ui

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import natour.dev.zonetechnologiestask.R
import natour.dev.zonetechnologiestask.core.util.SharedPreferencesUtil
import natour.dev.zonetechnologiestask.core.util.UniqueIdUtil.generateRandomId
import natour.dev.zonetechnologiestask.databinding.ActivityMainBinding
import natour.dev.zonetechnologiestask.ui.home.homeViewmodel
import natour.dev.zonetechnologiestask.ui.services.LocationTrackingService

class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding
    private val viewmodel = homeViewmodel()

    override fun onCreate(savedInstanceState: Bundle?) {
        SharedPreferencesUtil.init(this)
        var deviceId = SharedPreferencesUtil.getValue(
            getString(R.string.deviceId), default = ""
        )
        if (deviceId.isEmpty()) {
            deviceId = generateRandomId()
            SharedPreferencesUtil.setValue(
                getString(R.string.deviceId),
                deviceId
            )
            viewmodel.registerNewDevice(deviceId)
        }

        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)

        val navController = findNavController(R.id.nav_host_fragment_content_main)
        appBarConfiguration = AppBarConfiguration(navController.graph)
        setupActionBarWithNavController(navController, appBarConfiguration)


    }

    fun startTrackingService() {
        val foregroundServiceIntent = Intent(this, LocationTrackingService::class.java)
        this.startForegroundService(foregroundServiceIntent)
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }
}