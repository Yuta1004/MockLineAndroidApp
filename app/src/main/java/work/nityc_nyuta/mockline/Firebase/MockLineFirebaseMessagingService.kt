package work.nityc_nyuta.mockline.Firebase

import android.app.Activity
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.renderscript.Script
import android.support.v4.app.NotificationCompat
import android.support.v4.content.LocalBroadcastManager
import android.util.Log
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import work.nityc_nyuta.mockline.Activities.MyActivityLifeCycleCallbacks
import work.nityc_nyuta.mockline.Activities.TalkActivity
import work.nityc_nyuta.mockline.Database.TalkroomDatabaseHelper
import work.nityc_nyuta.mockline.R
import work.nityc_nyuta.mockline.ServerConncection.ServerConnectUserData
import kotlin.concurrent.thread

class MockLineFirebaseMessagingService: FirebaseMessagingService(){
    private var notifyBroadCaster: LocalBroadcastManager? = null

    override fun onCreate() {
        super.onCreate()

        notifyBroadCaster = LocalBroadcastManager.getInstance(applicationContext)
    }

    override fun onMessageReceived(remote_message: RemoteMessage?) {
        // 送信者確認 & 送信データ確認
        if(remote_message!!.from != "1048318911529" || remote_message.data == null){
            return
        }

        val receiveData = remote_message.data!!
        val talkroomId = receiveData["talkroom_id"]!!
        val talkroomName = receiveData["talkroom_name"]!!
        val senderId = receiveData["sender_id"]!!
        val message = receiveData["message"]!!
        val timestamp = receiveData["timestamp"]!!.toLong()

        // TalkroomActivityへ新規メッセージが来たことを通知
        notifyReceiveNewMessage(talkroomId, senderId, message, timestamp)

        // DB保存
        val databaseHelper = TalkroomDatabaseHelper(applicationContext)
        databaseHelper.addTalkHistory(talkroomId, senderId, message, timestamp)
        databaseHelper.close()

        // 現在表示中の画面がトークルームで，かつIDがtalkroomIDと同じ出ないなら通知を発行
        if(talkroomId != MyActivityLifeCycleCallbacks.nowDisplayTalkroomID){
            makeNotification(talkroomId, talkroomName, message)
        }
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

    private fun makeNotification(talkroomId: String, talkroomName: String, message: String){
        // 通知のビルダーと各種設定
        val builder = NotificationCompat.Builder(applicationContext, talkroomId)
        builder.setSmallIcon(R.drawable.ic_person_black_24dp)
        builder.setContentTitle(talkroomName)
        builder.setContentText(message)

        // 通知が押された時のインテント
        val talkIntent = Intent(applicationContext, TalkActivity::class.java)
        talkIntent.putExtra("id", talkroomId)
        talkIntent.putExtra("name", talkroomName)
        val talkPendingIntent =
                PendingIntent.getActivity(applicationContext, 0, talkIntent, PendingIntent.FLAG_CANCEL_CURRENT)
        builder.setContentIntent(talkPendingIntent)
        builder.setAutoCancel(true)

        // 通知発行
        val notify = builder.build()
        val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.notify(talkroomId, 0, notify)
    }
}