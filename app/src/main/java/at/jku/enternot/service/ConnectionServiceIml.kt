package at.jku.enternot.service

import android.content.Context
import android.util.Base64
import at.jku.enternot.contract.ConfigurationService
import at.jku.enternot.contract.ConnectionService
import at.jku.enternot.entity.Response
import com.google.gson.Gson
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL

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
                ?: throw IllegalArgumentException("Configuration cannot be null!")

        val hostnameURL = URL(configuration.hostname + url)
        val conn = hostnameURL.openConnection() as HttpURLConnection
        val basicAuth = "Basic " + String(Base64.encode((configuration.username + ":" + configuration.password).toByteArray(Charsets.UTF_8), Base64.DEFAULT))

        conn.setRequestProperty("Authorization", basicAuth)
        conn.connectTimeout = connectionTimeOut
        conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8")
        conn.requestMethod = "POST"

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
     * Returns the response content as object of class T. Returns a response with the status code and optional content payload.+
     * @throws [IOException] when the request to the server fails.
     */
    override fun <T> get(url: String, clazz: Class<T>, context: Context, parameters: Map<String, String>?): Response<T> {
        // As we only use a very small communication layer we do not need big data processing like Volley would
        // provide. A basic connection is enough
        val configuration = configurationService.getConfiguration(context)
                ?: throw IllegalArgumentException("Configuration cannot be null!")

        val hostnameURL = URL(configuration.hostname + url)
        val conn = hostnameURL.openConnection() as HttpURLConnection
        val basicAuth = "Basic " + String(Base64.encode((configuration.username + ":" + configuration.password).toByteArray(Charsets.UTF_8), Base64.DEFAULT))

        conn.setRequestProperty("Authorization", basicAuth)
        conn.connectTimeout = connectionTimeOut
        conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8")
        conn.requestMethod = "GET"
        val responseCode = conn.responseCode

        if (responseCode == 200) {
            conn.inputStream.use { inputStream ->
                val reader = inputStream.bufferedReader(Charsets.UTF_8)

                return if (clazz == String::class.java) {
                    Response(responseCode, reader.readText() as T)
                } else {
                    Response(responseCode, Gson().fromJson(reader, clazz))
                }
            }
        }

        conn.disconnect()

        return Response(responseCode)
    }
}