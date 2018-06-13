package at.jku.enternot.service

import android.content.Context
import at.jku.enternot.contract.ConfigurationService
import at.jku.enternot.contract.ConnectionService
import com.google.gson.Gson
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL
import java.nio.charset.Charset

class ConnectionServiceIml(private val configurationService: ConfigurationService) : ConnectionService {
    private val connectionTimeOut = 5000

    /**
     * Sends a post request to the given relative [url] with the optional [item] as payload.
     * Returns the status code of the http request.
     * @throws [IOException] when the request to the server fails.
     */
    override fun <T> post(url: String, context: Context, item: T?): Int {
        // As we only use a very small communication layer we do not need big data processing like Volley would
        // provide. A basic connection is enough
        val configuration = configurationService.getConfiguration(context)
        if (configuration == null) {
            throw IllegalArgumentException("Configuration cannot be null!")
        }

        val url = URL(configuration.hostname + url)
        val conn = url.openConnection() as HttpURLConnection

        conn.setConnectTimeout(connectionTimeOut)
        conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8")
        conn.setRequestMethod("POST")
        conn.outputStream.use { outputStream ->
            if (item != null) {
                outputStream.write(Gson().toJson(item)!!.toByteArray(Charsets.UTF_8))
            }
        }
        conn.disconnect()

        return conn.responseCode
    }

    /**
     * Sends a get request to the given relative [url] with the optional url [parameters].
     * Returns the response content as object of class T. When no content is requested [clazz] should be
     * [Int] to return the status code instead.
     */
    override fun <T> get(url: String, clazz : Class<T>, context: Context, parameters: Map<String, String>?): T {
        // As we only use a very small communication layer we do not need big data processing like Volley would
        // provide. A basic connection is enough
        val configuration = configurationService.getConfiguration(context)
        if (configuration == null) {
            throw IllegalArgumentException("Configuration cannot be null!")
        }

        var responseItem : Any?
        val url = URL(configuration.hostname + url)
        val conn = url.openConnection() as HttpURLConnection

        conn.setConnectTimeout(connectionTimeOut)
        conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8")
        conn.setRequestMethod("GET")
        responseItem = conn.responseCode

        conn.inputStream.use { inputStream ->
            val reader = inputStream.bufferedReader(Charsets.UTF_8)
            responseItem = Gson().fromJson(reader,clazz)
        }
        conn.disconnect()

        return responseItem as T
    }
}