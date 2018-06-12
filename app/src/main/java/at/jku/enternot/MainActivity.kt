package at.jku.enternot

import android.arch.lifecycle.Observer
import android.content.res.Configuration
import android.net.Uri
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.CompoundButton
import android.widget.Toast
import at.jku.enternot.entity.SirenBlinkingState
import at.jku.enternot.extension.uiThreadLater
import at.jku.enternot.viewmodel.MainActivityViewModelImpl
import com.google.android.exoplayer2.*
import com.google.android.exoplayer2.source.ExtractorMediaSource
import com.google.android.exoplayer2.source.TrackGroupArray
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.google.android.exoplayer2.trackselection.TrackSelectionArray
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory
import com.google.android.exoplayer2.util.Util
import kotlinx.android.synthetic.main.activity_main.*
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
import org.koin.android.architecture.ext.viewModel


class MainActivity : AppCompatActivity() {
    private val logTag: String = MainActivity::class.java.simpleName
    private val mainActivityViewModel: MainActivityViewModelImpl by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        toolbar.setTitleTextColor(ContextCompat.getColor(this, android.R.color.white))
        setSupportActionBar(toolbar)

        @Suppress("PLUGIN_WARNING")
        if(isInPortrait()) {
            toggle_button_voice.setOnCheckedChangeListener(this::onVoiceCheckChange)
            toggle_button_move_camera.setOnCheckedChangeListener(this::onCameraMoveCheckChange)
            button_siren.setOnClickListener(this::onSirenClick)

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

        // Sample Play Video code

        val sourceuri = "https://www.w3schools.com/tags/mov_bbb.mp4"

        val bandwidthMeter = DefaultBandwidthMeter()
        val dataSourceFactory = DefaultHttpDataSourceFactory(Util.getUserAgent(this, "exoplayer2example"))
        dataSourceFactory.defaultRequestProperties.set("basic", "asdasdsad")

        val videoTrackSelectionFactory = AdaptiveTrackSelection.Factory(bandwidthMeter)
        val trackSelector = DefaultTrackSelector(videoTrackSelectionFactory)

        val player = ExoPlayerFactory.newSimpleInstance(this, trackSelector)
        player.addListener(SomeKotlinListenr())
        view_streaming.player = player


        val uri = Uri.parse(sourceuri)
        val mediaSource = ExtractorMediaSource.Factory(dataSourceFactory).createMediaSource(uri)

        player.prepare(mediaSource)
        player.playWhenReady = true

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

    private class SomeKotlinListenr : Player.EventListener {
        override fun onPlaybackParametersChanged(playbackParameters: PlaybackParameters?) {
        }

        override fun onSeekProcessed() {
        }

        override fun onTracksChanged(trackGroups: TrackGroupArray?, trackSelections: TrackSelectionArray?) {
        }

        override fun onPlayerError(error: ExoPlaybackException?) {
        }

        override fun onLoadingChanged(isLoading: Boolean) {
        }

        override fun onPositionDiscontinuity(reason: Int) {
        }

        override fun onRepeatModeChanged(repeatMode: Int) {
        }

        override fun onShuffleModeEnabledChanged(shuffleModeEnabled: Boolean) {
        }

        override fun onTimelineChanged(timeline: Timeline?, manifest: Any?, reason: Int) {
        }

        override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) {
        }

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.action_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        R.id.action_settings -> {
            // TODO: Implement show settings activity.
            Toast.makeText(this, "Not Implemented", Toast.LENGTH_SHORT).show()
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
            this.resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT

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
