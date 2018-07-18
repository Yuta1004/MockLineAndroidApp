package work.nityc_nyuta.mockline.Activities

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.support.design.widget.TextInputEditText
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.text.Editable
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_talk.*
import kotlinx.android.synthetic.main.talk_holder_me.*
import work.nityc_nyuta.mockline.Adapters.TalkData
import work.nityc_nyuta.mockline.Adapters.TalkRecycleViewAdapter
import work.nityc_nyuta.mockline.Database.TalkroomDatabaseHelper
import work.nityc_nyuta.mockline.R
import work.nityc_nyuta.mockline.ServerConncection.ServerConnectTalkData
import javax.net.ServerSocketFactory
import javax.net.ssl.HandshakeCompletedListener
import kotlin.concurrent.thread

class TalkActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_talk)

        // トークルームID取得
        val senderIntent = intent
        val talkroomId = senderIntent.getStringExtra("id")
        val talkroomName = senderIntent.getStringExtra("name")
        title = talkroomName

        // RecycleViewの設定
        val chatRecycleView = findViewById<RecyclerView>(R.id.chat_recycle_view)
        chatRecycleView.setHasFixedSize(true)
        chatRecycleView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)

        // adapter設定
        val adapter = TalkRecycleViewAdapter(createTakeList(talkroomId, talkroomName, listOf("")))
        chatRecycleView.adapter = adapter
        chatRecycleView.scrollToPosition(adapter.itemCount-1)

        // 送信ボタンが押されたら
        findViewById<Button>(R.id.send_button).setOnClickListener {
            // 入力文字取得 -> 空判定
            val inpText = findViewById<TextInputEditText>(R.id.message_inp).text.toString()
            if(inpText == ""){ return@setOnClickListener }
            findViewById<TextInputEditText>(R.id.message_inp).setText("")

            val userID = FirebaseAuth.getInstance().currentUser!!.email!!
            val timestamp = System.currentTimeMillis()

            sendTalkDataAndSaveDB(talkroomId, userID, inpText, timestamp)

            // adapterとRecyecleViewを更新
            adapter.addTalkList(userID, inpText, timestamp)
            adapter.notifyDataSetChanged()
            chatRecycleView.smoothScrollToPosition(adapter.itemCount-1)
        }
    }

    private fun sendTalkDataAndSaveDB(talkroomId: String, userId: String, message: String, timestamp: Long){
        val handler = Handler()

        // 通信をするためスレッドを建てる
        thread {
            val result = ServerConnectTalkData().sendTalkData(talkroomId, message, timestamp)

            // 通信成功の場合はDB保存
            if (result) {
                val databaseHelper = TalkroomDatabaseHelper()
                databaseHelper.addTalkHistory(talkroomId, userId, message, timestamp)
                databaseHelper.close()
            }else{
                handler.post {
                    Toast.makeText(this, "メッセージの送信に失敗しました", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    // トーク履歴を生成する
    private fun createTakeList(talkroomId: String, talkroomName: String, talkroomUserList: List<String>): MutableList<TalkData>{
        val databaseHelper = TalkroomDatabaseHelper()

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
