package at.jku.enternot.service

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.support.v4.content.ContextCompat

class BootDeviceServiceImpl : BroadcastReceiver() {
    /**
     * This method is called when the BroadcastReceiver is receiving an Intent
     * broadcast.  During this time you can use the other methods on
     * BroadcastReceiver to view/modify the current result values.  This method
     * is always called within the main thread of its process, unless you
     * @param context The Context in which the receiver is running.
     * @param intent The Intent being received.
     */
    override fun onReceive(context: Context, intent: Intent) {
        val myIntent = Intent(context, GPSServiceImpl::class.java)
        ContextCompat.startForegroundService(context, myIntent)
    }
}