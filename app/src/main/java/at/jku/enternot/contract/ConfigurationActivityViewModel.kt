package at.jku.enternot.contract

import android.arch.lifecycle.LiveData
import at.jku.enternot.entity.Configuration

interface ConfigurationActivityViewModel {

    /**
     * Gets the configuration of the app.
     */
    fun getConfiguration(): LiveData<Configuration>

    /**
     * Saves the given configuration.
     */
    fun saveConfiguration(configuration: Configuration)
}