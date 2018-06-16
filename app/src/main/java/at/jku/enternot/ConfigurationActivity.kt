package at.jku.enternot

import android.arch.lifecycle.Observer
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import at.jku.enternot.extension.isOpen
import at.jku.enternot.viewmodel.ConfigurationActivityViewModelImpl
import kotlinx.android.synthetic.main.activity_configuration.*
import org.koin.android.architecture.ext.viewModel

class ConfigurationActivity : AppCompatActivity() {
    private val configurationViewModel: ConfigurationActivityViewModelImpl by viewModel()

    /**
     * Android override create.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.AppTheme)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_configuration)

        toolbar_configuration.setTitleTextColor(ContextCompat.getColor(this, android.R.color.white))
        setSupportActionBar(toolbar_configuration)
        navigation_configuration.setOnNavigationItemReselectedListener(bottomNavigationListener)

        configurationViewModel.getConfiguration().observe(this, Observer { config ->
            if (config != null && config.configured) {
                openMainActivity()
            } else {
                if (supportFragmentManager.findFragmentByTag(ConfigurationWelcomeFragment.TAG) == null && supportFragmentManager.findFragmentByTag(ConfigurationTestConnectionFragment.TAG) == null) {
                    openWelcomeFragment()
                }
            }
        })
    }

    /**
     * Gets called when the user clicks next or back on the bottom navigation bar and changes the current fragment.
     */
    private val bottomNavigationListener = BottomNavigationView.OnNavigationItemReselectedListener { menuItem ->
        if (menuItem.itemId == R.id.navigation_next) {

            if (supportFragmentManager.isOpen(ConfigurationWelcomeFragment.TAG)) {
                openTestConnectionFragment()
            } else if (supportFragmentManager.isOpen(ConfigurationTestConnectionFragment.TAG)) {
                openMainActivity()
            }
        } else if (menuItem.itemId == R.id.navigation_back) {
            if (supportFragmentManager.isOpen(ConfigurationTestConnectionFragment.TAG)) {
                openWelcomeFragment()
            }
        }
    }

    /**
     * Opens the main activity.
     */
    private fun openMainActivity() {
        val intent = Intent(this, MainActivity::class.java)
        intent.flags = intent.flags or Intent.FLAG_ACTIVITY_NO_HISTORY
        startActivity(intent)
        finish()
    }

    /**
     * Opens the welcome fragment.
     */
    private fun openWelcomeFragment() {
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.fragment_container_configurationPage, ConfigurationWelcomeFragment(), ConfigurationWelcomeFragment.TAG)
        transaction.commitNow()
    }

    /**
     * Opens the test connection fragment.
     */
    private fun openTestConnectionFragment() {
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.fragment_container_configurationPage, ConfigurationTestConnectionFragment(), ConfigurationTestConnectionFragment.TAG)
        transaction.commitNow()
    }
}
