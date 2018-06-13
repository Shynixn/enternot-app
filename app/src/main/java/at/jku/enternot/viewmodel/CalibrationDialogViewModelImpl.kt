package at.jku.enternot.viewmodel

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import at.jku.enternot.contract.CalibrationDialogViewModel
import at.jku.enternot.contract.CameraMovementService

class CalibrationDialogViewModelImpl(applicationContext: Application,
                                     private val cameraMovementService: CameraMovementService) :
        AndroidViewModel(applicationContext), CalibrationDialogViewModel {
    /**
     * Calibrate the camera movement service.
     * @param f Callback on finishing the calibration.
     */
    override fun calibrateCameraMovement(f: () -> Unit) {
        cameraMovementService.calibrateSensor(f)
    }
}