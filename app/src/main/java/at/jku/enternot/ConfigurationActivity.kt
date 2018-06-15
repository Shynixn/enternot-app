package at.jku.enternot

import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import at.jku.enternot.extension.isOpen
import kotlinx.android.synthetic.main.activity_configuration.*

class ConfigurationActivity : AppCompatActivity() {
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

        if (supportFragmentManager.findFragmentByTag(ConfigurationWelcomeFragment.TAG) == null && supportFragmentManager.findFragmentByTag(ConfigurationTestConnectionFragment.TAG) == null) {
            openWelcomeFragment()
        }
    }

    /**
     * Gets called when the user clicks next or back on the bottom navigation bar and changes the current fragment.
     */
    private val bottomNavigationListener = BottomNavigationView.OnNavigationItemReselectedListener { menuItem ->
        if (menuItem.itemId == R.id.navigation_next) {

            if (supportFragmentManager.isOpen(ConfigurationWelcomeFragment.TAG)) {
                openTestConnectionFragment()
            }

            if (supportFragmentManager.isOpen(ConfigurationTestConnectionFragment.TAG)) {
                //   openTestConnectionFragment()
            }
        } else if (menuItem.itemId == R.id.navigation_back) {
            if (supportFragmentManager.isOpen(ConfigurationTestConnectionFragment.TAG)) {
                openWelcomeFragment()
            }
        }
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
