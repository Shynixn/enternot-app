package at.jku.enternot.service

import at.jku.enternot.contract.ConfigurationService
import at.jku.enternot.contract.ConnectionService
import com.android.volley.Request
import com.android.volley.Response
import java.io.IOException
import com.android.volley.VolleyError
import org.json.JSONObject
import com.android.volley.toolbox.JsonObjectRequest
import com.google.gson.Gson
import com.google.gson.JsonObject


class ConnectionServiceIml(private val configurationService : ConfigurationService) : ConnectionService {

    /**
     * Sends a post request to the given relative [url] with the optional [item] as payload.
     * @throws [IOException] when the request to the server fails.
     */
    @Throws(IOException::class)
    override fun <T> post(url: String, item: T?): Int {
        val url = "http://my-json-feed"

        val jsonObjectRequest = JsonObjectRequest(Request.Method.POST, url,JSONObject(Gson().toJson(item)),
                Response.Listener { response ->
                    textView.text = "Response: %s".format(response.toString())
                },
                Response.ErrorListener { error ->
                    // TODO: Handle error
                }
        )

// Access the RequestQueue through your singleton class.
        MySingleton.getInstance(this).addToRequestQueue(jsonObjectRequest)
    }
}