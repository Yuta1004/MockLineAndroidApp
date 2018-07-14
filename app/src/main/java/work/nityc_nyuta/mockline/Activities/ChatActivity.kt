package work.nityc_nyuta.mockline.Activities

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import work.nityc_nyuta.mockline.Adapters.ChatData
import work.nityc_nyuta.mockline.Adapters.ChatRecycleViewAdapter
import work.nityc_nyuta.mockline.ConfigurationDataClass
import work.nityc_nyuta.mockline.R

class ChatActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        // トークルームID取得
        val senderIntent = intent
        val talkroomId = senderIntent.getStringExtra("id")
        title = talkroomId

        // RecycleViewの設定
        val chatRecycleView = findViewById<RecyclerView>(R.id.chat_recycle_view)
        chatRecycleView.setHasFixedSize(true)
        chatRecycleView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)

        val adapter = ChatRecycleViewAdapter(createChatList())
        chatRecycleView.adapter = adapter
        chatRecycleView.scrollToPosition(adapter.itemCount-1)
    }

    // チャット履歴を生成する(デバッグ用)
    fun createChatList(): List<ChatData>{
        val chatList = mutableListOf<ChatData>()

        val messageDummy = ConfigurationDataClass().nopoiKashi
        for(idx in 10..59){
            if(idx % 2 == 0) {
                chatList.add(
                        ChatData("guguru0014@gmail.com", "Yuta1004", messageDummy, " 10:" + idx.toString() + " ")
                )
            }else{
                chatList.add(
                        ChatData("guguru00142@gmail.com", "Chino1204", messageDummy, " 10:" + idx.toString() + " ")
                )
            }
        }

        return chatList.toList()
    }
}
