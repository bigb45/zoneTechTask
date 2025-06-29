package natour.dev.zonetechnologiestask

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment

class CustomAlertDialog : DialogFragment() {

    companion object {
        fun newInstance(
            message: String,
            positiveText: String,
            onPositive: () -> Unit,
            cancelable: Boolean = true
        ): CustomAlertDialog {
            return CustomAlertDialog().apply {
                this.message = message
                this.positiveText = positiveText
                this.onPositive = onPositive
                this.isCancelable = cancelable
            }
        }
    }

    private lateinit var message: String
    private lateinit var positiveText: String
    private var onPositive: (() -> Unit)? = null

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return requireActivity().let {
            AlertDialog.Builder(it).setMessage(message).setCancelable(isCancelable)
                .setPositiveButton(positiveText) { _, _ -> onPositive?.invoke() }.create()
        }
    }
}
