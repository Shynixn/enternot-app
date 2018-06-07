package at.jku.enternot

import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.CompoundButton
import kotlinx.android.synthetic.main.activity_main.*
import android.util.Log
import android.widget.Toast
import at.jku.enternot.viewmodel.MainActivityViewModelImpl
import com.google.android.exoplayer2.*
import com.google.android.exoplayer2.source.TrackGroupArray
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.google.android.exoplayer2.trackselection.TrackSelectionArray
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.android.exoplayer2.util.Util
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
import org.koin.android.architecture.ext.viewModel
import java.io.IOException
import com.google.android.exoplayer2.util.Util.getUserAgent
import com.google.android.exoplayer2.source.ExtractorMediaSource
import com.google.android.exoplayer2.source.MediaSource
import android.os.Looper
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory


class MainActivity : AppCompatActivity() {
    private val LOG_TAG: String = MainActivity::class.java.simpleName
    private val mainActivityViewModel: MainActivityViewModelImpl by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        toggle_button_voice.setOnCheckedChangeListener(this::onVoiceCheckChange)
        toggle_button_move_camera.setOnCheckedChangeListener(this::onCamaeraMoveCheckChange)
        button_siren.setOnClickListener(this::onSirenClick)


        // Sample Play Video code

        val sourceuri = "https://www.w3schools.com/tags/mov_bbb.mp4";

        val bandwidthMeter = DefaultBandwidthMeter();
        val dataSourceFactory = DefaultHttpDataSourceFactory(Util.getUserAgent(this, "exoplayer2example"))
        dataSourceFactory.defaultRequestProperties.set("basic", "asdasdsad")

        val videoTrackSelectionFactory = AdaptiveTrackSelection.Factory(bandwidthMeter);
        val trackSelector = DefaultTrackSelector(videoTrackSelectionFactory);

        val player = ExoPlayerFactory.newSimpleInstance(this, trackSelector);
        player.addListener(SomeKotlinListenr())
        view_streaming.player = player


        val uri = Uri.parse(sourceuri)
        val mediaSource = ExtractorMediaSource.Factory(dataSourceFactory).createMediaSource(uri)

        player.prepare(mediaSource);
        player.setPlayWhenReady(true);

        // Sample Play Video Code.

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
        inflater.inflate(R.menu.settings, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
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
