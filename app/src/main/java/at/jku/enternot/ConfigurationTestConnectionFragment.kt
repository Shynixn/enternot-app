package at.jku.enternot

import android.arch.lifecycle.Observer
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import at.jku.enternot.entity.Configuration
import at.jku.enternot.viewmodel.ConfigurationActivityViewModelImpl
import kotlinx.android.synthetic.main.activity_configuration.*
import kotlinx.android.synthetic.main.fragment_testconnection.*
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
import org.koin.android.architecture.ext.sharedViewModel

class ConfigurationTestConnectionFragment : Fragment() {
    private val configurationViewModel: ConfigurationActivityViewModelImpl by sharedViewModel()

    companion object {
        const val TAG = "CONFIGURATIONTESTCONNECTION_FRAGMENT"
    }

    /**
     * Android override.
     */
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_testconnection, container, false)
    }

    /**
     * Called immediately after [.onCreateView]
     * has returned, but before any saved state has been restored in to the view.
     * This gives subclasses a chance to initialize themselves once
     * they know their view hierarchy has been completely created.  The fragment's
     * view hierarchy is not however attached to its parent at this point.
     * @param view The View returned by [.onCreateView].
     * @param savedInstanceState If non-null, this fragment is being re-constructed
     * from a previous saved state as given here.
     */
    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        activity.navigation_configuration.menu.getItem(1).isEnabled = false
        activity.navigation_configuration.menu.getItem(1).isChecked = false
        activity.navigation_configuration.menu.getItem(0).isEnabled = true
        activity.navigation_configuration.menu.getItem(0).isChecked = false

        this.button_testConnection_configurationPage.setOnClickListener(testConnectionListener)

        configurationViewModel.getConfiguration().observe(this, Observer { config ->
            if (config != null) {
                if (this.editText_host_configurationPage.text.isNullOrEmpty()) {
                    this.editText_host_configurationPage.setText(config.hostname)
                }

                if (this.editText_username_configurationPage.text.isNullOrEmpty()) {
                    this.editText_username_configurationPage.setText(config.username)
                }

                if (this.editText_password_configurationPage.text.isNullOrEmpty()) {
                    this.editText_password_configurationPage.setText(config.password)
                }
            }
        })

        configurationViewModel.getSuccessfullState().observe(this, Observer { isSuccessful ->
            if (isSuccessful!!) {
                this.textView_success_configurationPage.visibility = View.VISIBLE
                activity.navigation_configuration.menu.getItem(1).isEnabled = true
                activity.navigation_configuration.menu.getItem(1).isChecked = true
            } else {
                this.textView_success_configurationPage.visibility = View.GONE
                activity.navigation_configuration.menu.getItem(1).isEnabled = false
                activity.navigation_configuration.menu.getItem(1).isChecked = false
                activity.navigation_configuration.menu.getItem(0).isEnabled = true
                activity.navigation_configuration.menu.getItem(0).isChecked = false
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
        })
    }

    /**
     * When the view gets clicked the app checks for the server if it can connect.
     */
    private val testConnectionListener = View.OnClickListener {
        val configuration = getCurrentConfiguration()

        if (configuration != null) {
            configurationViewModel.saveConfiguration(configuration)
            configurationViewModel.getProgressingState().value = true

            context.doAsync {
                val response = configurationViewModel.testConnection(configuration)

                uiThread { context ->
                    when (response.statusCode) {
                        200 -> {
                            Toast.makeText(context, response.content!!, Toast.LENGTH_LONG).show()
                            configuration.configured = true
                            configurationViewModel.saveConfiguration(configuration)

                            configurationViewModel.getSuccessfullState().value = true
                        }
                        401 -> Toast.makeText(context, "Entered username or password is invalid", Toast.LENGTH_LONG).show()
                        else -> Toast.makeText(context, "Cannot connect to the server", Toast.LENGTH_LONG).show()
                    }

                    configurationViewModel.getProgressingState().value = false
                }
            }
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
            hostname.isEmpty() -> Toast.makeText(this.context, "Please enter a hostname", Toast.LENGTH_LONG).show()
            username.isEmpty() -> Toast.makeText(this.context, "Please enter a username", Toast.LENGTH_LONG).show()
            password.isEmpty() -> Toast.makeText(this.context, "Please enter a password", Toast.LENGTH_LONG).show()
            else -> {
                return Configuration(hostname, username, password, false)
            }
        }

        return null
    }
}
