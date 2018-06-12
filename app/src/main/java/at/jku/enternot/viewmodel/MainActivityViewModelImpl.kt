package at.jku.enternot.viewmodel

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.util.Log
import at.jku.enternot.contract.ConfigurationService
import at.jku.enternot.contract.CameraMovementService
import at.jku.enternot.contract.MainActivityViewModel
import at.jku.enternot.contract.SirenService
import at.jku.enternot.entity.Configuration
import at.jku.enternot.entity.SirenBlinkingState
import org.jetbrains.anko.doAsync
import java.io.IOException

class MainActivityViewModelImpl(applicationContext: Application, private val sirenService: SirenService, private val configurationService: ConfigurationService) : AndroidViewModel(applicationContext), MainActivityViewModel {
    private val logTag: String = MainActivityViewModelImpl::class.java.simpleName
    private var progressingLoad: MutableLiveData<Boolean> = MutableLiveData()
    private var blinkingState: MutableLiveData<SirenBlinkingState> = MutableLiveData()
    private var sirenButtonState: MutableLiveData<Boolean> = MutableLiveData()
    private var configuration: MutableLiveData<Configuration>? = null

    init {
        blinkingState.value = SirenBlinkingState.DISABLED
    }

    /**
     * Gets the current app configuration.
     */
    override fun getConfiguration(): LiveData<Configuration> {
        if (configuration == null) {
            configuration = MutableLiveData()
            loadConfiguration()
        }

        return configuration!!
    }

    /**
     * Gets the siren button state of the app.
     */
    override fun getSirenButtonState(): MutableLiveData<Boolean> {
        return sirenButtonState
    }

    /**
     * Gets the progressing state of the app.
     */
    override fun getProgressingState(): MutableLiveData<Boolean> {
        return progressingLoad
    }

    /**
     * Gets the blinking state of the siren.
     */
    override fun getSirenBlinkingState(): MutableLiveData<SirenBlinkingState> {
        return blinkingState
    }

    /**
     * Gets the accelerometer sensor data.
     */
    override fun getCameraMovementData(): MutableLiveData<Triple<Float, Float, Float>> {
        return cameraMovementService.getAxisData()
    }

    /**
     * Plays the siren at the house of the app user.
     * @throws [IOException] when the connection to the server fails.
     */
    override fun playSiren(): Int {
        return try {
            sirenService.playSiren()
        } catch (e: IOException) {
            Log.e(logTag, "Failed to connect to the server.", e)
            500
        }
    }

    /**
     * Loads the configuration asynchronously.
     */
    private fun loadConfiguration() {
        doAsync {
            try {
                configuration!!.postValue(configurationService.getConfiguration(getApplication()))
            } catch (e: IOException) {
                Log.e(logTag, "Failed to load configuration.", e)
            }
        }
    }

    /**
     * Enables or disables the camera movement.
     * @param b True if the camera movement should be enabled otherwise false.
     */
    override fun enableCameraMovement(b: Boolean) {
        cameraMovementService.enableCameraMovement(b)
    }
}