package at.jku.enternot.contract

interface CalibrationDialogViewModel {

    /**
     * Calibrate the camera movement service.
     * @param f Callback on finishing the calibration.
     */
    fun calibrateCameraMovement(f: () -> Unit)

}