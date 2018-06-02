package at.jku.enternot

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
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
                    Toast.makeText(context, "Cannot connect to serverr!", Toast.LENGTH_LONG).show()
                }
            }
        }
    }
}
