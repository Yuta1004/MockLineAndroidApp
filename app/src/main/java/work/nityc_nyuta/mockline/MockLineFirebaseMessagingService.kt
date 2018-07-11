package work.nityc_nyuta.mockline

import android.content.Intent
import android.util.Log
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class MockLineFirebaseMessagingService: FirebaseMessagingService(){

    override fun onMessageReceived(remote_message: RemoteMessage?) {
        Log.d("Firebase-service", "Receive from: " + remote_message!!.from)
        Log.d("Firebase-service", "Receive data: " + remote_message.data)
    }
}