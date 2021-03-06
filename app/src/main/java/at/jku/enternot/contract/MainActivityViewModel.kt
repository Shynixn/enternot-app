package at.jku.enternot.contract

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import at.jku.enternot.entity.Configuration
import at.jku.enternot.entity.SirenBlinkingState

interface MainActivityViewModel {

    /**
     * Plays the siren at the house of the app user.
     * Returns the status code of the webRequest.
     */
    fun playSiren(): Int

    /**
     * Stops the siren at the house of the app user.
     * Returns the status code of the webRequest.
     */
    fun stopSiren(): Int

    /**
     * Gets the progressing state of the app.
     */
    fun getProgressingState(): MutableLiveData<Boolean>

    /**
     * Gets the siren button state of the app.
     */
    fun getSirenState(): MutableLiveData<Boolean>

    /**
     * Gets the blinking state of the siren.
     */
    fun getSirenBlinkingState(): MutableLiveData<SirenBlinkingState>

    /**
     * Gets the current app configuration.
     */
    fun getConfiguration(): LiveData<Configuration>

    /**
     * Gets the accelerometer sensor data.
     */
    fun getCameraMovementData(): MutableLiveData<Triple<Float, Float, Float>>

    /**
     * Enables or disables the camera movement.
     * @param b True if the camera movement should be enabled otherwise false.
     */
    fun enableCameraMovement(b: Boolean)

    /**
     * Sends the camera movement changes to the pi.
     */
    fun sendCameraMovement(data: Triple<Float, Float, Float>)

    /**
     * Enables or disables the voice recording.
     * @param b True if the voice recording should be enabled otherwise false.
     */
    fun enableVoiceRecording(b: Boolean)

    /**  
     * Saves the given configuration.
     */
    fun saveConfiguration(configuration: Configuration)
}