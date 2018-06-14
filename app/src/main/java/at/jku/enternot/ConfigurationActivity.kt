package at.jku.enternot

import android.arch.lifecycle.Observer
import android.content.Intent
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import at.jku.enternot.entity.Configuration
import at.jku.enternot.viewmodel.ConfigurationActivityViewModelImpl
import kotlinx.android.synthetic.main.activity_configuration.*
import kotlinx.android.synthetic.main.fragment_testconnection.*
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
import org.koin.android.architecture.ext.viewModel

class ConfigurationActivity : AppCompatActivity() {
    private val configurationViewModel: ConfigurationActivityViewModelImpl by viewModel()

    //region Android

    /**
     * Android override create.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        // region Design
        setTheme(R.style.AppTheme)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_configuration)

        toolbar_configuration.setTitleTextColor(ContextCompat.getColor(this, android.R.color.white))
        setSupportActionBar(toolbar_configuration)

        println("MEME")

        configurationViewModel.getFragementNumber().observe(this, Observer { number ->
            if (number == 1) {
                val transaction = supportFragmentManager.beginTransaction()
                transaction.replace(R.id.fragment_container_configurationPage, WelcomeFragment())
                transaction.commit()
            } else {

                val transaction = supportFragmentManager.beginTransaction()
                transaction.replace(R.id.fragment_container_configurationPage,ConfigureFragment())
                transaction.commit()

                // region Listener
                this.button_testConnection_configurationPage.setOnClickListener(testConnectionListener)
                // endregion

                configurationViewModel.getConfiguration().observe(this, Observer { config ->
                    if (config != null) {
                        this.editText_host_configurationPage.setText(config.hostname)
                        this.editText_username_configurationPage.setText(config.username)
                        this.editText_password_configurationPage.setText(config.password)
                    }
                })

                configurationViewModel.getProgressingState().observe(this, Observer { isProgressing ->
                    if (isProgressing!!) {
                        progressbar_testConnection_configurationPage.visibility = View.VISIBLE
                        button_testConnection_configurationPage.isEnabled = false
                    } else {
                        progressbar_testConnection_configurationPage.visibility = View.INVISIBLE
                        button_testConnection_configurationPage.isEnabled = true
                    }
                });
            }
        });

    }

    /**
     * Android override createOptionsMenu.
     */
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        toolbar_configuration.inflateMenu(R.menu.configuration_menu)
        return true
    }

    /**
     * Android override onOptionsItemSelected.
     */
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.menuItem_acceptSettings_configurationPage) {
            saveConfigurationListener.onClick(item.actionView)
        }

        return super.onOptionsItemSelected(item)
    }

    // endregion

    /**
     * When the view gets clicked the app checks for the server if it can connect.
     */
    private val testConnectionListener = View.OnClickListener {
        val configuration = getCurrentConfiguration()

        if (configuration != null) {
            configurationViewModel.saveConfiguration(configuration)
            configurationViewModel.getProgressingState().value = true

            doAsync {
                val response = configurationViewModel.testConnection(configuration)

                uiThread { context ->
                    when (response.statusCode) {
                        200 -> Toast.makeText(context, response.content!!, Toast.LENGTH_LONG).show()
                        401 -> Toast.makeText(context, "Entered username or password is invalid", Toast.LENGTH_LONG).show()
                        else -> Toast.makeText(context, "Cannot connect to the server", Toast.LENGTH_LONG).show()
                    }

                    configurationViewModel.getProgressingState().value = false
                }
            }
        }
    }

    /**
     * When the view gets clicked the config values get checked, stored and opens the [MainActivity].
     */
    private val saveConfigurationListener = View.OnClickListener {
        val configuration = getCurrentConfiguration()

        if (configuration != null) {
            configurationViewModel.saveConfiguration(configuration)

            val intent = Intent(this, MainActivity::class.java)
            intent.flags = intent.flags or Intent.FLAG_ACTIVITY_NO_HISTORY
            startActivity(intent)
        }
    }

    /**
     * Returns the current entered [Configuration].
     */
    private fun getCurrentConfiguration(): Configuration? {
        val hostname = this.editText_host_configurationPage.text.toString()
        val username = this.editText_username_configurationPage.text.toString()
        val password = this.editText_password_configurationPage.text.toString()

        when {
            hostname.isEmpty() -> Toast.makeText(this, "Please enter a hostname", Toast.LENGTH_LONG).show()
            username.isEmpty() -> Toast.makeText(this, "Please enter a username", Toast.LENGTH_LONG).show()
            password.isEmpty() -> Toast.makeText(this, "Please enter a password", Toast.LENGTH_LONG).show()
            else -> {
                return Configuration(hostname, username, password)
            }
        }

        return null
    }
}
