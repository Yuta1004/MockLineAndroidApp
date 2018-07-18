package work.nityc_nyuta.mockline.Activities

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.widget.Button
import work.nityc_nyuta.mockline.Adapters.ChatData
import work.nityc_nyuta.mockline.Adapters.ChatRecycleViewAdapter
import work.nityc_nyuta.mockline.ConfigurationDataClass
import work.nityc_nyuta.mockline.Database.TalkroomDatabaseHelper
import work.nityc_nyuta.mockline.R

class ChatActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        // トークルームID取得
        val senderIntent = intent
        val talkroomId = senderIntent.getStringExtra("id")
        val talkroomName = senderIntent.getStringExtra("name")
        title = talkroomName

        // RecycleViewの設定
        val chatRecycleView = findViewById<RecyclerView>(R.id.chat_recycle_view)
        chatRecycleView.setHasFixedSize(true)
        chatRecycleView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)

        val adapter = ChatRecycleViewAdapter(createTakeList(talkroomId, talkroomName, listOf("")))
        chatRecycleView.adapter = adapter
        chatRecycleView.scrollToPosition(adapter.itemCount-1)

        // 送信ボタンが押されたら
        findViewById<Button>(R.id.send_button).setOnClickListener {
            // トークDBにトーク内容保存
            val databaseHelper = TalkroomDatabaseHelper()
            databaseHelper.addTalkHistory(talkroomId, "guguru0014@gmail.com", "テスト送信です", 0)
            databaseHelper.close()

            // adapterとRecyecleViewを更新
            adapter.addTalkList("guguru0014@gmail.com", "Yuta1004", "テスト送信です", "0")
            adapter.notifyDataSetChanged()
            chatRecycleView.smoothScrollToPosition(adapter.itemCount-1)
        }
    }

    // トーク履歴を生成する
    private fun createTakeList(talkroomId: String, talkroomName: String, talkroomUserList: List<String>): MutableList<ChatData>{
        val databaseHelper = TalkroomDatabaseHelper()

        // トークルームテーブルが存在しない場合はテーブル作成
        if(!databaseHelper.existenceTalkroom(talkroomId)){
            databaseHelper.makeTalkroom(talkroomId, talkroomName, talkroomUserList)
        }

        val talkList = mutableListOf<ChatData>()

        // トーク履歴取得
        val talkHistory = databaseHelper.getTalkHistory(talkroomId)
        if(talkHistory != null){
            for(talkData in talkHistory){
                talkList.add(ChatData(talkData.senderId, "TestUser", talkData.message, talkData.timestamp.toString()))
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
