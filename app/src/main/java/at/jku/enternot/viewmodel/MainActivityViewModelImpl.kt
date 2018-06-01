package at.jku.enternot.viewmodel

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.util.Log
import android.widget.Toast
import at.jku.enternot.contract.MainActivityViewModel
import at.jku.enternot.contract.SirenService
import java.io.IOException

class MainActivityViewModelImpl(applicationContext: Application, private val sirenService: SirenService) : AndroidViewModel(applicationContext), MainActivityViewModel {
    private val LOG_TAG: String = MainActivityViewModelImpl::class.java.simpleName

    /**
     * Plays the siren at the house of the app user.
     * Displays a message box when the connection fails.
     */
    override fun playSiren() {
        try {
            sirenService.playSiren()
        } catch (e: IOException) {
            Log.e(LOG_TAG, "Failed to send siren message.", e)
            Toast.makeText(getApplication(), "Cannot connect to the server.", Toast.LENGTH_SHORT).show()
        }
    }
}