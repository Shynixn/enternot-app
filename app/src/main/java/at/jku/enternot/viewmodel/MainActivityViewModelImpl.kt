package at.jku.enternot.viewmodel

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import at.jku.enternot.contract.MainActivityViewModel
import at.jku.enternot.contract.SirenService
import java.io.IOException

class MainActivityViewModelImpl(applicationContext: Application, private val sirenService: SirenService) : AndroidViewModel(applicationContext), MainActivityViewModel {
    /**
     * Plays the siren at the house of the app user.
     * @throws [IOException] when the connection to the server fails.
     */
    @Throws(IOException::class)
    override fun playSiren() {
        sirenService.playSiren()
    }
}