package at.jku.enternot

import android.arch.lifecycle.Observer
import android.content.Intent
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.CompoundButton
import android.widget.Toast
import at.jku.enternot.entity.SirenBlinkingState
import at.jku.enternot.extension.uiThreadLater
import at.jku.enternot.ui.CustomWebClient
import at.jku.enternot.viewmodel.MainActivityViewModelImpl
import kotlinx.android.synthetic.main.activity_main.*
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
import org.koin.android.architecture.ext.viewModel

class MainActivity : AppCompatActivity() {
    private val logTag: String = MainActivity::class.java.simpleName
    private val mainActivityViewModel: MainActivityViewModelImpl by viewModel()
    private var cacheWebClient: CustomWebClient? = null;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        toolbar.setTitleTextColor(ContextCompat.getColor(this, android.R.color.white))
        setSupportActionBar(toolbar)

        @Suppress("PLUGIN_WARNING")
        if (isInPortrait()) {
            toggle_button_voice.setOnCheckedChangeListener(this::onVoiceCheckChange)
            toggle_button_move_camera.setOnCheckedChangeListener(this::onCameraMoveCheckChange)
            button_siren.setOnClickListener(this::onSirenClick)
        }

        mainActivityViewModel.getProgressingState().observe(this, Observer { isProgressing ->
            if (isProgressing!!) {
                progressbar_load_mainPage.visibility = View.VISIBLE
            } else {
                progressbar_load_mainPage.visibility = View.GONE
            }
        })

        mainActivityViewModel.getConfiguration().observe(this, Observer { config ->
            if (config != null) {
                cacheWebClient = CustomWebClient(this, config)
                webview.webViewClient = cacheWebClient
            }
        })

        mainActivityViewModel.getSirenButtonState().observe(this, Observer { isEnabled ->
            button_siren.isEnabled = isEnabled!!
        })
        mainActivityViewModel.getSirenButtonState().observe(this, Observer { isEnabled ->
            button_siren.isEnabled = isEnabled!!
        })

        mainActivityViewModel.getSirenBlinkingState().observe(this, Observer { blinkingState ->
            if (blinkingState == SirenBlinkingState.BLINK) {
                button_siren.setBackgroundColor(ContextCompat.getColor(this, android.R.color.holo_red_dark))
                doAsync {
                    uiThreadLater({ context ->
                        if (mainActivityViewModel.getSirenBlinkingState().value != SirenBlinkingState.DISABLED) {
                            mainActivityViewModel.getSirenBlinkingState().value = SirenBlinkingState.BLINK_OFF
                        } else {
                            button_siren.setBackgroundColor(ContextCompat.getColor(context, android.R.color.holo_red_dark))
                        }
                    }, 500)
                }
            } else if (blinkingState == SirenBlinkingState.BLINK_OFF) {
                button_siren.setBackgroundColor(ContextCompat.getColor(this, android.R.color.holo_orange_dark))
                doAsync {
                    uiThreadLater({ context ->
                        if (mainActivityViewModel.getSirenBlinkingState().value != SirenBlinkingState.DISABLED) {
                            mainActivityViewModel.getSirenBlinkingState().value = SirenBlinkingState.BLINK
                        } else {
                            button_siren.setBackgroundColor(ContextCompat.getColor(context, android.R.color.holo_red_dark))
                        }
                    }, 500)
                }
            }
        })
    }

    override fun onResume() {
        super.onResume()
        // Sample Play Video code

        // Sample Play Video Code.

        mainActivityViewModel.getProgressingState().observe(this, Observer { isProgressing ->
            if (isProgressing!!) {
                progressbar_load_mainPage.visibility = View.VISIBLE
            } else {
                progressbar_load_mainPage.visibility = View.GONE
            }
        })

        mainActivityViewModel.getCameraMovementData().observe(this, Observer {
            // TODO: Send data to the raspberry pi
            val (x, y, z) = it ?: Triple(0, 0, 0)
            Log.i(logTag, "Accelerometer Axis: x=$x, y=$y, z=$z")
        })
    }

    override fun onDestroy() {
        super.onDestroy()

        // Cleanup webview.
        if (cacheWebClient != null) {
            cacheWebClient!!.close()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.action_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        R.id.action_settings -> {
            val builder = AlertDialog.Builder(this, R.style.AlertDialogCustom)
            builder.setTitle("Reset settings?")
                    .setMessage("This will disconnect you from your server completely and reset all settings.")
                    .setPositiveButton("ACCEPT", { _, _ ->
                        val config = mainActivityViewModel.getConfiguration().value!!
                        config.configured = false
                        this.mainActivityViewModel.saveConfiguration(config)
                        finish()
                        val intent = Intent(this, ConfigurationActivity::class.java)
                        intent.flags = intent.flags or Intent.FLAG_ACTIVITY_NO_HISTORY
                        startActivity(intent)
                    })
                    .setNegativeButton("CANCEL", { _, _ ->
                    })
            builder.create().show()
            true
        }
        R.id.action_camera_move_calibration -> {
            showCalibrationDialog()
            true
        }
        R.id.action_voice -> {
            item.isChecked = !item.isChecked
            true
        }
        R.id.action_camera_move -> {
            item.isChecked = !item.isChecked
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

    private fun onCameraMoveCheckChange(button: CompoundButton, isChecked: Boolean) {
        mainActivityViewModel.enableCameraMovement(isChecked)
    }

    /**
     * When the view gets clicked the app starts the siren.
     */
    private fun onSirenClick(view: View) {
        mainActivityViewModel.getProgressingState().value = true
        mainActivityViewModel.getSirenButtonState().value = false
        doAsync {
            val statusCode = mainActivityViewModel.playSiren()
            uiThread { context ->
                when (statusCode) {
                    200 -> {
                        if (mainActivityViewModel.getSirenBlinkingState().value == null || mainActivityViewModel.getSirenBlinkingState().value == SirenBlinkingState.DISABLED) {
                            mainActivityViewModel.getSirenBlinkingState().value = SirenBlinkingState.BLINK
                        }

                        Toast.makeText(context, "Siren started", Toast.LENGTH_LONG).show()

                        uiThreadLater({
                            mainActivityViewModel.getSirenBlinkingState().value = SirenBlinkingState.DISABLED
                            mainActivityViewModel.getSirenButtonState().value = true
                        }, 15000)
                    }
                    401 -> {
                        mainActivityViewModel.getSirenButtonState().value = true
                        Toast.makeText(context, "Stored username or password is invalid", Toast.LENGTH_LONG).show()
                    }
                    else -> {
                        mainActivityViewModel.getSirenButtonState().value = true
                        Toast.makeText(context, "Cannot connect to the server", Toast.LENGTH_LONG).show()
                    }
                }

                mainActivityViewModel.getProgressingState().value = false
            }
        }
    }

    private fun isInPortrait(): Boolean =
            this.resources.configuration.orientation == android.content.res.Configuration.ORIENTATION_PORTRAIT

    private fun showCalibrationDialog() {
        val calibrationDialog = CalibrationDialog()
        calibrationDialog.onStartCalibration {
            progress_bar_calibration.visibility = View.VISIBLE
        }
        calibrationDialog.onFinished {
            progress_bar_calibration.visibility = View.INVISIBLE
        }
        calibrationDialog.show(fragmentManager, "calibration")
    }
}
