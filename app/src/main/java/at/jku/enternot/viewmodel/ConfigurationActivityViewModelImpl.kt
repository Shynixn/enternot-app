package at.jku.enternot.viewmodel

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.util.Log
import at.jku.enternot.contract.ConfigurationActivityViewModel
import at.jku.enternot.contract.ConfigurationService
import at.jku.enternot.entity.Configuration
import org.jetbrains.anko.doAsync
import java.io.IOException

class ConfigurationActivityViewModelImpl(applicationContext: Application, private val configurationService: ConfigurationService) : AndroidViewModel(applicationContext), ConfigurationActivityViewModel {
    private val LOG_TAG: String = ConfigurationActivityViewModelImpl::class.java.simpleName
    private var configuration: MutableLiveData<Configuration>? = null

    /**
     * Gets the configuration of the app.
     */
    override fun getConfiguration(): LiveData<Configuration> {
        if (configuration == null) {
            configuration = MutableLiveData()
            loadConfiguration()
        }

        return configuration!!
    }

    /**
     * Saves the given configuration.
     */
    override fun saveConfiguration(configuration: Configuration) {
        doAsync {
            try {
                configurationService.saveConfiguration(configuration, getApplication())
            } catch (e: IOException) {
                Log.e(LOG_TAG, "Failed to save configuration.", e)
            }
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
                Log.e(LOG_TAG, "Failed to load configuration.", e)
            }
        }
    }
}