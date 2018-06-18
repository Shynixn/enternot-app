package at.jku.enternot

import android.Manifest
import android.arch.lifecycle.Observer
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.support.v4.app.ActivityCompat
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
import at.jku.enternot.extension.isInPortrait
import at.jku.enternot.extension.uiThreadLater
import at.jku.enternot.service.GPSServiceImpl
import at.jku.enternot.ui.CustomWebClient
import at.jku.enternot.viewmodel.MainActivityViewModelImpl
import kotlinx.android.synthetic.main.activity_main.*
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
import org.koin.android.architecture.ext.viewModel

class MainActivity : AppCompatActivity() {
    private val requestCodeAccessGPS = 50
    private val PERMISSION_RECORD_AUDIO = 0
    private val logTag: String = MainActivity::class.java.simpleName
    private val mainActivityViewModel: MainActivityViewModelImpl by viewModel()
    private var cacheWebClient: CustomWebClient? = null

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

        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.ACCESS_COARSE_LOCATION), requestCodeAccessGPS)
        }

        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION), requestCodeAccessGPS)
        }

        val gpsIntent = Intent(this, GPSServiceImpl::class.java)
        this.startService(gpsIntent)

        mainActivityViewModel.getConfiguration().observe(this, Observer { config ->
            if (config != null) {
                cacheWebClient = CustomWebClient(this, config)
                webview.webViewClient = cacheWebClient
            }
        })

        mainActivityViewModel.getSirenState().observe(this, Observer { isEnabled ->
            if (isEnabled!!) {
                if (isInPortrait()) {
                    @Suppress("PLUGIN_WARNING")
                    button_siren.text = "SIREN STOP"
                }
            } else {
                if (isInPortrait()) {
                    @Suppress("PLUGIN_WARNING")
                    button_siren.text = "SIREN"
                }
            }
        })
    }

    override fun onResume() {
        super.onResume()
        // Sample Play Video code

        // Sample Play Video Code.

        mainActivityViewModel.getCameraMovementData().observe(this, Observer {
            // TODO: Send data to the raspberry pi
            val (x, y, z) = it ?: Triple(0, 0, 0)
            Log.i(logTag, "Accelerometer Axis: x=$x, y=$y, z=$z")
        })
    }

    override fun onDestroy() {
        super.onDestroy()

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
            showResetSettingsDialog()
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
        R.id.action_siren -> {
            onSirenClick()
            true
        }
        else -> {
            super.onOptionsItemSelected(item)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        when (requestCode) {
            PERMISSION_RECORD_AUDIO -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    mainActivityViewModel.enableVoiceRecording(true)
                    toggle_button_voice?.isChecked = true
                }
            }
        }
    }

    private fun onVoiceCheckChange(button: CompoundButton, isChecked: Boolean) {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    arrayOf(Manifest.permission.RECORD_AUDIO), PERMISSION_RECORD_AUDIO)
            button.isChecked = false
        } else {
            mainActivityViewModel.enableVoiceRecording(isChecked)
        }
    }

    private fun onCameraMoveCheckChange(button: CompoundButton, isChecked: Boolean) {
        mainActivityViewModel.enableCameraMovement(isChecked)
    }

    /**
     * When the view gets clicked the app starts the siren.
     */
    private fun onSirenClick(view: View? = null) {
        if (isInPortrait()) {
            @Suppress("PLUGIN_WARNING")
            button_siren.isEnabled = false
        }

        if (!mainActivityViewModel.getSirenState().value!!) {
            doAsync {
                val statusCode = mainActivityViewModel.playSiren()
                uiThread { context ->
                    when (statusCode) {
                        200 -> {
                            Toast.makeText(context, "Siren started", Toast.LENGTH_LONG).show()
                            mainActivityViewModel.getSirenState().value = true
                        }
                        401 -> {
                            Toast.makeText(context, "Stored username or password is invalid", Toast.LENGTH_LONG).show()
                        }
                        else -> {
                            Toast.makeText(context, "Cannot connect to the server", Toast.LENGTH_LONG).show()
                        }
                    }

                    if (isInPortrait()) {
                        @Suppress("PLUGIN_WARNING")
                        context.button_siren.isEnabled = true
                    }
                }
            }
        } else {
            doAsync {
                val statusCode = mainActivityViewModel.stopSiren()
                uiThread { context ->
                    when (statusCode) {
                        200 -> {
                            Toast.makeText(context, "Siren stopped", Toast.LENGTH_LONG).show()
                            mainActivityViewModel.getSirenState().value = false
                        }
                        401 -> {
                            Toast.makeText(context, "Stored username or password is invalid", Toast.LENGTH_LONG).show()
                        }
                        else -> {
                            Toast.makeText(context, "Cannot connect to the server", Toast.LENGTH_LONG).show()
                        }
                    }

                    if (isInPortrait()) {
                        @Suppress("PLUGIN_WARNING")
                        context.button_siren.isEnabled = true
                    }
                }
            }
        }
    }

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

    /**
     * Displays the reset settings dialog on the users screen.
     */
    private fun showResetSettingsDialog() {
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
    }
}
