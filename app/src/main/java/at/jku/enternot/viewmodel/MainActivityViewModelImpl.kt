package at.jku.enternot.viewmodel

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.MutableLiveData
import android.util.Log
import at.jku.enternot.contract.MainActivityViewModel
import at.jku.enternot.contract.SirenService
import at.jku.enternot.entity.SirenBlinkingState
import java.io.IOException

class MainActivityViewModelImpl(applicationContext: Application, private val sirenService: SirenService) : AndroidViewModel(applicationContext), MainActivityViewModel {
    private val logTag: String = MainActivityViewModelImpl::class.java.simpleName
    private var progressingLoad: MutableLiveData<Boolean> = MutableLiveData()
    private var blinkingState: MutableLiveData<SirenBlinkingState> = MutableLiveData()
    private var sirenButtonState: MutableLiveData<Boolean> = MutableLiveData()

    init {
        blinkingState.value = SirenBlinkingState.DISABLED
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
}