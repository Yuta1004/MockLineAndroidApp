package work.nityc_nyuta.mockline.Firebase

import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.renderscript.Script
import android.support.v4.content.LocalBroadcastManager
import android.util.Log
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import work.nityc_nyuta.mockline.Database.TalkroomDatabaseHelper
import work.nityc_nyuta.mockline.ServerConncection.ServerConnectUserData
import kotlin.concurrent.thread

class MockLineFirebaseMessagingService: FirebaseMessagingService(){
    private var notifyBroadCaster: LocalBroadcastManager? = null

    override fun onCreate() {
        super.onCreate()

        notifyBroadCaster = LocalBroadcastManager.getInstance(applicationContext)
    }

    override fun onMessageReceived(remote_message: RemoteMessage?) {
        if(remote_message!!.from != "1048318911529" || remote_message.data == null){
            return
        }

        val receiveData = remote_message.data!!
        val talkroomId = receiveData["talkroom_id"]!!
        val senderId = receiveData["sender_id"]!!
        val message = receiveData["message"]!!
        val timestamp = receiveData["timestamp"]!!.toLong()

        // TalkroomActivityへ新規メッセージが来たことを通知
        notifyReceiveNewMessage(talkroomId, senderId, message, timestamp)

        // DB保存
        val databaseHelper = TalkroomDatabaseHelper()
        databaseHelper.addTalkHistory(talkroomId, senderId, message, timestamp)
        databaseHelper.close()
    }

    override fun onNewToken(token: String?) {
        if (token != null) {
            Log.d("Firebase-Token", token)

            // サーバに通知トークンを送信
            thread {
                ServerConnectUserData().sendUserData("", token, "", "", "update_user")
            }
        }
    }

    // 起動中のActivityに新規データを受信したことを知らせる
    private fun notifyReceiveNewMessage(talkroomID: String, senderId: String, message: String, timestamp: Long){
        val sendIntent = Intent()
        sendIntent.putExtra("talkroomId", talkroomID)
        sendIntent.putExtra("senderId", senderId)
        sendIntent.putExtra("message", message)
        sendIntent.putExtra("timestamp", timestamp)
        sendIntent.action = "UpdateRecyclerView"

        notifyBroadCaster!!.sendBroadcast(sendIntent)
    }
}