package at.jku.enternot.service

import android.content.Context
import android.util.Base64
import android.util.Log
import at.jku.enternot.MainActivity
import at.jku.enternot.contract.ConfigurationService
import at.jku.enternot.contract.ConnectionService
import at.jku.enternot.entity.Response
import com.google.gson.Gson
import java.io.IOException
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL

class ConnectionServiceIml(private val configurationService: ConfigurationService) : ConnectionService {
    private val logTag: String = ConnectionService::class.java.simpleName
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
        conn.requestMethod = "POST"

        if (item is InputStream) {
            conn.setRequestProperty("Content-Type", "application/octet-stream")
        } else {
            conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8")
        }

        conn.outputStream.use { outputStream ->
            if (item != null) {
                val byteBuffer = ByteArray(1024)
                if (item is InputStream) {
                    try {
                        item.use {
                            while (true) {
                                val result = item.read(byteBuffer)
                                if (result == -1) {
                                    break
                                }
                                outputStream.write(byteBuffer)
                            }
                        }
                    } catch (e: IOException) {
                        Log.i(logTag, "Closed stream.")
                    }
                } else {
                    outputStream.write(Gson().toJson(item)!!.toByteArray(Charsets.UTF_8))
                }
            }
        }

        val responseCode = conn.responseCode

        conn.disconnect()

        return responseCode
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