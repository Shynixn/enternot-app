package at.jku.enternot.service

import at.jku.enternot.contract.ConnectionService
import java.io.IOException

class ConnectionServiceIml(baseURL: String) : ConnectionService {

    /**
     * Sends a post request to the given relative [url] with the optional [item] as payload.
     * @throws [IOException] when the request to the server fails.
     */
    @Throws(IOException::class)
    override fun <T> post(url: String, item: T?): Int {
        Thread.sleep(5000)
        throw IOException("Please implement the connection stuff.")
    }
}