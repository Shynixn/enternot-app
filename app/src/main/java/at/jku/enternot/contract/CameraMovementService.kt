package at.jku.enternot.contract

import android.arch.lifecycle.MutableLiveData

interface CameraMovementService {

    /**
     * Enables or disables the camera movement.
     * @param b True if the camera movement should be enabled otherwise false.
     */
    fun enableCameraMovement(b: Boolean)

    /**
     * Gets the axis moving data.
     */
    fun getAxisData(): MutableLiveData<Triple<Float, Float, Float>>

    /**
     * Reads a set of axis data, calculate the average value for all three axis
     * and saves these values to the local storage.
     * @param f: Callback when the calibration has finished.
     */
    fun calibrateSensor(f: () -> Unit)

    /**
     * Sends axis data to the pi.
     */
    fun sendAxisData(data: Triple<Float, Float, Float>)
}