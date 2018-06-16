package at.jku.enternot.service

import android.content.Context
import at.jku.enternot.R
import at.jku.enternot.contract.ConfigurationService
import at.jku.enternot.entity.Configuration
import java.io.IOException

class ConfigurationServiceImpl : ConfigurationService {
    /**
     * Saves the app configuration.
     */
    override fun saveConfiguration(configuration: Configuration, context: Context) {
        val sharedPref = context.getSharedPreferences(context.getString(R.string.preference_file_key), Context.MODE_PRIVATE)

        with(sharedPref.edit()) {
            putString("hostname", configuration.hostname)
            putString("username", configuration.username)
            putString("password", configuration.password)
            putBoolean("configured", configuration.configured)
            // Security Concerns: SharedPreferences has the same security level as the account manager in android.
            // They both are getting stored in an apps personal storage which is only accessible by the app creating it. The AccountManager should be used once you get
            // an online authentication service (OAuth, Tokens...), but to keep it simple with Basic Authentication we use this.
            apply()
        }
    }

    /**
     * Gets the app configuration settings.
     * Returns null if nothing is stored.
     */
    @Throws(IOException::class)
    override fun getConfiguration(context: Context): Configuration? {
        val sharedPref = context.getSharedPreferences(context.getString(R.string.preference_file_key), Context.MODE_PRIVATE)

        val hostname = sharedPref.getString("hostname", null) ?: return null
        val username = sharedPref.getString("username", null) ?: return null
        val password = sharedPref.getString("password", null) ?: return null
        val configured = sharedPref.getBoolean("configured", false)

        return Configuration(hostname, username, password, configured)
    }
}