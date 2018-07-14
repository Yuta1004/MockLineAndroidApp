package work.nityc_nyuta.mockline

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.widget.ListView

class ChatActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        val senderIntent = intent
        val talkroomId = senderIntent.getStringExtra("id")
        title = talkroomId

        val chatRecycleView = findViewById<RecyclerView>(R.id.chat_recycle_view)
        chatRecycleView.setHasFixedSize(true)
        chatRecycleView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)

        val adapter = ChatRecycleViewAdapter(createChatList())
        chatRecycleView.adapter = adapter
        chatRecycleView.scrollToPosition(adapter.itemCount-1)
    }

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
