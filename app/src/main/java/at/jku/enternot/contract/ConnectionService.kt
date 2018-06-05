package at.jku.enternot.contract

import java.io.IOException

interface ConnectionService {
    /**
     * Sends a post request to the given relative [url] with the optional [item] as payload.
     * Returns the status code of the http request.
     * @throws [IOException] when the request to the server fails.
     */
    @Throws(IOException::class)
    fun <T> post(url: String, item: T? = null): Int
}