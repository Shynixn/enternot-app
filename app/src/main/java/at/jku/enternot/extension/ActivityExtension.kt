package at.jku.enternot.extension

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.support.v4.app.FragmentManager
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

/**
 * Returns if the current rotation is landscape or portrait.
 */
fun Context.isInPortrait(): Boolean {
    return this.resources.configuration.orientation == android.content.res.Configuration.ORIENTATION_PORTRAIT
}

/**
 * Returns if this fragment is visible and displayed.
 */
fun FragmentManager.isOpen(tagName: String): Boolean {
    val fragment = this.findFragmentByTag(tagName)
    if (fragment != null && fragment.isVisible) {
        return true
    }

    return false
}