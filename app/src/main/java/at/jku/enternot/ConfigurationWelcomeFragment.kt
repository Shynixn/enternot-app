package at.jku.enternot


import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.activity_configuration.*

class ConfigurationWelcomeFragment : Fragment() {
    companion object {
        const val TAG = "CONFIGURATIONWELCOME_FRAGMENT"
    }

    /**
     * Android override.
     */
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_welcome, container, false)
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

        activity.navigation_configuration.menu.getItem(0).isEnabled = false
        activity.navigation_configuration.menu.getItem(0).isChecked = false
        activity.navigation_configuration.menu.getItem(1).isEnabled = true
        activity.navigation_configuration.menu.getItem(1).isChecked = false
    }
}
