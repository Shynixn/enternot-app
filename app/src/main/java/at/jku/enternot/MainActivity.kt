package at.jku.enternot

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.CompoundButton
import kotlinx.android.synthetic.main.activity_main.*
import android.util.Log
import android.widget.Toast
import at.jku.enternot.viewmodel.MainActivityViewModelImpl
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
import org.koin.android.architecture.ext.viewModel
import java.io.IOException

class MainActivity : AppCompatActivity() {
    private val LOG_TAG: String = MainActivity::class.java.simpleName
    private val mainActivityViewModel: MainActivityViewModelImpl by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        toggle_button_voice.setOnCheckedChangeListener(this::onVoiceCheckChange)
        toggle_button_move_camera.setOnCheckedChangeListener(this::onCamaeraMoveCheckChange)
        button_siren.setOnClickListener(this::onSirenClick)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.settings, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem) = when(item.itemId) {
        R.id.action_settings -> {
            // TODO: Implement show settings activity.
            Toast.makeText(this, "Not Implemented", Toast.LENGTH_SHORT).show()
            true
        }
        else -> {
            super.onOptionsItemSelected(item)
        }
    }

    private fun onVoiceCheckChange(button: CompoundButton, isChecked: Boolean) {
        // TODO: Implement activate voice listener
        Toast.makeText(this, "Not Implemented", Toast.LENGTH_SHORT).show()
    }

    private fun onCamaeraMoveCheckChange(button: CompoundButton, isChecked: Boolean) {
        // TODO: Implement activate camera movement
        Toast.makeText(this, "Not Implemented", Toast.LENGTH_SHORT).show()
    }

    private fun onSirenClick(view: View) {
        // TODO: Implement activate siren
        Toast.makeText(this, "Not Implemented", Toast.LENGTH_SHORT).show()
    }

    /**
     * Plays the siren asynchronously.
     */
    private fun playSiren() {
        doAsync {
            var success = false
            try {
                mainActivityViewModel.playSiren()
                success = true
            } catch (e: IOException) {
                Log.e(LOG_TAG, "Failed to play the siren.", e)
            }

            // The uiThread call does only work when the context was not destroyed. Otherwise it gets ignored.
            uiThread { context ->
                if (success) {
                    // Create fancy animation
                } else {
                    Toast.makeText(context, "Cannot connect to server!", Toast.LENGTH_LONG).show()
                }
            }
        }
    }
}
