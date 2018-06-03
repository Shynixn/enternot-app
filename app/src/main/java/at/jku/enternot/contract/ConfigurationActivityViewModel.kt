package at.jku.enternot.contract

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import at.jku.enternot.entity.Configuration

interface ConfigurationActivityViewModel {

    /**
     * Gets the progressing state of the app.
     */
    fun getProgressingState(): MutableLiveData<Boolean>

    /**
     * Gets the configuration of the app.
     */
    fun getConfiguration(): LiveData<Configuration>

    /**
     * Saves the given configuration.
     */
    fun saveConfiguration(configuration: Configuration)

    /**
     * Checks if the entered configuration can be used to connect to a server.
     */
    fun testConnection(configuration: Configuration): Int
}