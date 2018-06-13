package at.jku.enternot.viewmodel

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.util.Log
import at.jku.enternot.contract.ConfigurationActivityViewModel
import at.jku.enternot.contract.ConfigurationService
import at.jku.enternot.contract.ConnectionService
import at.jku.enternot.entity.Configuration
import at.jku.enternot.entity.Response
import org.jetbrains.anko.doAsync
import java.io.IOException

class ConfigurationActivityViewModelImpl(applicationContext: Application, private val configurationService: ConfigurationService, private val connectionService: ConnectionService) : AndroidViewModel(applicationContext), ConfigurationActivityViewModel {
    private val logTag: String = ConfigurationActivityViewModelImpl::class.java.simpleName
    private var configuration: MutableLiveData<Configuration>? = null
    private var progressingLoad: MutableLiveData<Boolean> = MutableLiveData()

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
     * Gets the progressing state of the app.
     */
    override fun getProgressingState(): MutableLiveData<Boolean> {
        return progressingLoad
    }

    /**
     * Checks if the entered configuration can be used to connect to a server.
     */
    override fun testConnection(configuration: Configuration): Response<String> {
        return try {
            connectionService.get("/status", String::class.java, getApplication())
        } catch (e: Exception) {
            Log.e(logTag, "Failed to connect to the server.", e)
            Response(500)
        }
    }

    /**
     * Saves the given configuration.
     */
    override fun saveConfiguration(configuration: Configuration) {
        doAsync {
            try {
                configurationService.saveConfiguration(configuration, getApplication())
            } catch (e: IOException) {
                Log.e(logTag, "Failed to save configuration.", e)
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
                Log.e(logTag, "Failed to load configuration.", e)
            }
        }
    }
}