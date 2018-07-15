package work.nityc_nyuta.mockline.Firebase

import android.util.Log
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import work.nityc_nyuta.mockline.ServerConncection.ServerConnectUserData
import kotlin.concurrent.thread

class MockLineFirebaseMessagingService: FirebaseMessagingService(){

    override fun onMessageReceived(remote_message: RemoteMessage?) {
        Log.d("Firebase-service", "Receive from: " + remote_message!!.from)
        Log.d("Firebase-service", "Receive data: " + remote_message.data)
    }

    override fun onNewToken(token: String?) {
        if(token != null) {
            Log.d("Firebase-Token", token)

            // サーバに通知トークンを送信
            thread{
                ServerConnectUserData().sendUserData("", token, "", "", "update_user")
            }
        }
    }
}