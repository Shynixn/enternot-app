package at.jku.enternot.ui

import android.webkit.*
import kotlinx.android.synthetic.main.activity_main.*
import android.webkit.HttpAuthHandler
import android.webkit.WebView
import android.widget.Toast
import at.jku.enternot.MainActivity
import at.jku.enternot.entity.Configuration

class CustomWebClient(private var context: MainActivity?, private val configuration: Configuration) : WebViewClient(), AutoCloseable {
    private var reConnectionAttempts = 0
    private var hasTriedAuthentication = false

    /**
     * Loads the webClient url on start.
     */
    init {
        context!!.webview.loadUrl(configuration.hostname + "/camera/stream")
    }

    /**
     * Gets called from the webview when a http authentication is required.
     */
    override fun onReceivedHttpAuthRequest(view: WebView, handler: HttpAuthHandler, host: String, realm: String) {
        if (!hasTriedAuthentication) {
            hasTriedAuthentication = true
            handler.proceed(configuration.username, configuration.password)
        } else {
            Toast.makeText(context, "Invalid Authentication Credentials.", Toast.LENGTH_LONG).show()
        }
    }

    /**
     * Gets called from the webview when an error gets received. Has to be used for compatibility issues with API 21.
     */
    override fun onReceivedError(view: WebView, errorCode: Int, description: String, failingUrl: String) {
        if (reConnectionAttempts < 10) {
            context?.webview?.reload()
            reConnectionAttempts++;
        }
    }

    /**
     * Remove the context reference otherwise the activity stacks.
     */
    override fun close() {
        context = null
    }
}