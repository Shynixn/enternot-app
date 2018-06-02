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
import kotlinx.android.synthetic.main.content_configuration.*
import org.koin.android.architecture.ext.viewModel


class ConfigurationActivity : AppCompatActivity() {
    private val LOG_TAG: String = ConfigurationActivity::class.java.simpleName
    private val configurationViewModel: ConfigurationActivityViewModelImpl by viewModel()

    //region Android

    /**
     * Android override create.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        // region Design
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_configuration)

        toolbar.setTitleTextColor(ContextCompat.getColor(this, android.R.color.white))
        setSupportActionBar(toolbar)

        // endregion

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
    }

    /**
     * Android override createOptionsMenu.
     */
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        toolbar.inflateMenu(R.menu.configuration_menu)
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
        // TODO: Implement connection test.
    }

    /**
     * When the view gets clicked the config values get checked, stored and opens the [MainActivity].
     */
    private val saveConfigurationListener = View.OnClickListener {
        val hostname = this.editText_host_configurationPage.text.toString()
        val username = this.editText_username_configurationPage.text.toString()
        val password = this.editText_password_configurationPage.text.toString()

        when {
            hostname.isEmpty() -> Toast.makeText(this, "Please enter a hostname", Toast.LENGTH_LONG).show()
            username.isEmpty() -> Toast.makeText(this, "Please enter a username", Toast.LENGTH_LONG).show()
            password.isEmpty() -> Toast.makeText(this, "Please enter a password", Toast.LENGTH_LONG).show()
            else -> {
                val configuration = Configuration(hostname, username, password)
                configurationViewModel.saveConfiguration(configuration)

                val intent = Intent(this, MainActivity::class.java)
                intent.flags = intent.flags or Intent.FLAG_ACTIVITY_NO_HISTORY
                startActivity(intent)
            }
        }
    }
}
