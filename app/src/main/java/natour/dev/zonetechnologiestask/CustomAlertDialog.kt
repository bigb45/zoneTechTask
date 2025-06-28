package natour.dev.zonetechnologiestask

import android.app.AlertDialog
import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import androidx.fragment.app.DialogFragment
import androidx.core.net.toUri

class CustomAlertDialog: DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            // Use the Builder class for convenient dialog construction.
            val builder = AlertDialog.Builder(it)
            builder.setMessage("Start game")
                .setCancelable(false)
                .setPositiveButton("Start") { dialog, id ->
                    val settingsIntent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).also {
                        val uri = "package:${context?.packageName}".toUri()
                        it.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                        it.data = uri

                    }
                    startActivity(settingsIntent)
                }


            builder.create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }


}
