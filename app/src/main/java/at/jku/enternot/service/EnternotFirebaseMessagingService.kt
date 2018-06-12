package at.jku.enternot.service

import android.util.Log
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class EnternotFirebaseMessagingService : FirebaseMessagingService() {

    private val LOG_TAG = javaClass.simpleName

    override fun onMessageReceived(remoteMessage: RemoteMessage?) {
        super.onMessageReceived(remoteMessage)
        Log.d(LOG_TAG, remoteMessage?.notification?.body)
    }
}