package at.jku.enternot.extension

import android.content.Context
import android.os.Handler
import android.os.Looper
import org.jetbrains.anko.AnkoAsyncContext
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread

/**
 * Handles a ui task later regardless of the current state of the activity. Uses anko internally.
 */
fun <T> AnkoAsyncContext<T>.uiThreadLater(f: (T) -> Unit, milliSeconds: Long) {
    val handler = Handler(Looper.getMainLooper())
    handler.postDelayed({
        uiThread(f)
    }, milliSeconds)
}

/**
 * Handles a ui task later regardless of the current state of the activity. Uses anko internally.
 */
fun <T> Context.uiThreadLater(f: (T) -> Unit, milliSeconds: Long) {
    doAsync {
        uiThreadLater(f, milliSeconds)
    }
}