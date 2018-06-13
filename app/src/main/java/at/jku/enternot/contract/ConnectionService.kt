package at.jku.enternot.contract

import android.content.Context
import at.jku.enternot.entity.Response
import java.io.IOException

interface ConnectionService {
    /**
     * Sends a post request to the given relative [url] with the optional [item] as payload.
     * Returns the status code of the http request.
     * @throws [IOException] when the request to the server fails.
     */
    @Throws(IOException::class)
    fun <T> post(url: String, context: Context, item: T? = null): Int

    /**
     * Sends a get request to the given relative [url] with the optional url [parameters].
     * Returns the response content as object of class T. Returns a response with the status code and optional content payload.+
     * @throws [IOException] when the request to the server fails.
     */
    @Throws(IOException::class)
    fun <T> get(url: String, clazz: Class<T>, context: Context, parameters: Map<String, String>? = null): Response<T>
}
