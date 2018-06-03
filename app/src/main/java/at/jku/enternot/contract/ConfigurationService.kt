package at.jku.enternot.contract

import android.content.Context
import at.jku.enternot.entity.Configuration
import java.io.IOException

interface ConfigurationService {

    /**
     * Gets the app configuration settings.
     * Returns null if nothing is stored.
     */
    @Throws(IOException::class)
    fun getConfiguration(context: Context): Configuration?

    /**
     * Saves the app configuration.
     */
    @Throws(IOException::class)
    fun saveConfiguration(configuration: Configuration, context: Context)
}