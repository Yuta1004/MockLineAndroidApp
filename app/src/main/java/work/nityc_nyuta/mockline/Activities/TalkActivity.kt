package work.nityc_nyuta.mockline.Activities

import android.content.*
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.support.design.widget.TextInputEditText
import android.support.v4.content.LocalBroadcastManager
import android.support.v7.app.AlertDialog
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.text.Editable
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_talk.*
import kotlinx.android.synthetic.main.talk_holder_me.*
import work.nityc_nyuta.mockline.Adapters.TalkData
import work.nityc_nyuta.mockline.Adapters.TalkRecycleViewAdapter
import work.nityc_nyuta.mockline.Database.TalkroomDatabaseHelper
import work.nityc_nyuta.mockline.Fragments.TalkroomViewFragment
import work.nityc_nyuta.mockline.R
import work.nityc_nyuta.mockline.ServerConncection.ServerConnectTalkData
import work.nityc_nyuta.mockline.ServerConncection.ServerConnectTalkroomData
import javax.net.ServerSocketFactory
import javax.net.ssl.HandshakeCompletedListener
import kotlin.concurrent.thread
import kotlin.reflect.jvm.internal.impl.resolve.constants.LongValue

class TalkActivity : AppCompatActivity() {
    private var talkRecycleView: RecyclerView? = null
    private var talkRecycleViewAdapter: TalkRecycleViewAdapter? = null
    private var notifyBroadCastReceiver: BroadcastReceiver? = null
    private var talkroomId: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_talk)

        // トークルームID取得
        val senderIntent = intent
        talkroomId = senderIntent.getStringExtra("id")
        val talkroomName = senderIntent.getStringExtra("name")
        title = talkroomName

        // LocalBroadReceiverのリスナ
        // Firebase:onMessageReceiveからの情報を待機する
        notifyBroadCastReceiver = object : BroadcastReceiver(){
            override fun onReceive(context: Context?, intent: Intent?) {
                val talkroomIdReceive = intent!!.getStringExtra("talkroomId")
                val senderIdReceive = intent.getStringExtra("senderId")
                val messageReceive = intent.getStringExtra("message")
                val timestampReceive = intent.getLongExtra("timestamp", 0)

                if(talkroomId == talkroomIdReceive){
                    adapterAndRecycleViewUpdate(senderIdReceive, messageReceive, timestampReceive)
                }
            }
        }

        // LocalBroadReceiverを登録する
        val intentFilter = IntentFilter()
        intentFilter.addAction("UpdateRecyclerView")
        LocalBroadcastManager.getInstance(applicationContext).registerReceiver(notifyBroadCastReceiver!!, intentFilter)

        // RecycleViewの設定
        talkRecycleView = findViewById<RecyclerView>(R.id.chat_recycle_view)
        talkRecycleView!!.setHasFixedSize(true)
        talkRecycleView!!.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)

        // adapter設定
        talkRecycleViewAdapter = TalkRecycleViewAdapter(createTakeList(talkroomId, talkroomName, listOf("")))
        talkRecycleView!!.adapter = talkRecycleViewAdapter
        talkRecycleView!!.scrollToPosition(talkRecycleViewAdapter!!.itemCount-1)

        // 送信ボタンのリスナ
        findViewById<Button>(R.id.send_button).setOnClickListener {
            // 入力取得 -> 判定
            val inpText = findViewById<TextInputEditText>(R.id.message_inp).text.toString()
            if(inpText == ""){ return@setOnClickListener }
            findViewById<TextInputEditText>(R.id.message_inp).setText("")

            val userID = FirebaseAuth.getInstance().currentUser!!.email!!
            val timestamp = System.currentTimeMillis()

            // サーバへトーク内容を送信，DBへ保存，adapterとRecyecleViewを更新
            sendTalkDataAndSaveDB(talkroomId, userID, inpText, timestamp)
            adapterAndRecycleViewUpdate(userID, inpText, timestamp)
        }
    }

    private fun sendTalkDataAndSaveDB(talkroomId: String, userId: String, message: String, timestamp: Long){
        val handler = Handler()

        // 通信をするためスレッドを建てる
        thread {
            val result = ServerConnectTalkData().sendTalkData(talkroomId, message, timestamp)

            // 通信成功の場合はDB保存
            if (result) {
                val databaseHelper = TalkroomDatabaseHelper(this)
                databaseHelper.addTalkHistory(talkroomId, userId, message, timestamp)
                databaseHelper.close()
            }else{
                handler.post {
                    Toast.makeText(this, "メッセージの送信に失敗しました", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun adapterAndRecycleViewUpdate(userId: String, message: String, timestamp: Long){
        talkRecycleViewAdapter!!.addTalkList(userId, message, timestamp)
        talkRecycleViewAdapter!!.notifyDataSetChanged()
        talkRecycleView!!.smoothScrollToPosition(talkRecycleViewAdapter!!.itemCount-1)
    }

    // トーク履歴を生成する
    private fun createTakeList(talkroomId: String, talkroomName: String, talkroomUserList: List<String>): MutableList<TalkData>{
        val databaseHelper = TalkroomDatabaseHelper(this)

        // トークルームテーブルが存在しない場合はテーブル作成
        if(!databaseHelper.existenceTalkroom(talkroomId)){
            databaseHelper.makeTalkroom(talkroomId, talkroomName, talkroomUserList)
        }

        val talkList = mutableListOf<TalkData>()

        // トーク履歴取得
        val talkHistory = databaseHelper.getTalkHistory(talkroomId)
        if(talkHistory != null){
            for(talkData in talkHistory){
                talkList.add(TalkData(talkData.senderId, talkData.message, talkData.timestamp))
            }
        }

        databaseHelper.close()
        return talkList
    }

    override fun onDestroy() {
        super.onDestroy()

        // BroadCastReceiverを登録解除する
        LocalBroadcastManager.getInstance(applicationContext).unregisterReceiver(notifyBroadCastReceiver!!)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.talkactivity_option_menu, menu)

        return super.onCreateOptionsMenu(menu)
    }

    // オプションメニューがクリックされたら
    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when(item!!.itemId){
            R.id.invite_talkroom ->{
                Toast.makeText(this, "トークルーム招待", Toast.LENGTH_SHORT).show()
            }

            R.id.exit_talkroom -> {
                // 退出確認のダイアログを出す
                AlertDialog.Builder(this)
                        .setTitle("退出確認")
                        .setMessage("本当に退出してもよろしいですか？")

                        // 退出する
                        .setPositiveButton("OK"){ _, _ ->
                            val handler = Handler()
                            thread {
                                // サーバ通信
                                val result = ServerConnectTalkroomData().exitTalkroom(talkroomId)

                                // UI操作をするためハンドラを使う
                                handler.post {
                                    if (result) {
                                        Toast.makeText(this, "${title}を退出しました", Toast.LENGTH_SHORT).show()
                                    } else {
                                        Toast.makeText(this, "退出に失敗しました", Toast.LENGTH_SHORT).show()
                                    }

                                    // adapterをnullにしてTalkroomListの更新をかける
                                    TalkroomViewFragment.talkroomListAdapter = null
                                    finish()
                                }
                            }
                        }

                        // 退出しない(何もしない)
                        .setNegativeButton("CANCEL"){ _, _ -> }
                        .show()
            }
        }


        return super.onOptionsItemSelected(item)
    }

    // チャット履歴を生成する(デバッグ用)
//    private fun createChatList(): List<ChatData>{
//        val chatList = mutableListOf<ChatData>()
//
//        val messageDummy = ConfigurationDataClass().nopoiKashi
//        for(idx in 10..59){
//            if(idx % 2 == 0) {
//                chatList.add(
//                        ChatData("guguru0014@gmail.com", "Yuta1004", messageDummy, " 10:" + idx.toString() + " ")
//                )
//            }else{
//                chatList.add(
//                        ChatData("guguru00142@gmail.com", "Chino1204", messageDummy, " 10:" + idx.toString() + " ")
//                )
//            }
//        }
//
//        return chatList.toList()
//    }
}
