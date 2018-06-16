package at.jku.enternot.service

import android.app.IntentService
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.util.Log
import java.io.IOException

class GPSServiceImpl : IntentService("GPS-SERVICE") {
    private val logTag: String = GPSServiceImpl::class.java.simpleName
    private val locationCheckInterval = 1000 * 60 * 10L
    private val connectionServiceIml = ConnectionServiceIml(ConfigurationServiceImpl())

    companion object {
        var initialized = false
    }

    /**
     * This method is invoked on the worker thread with a request to process.
     * Only one Intent is processed at a time, but the processing happens on a
     * worker thread that runs independently from other application logic.
     * So, if this code takes a long time, it will hold up other requests to
     * the same IntentService, but it will not hold up anything else.
     * When all requests have been handled, the IntentService stops itself,
     * so you should not call [.stopSelf].
     *
     * @param intent The value passed to [               ][android.content.Context.startService].
     * This may be null if the service is being restarted after
     * its process has gone away; see
     * [android.app.Service.onStartCommand]
     * for details.
     */
    override fun onHandleIntent(intent: Intent) {
        if (initialized) {
            return
        }

        Log.i(logTag, "Trying to enable location listening.." + hashCode())

        if (Build.VERSION.SDK_INT >= 23 &&
                (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED ||
                        ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)) {
            return
        }

        val locationManager = this.getSystemService(Context.LOCATION_SERVICE) as LocationManager

        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, locationCheckInterval, 0F, locationListener)
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, locationCheckInterval, 0F, locationListener)

        val location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)

        if (location != null) {
            locationListener.onLocationChanged(location)
        }

        initialized = true

        Log.i(logTag, "Enabled location listening.")
    }

    private val locationListener = object : LocationListener {
        /**
         * Not Used.
         */
        override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {
        }

        /**
         * Not Used.
         */
        override fun onProviderEnabled(provider: String?) {
        }

        /**
         * Not Used.
         */
        override fun onProviderDisabled(provider: String?) {
        }

        /**
         * Called when the location has changed.
         *
         *
         *  There are no restrictions on the use of the supplied Location object.
         *
         * @param location The new location, as a Location object.
         */
        override fun onLocationChanged(location: Location) {
            try {
                val statusCode = connectionServiceIml.post("/location", applicationContext, at.jku.enternot.entity.Location(location.longitude, location.latitude))
                Log.i(logTag, "Updated position of user with StatusCode $statusCode.")
            } catch (e: IllegalArgumentException) {
                // Ignore if configuration is not valid.
            } catch (e: IOException) {
                Log.e(logTag, "Failed to send location updates.", e)
            }
        }
    }
}