package at.jku.enternot

import android.app.Dialog
import android.app.DialogFragment
import android.os.Bundle
import android.support.v7.app.AlertDialog
import at.jku.enternot.viewmodel.CalibrationDialogViewModelImpl
import org.koin.android.ext.android.inject

class CalibrationDialog : DialogFragment() {

    private val calibrationDialogViewModel: CalibrationDialogViewModelImpl by inject()
    private lateinit var s: () -> Unit
    private lateinit var f: () -> Unit

    /**
     * Override to build your own custom Dialog container.  This is typically
     * used to show an AlertDialog instead of a generic Dialog; when doing so,
     * [.onCreateView] does not need
     * to be implemented since the AlertDialog takes care of its own content.
     *
     *
     * This method will be called after [.onCreate] and
     * before [.onCreateView].  The
     * default implementation simply instantiates and returns a [Dialog]
     * class.
     *
     *
     * *Note: DialogFragment own the [ Dialog.setOnCancelListener][Dialog.setOnCancelListener] and [ Dialog.setOnDismissListener][Dialog.setOnDismissListener] callbacks.  You must not set them yourself.*
     * To find out about these events, override [.onCancel]
     * and [.onDismiss].
     *
     * @param savedInstanceState The last saved instance state of the Fragment,
     * or null if this is a freshly created Fragment.
     *
     * @return Return a new Dialog instance to be displayed by the Fragment.
     */
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(activity, R.style.AlertDialogCustom)
        builder.setTitle(R.string.calibration_dialog_title)
                .setMessage(R.string.calibration_dialog_message)
                .setPositiveButton(R.string.calibration_dialog_positive_button, { _, _ ->
                    s.invoke()
                    calibrationDialogViewModel.calibrateCameraMovement(f)
                })
                .setNegativeButton(R.string.calibration_dialog_negative_button, { dialog, _ ->
                    dialog.cancel()
                })
        return builder.create()
    }

    fun onStartCalibration(s: () -> Unit) {
        this.s = s
    }

    fun onFinished(f: () -> Unit) {
        this.f = f
    }
}