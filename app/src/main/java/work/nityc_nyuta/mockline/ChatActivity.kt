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

        val chatRecycleView = findViewById<RecyclerView>(R.id.chat_recycle_view)
        chatRecycleView.setHasFixedSize(true)
        chatRecycleView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)

        val adapter = ChatRecycleViewAdapter(createChatList())
        chatRecycleView.adapter = adapter
        chatRecycleView.scrollToPosition(adapter.itemCount-1)
    }

    fun createChatList(): List<ChatData>{
        val chatList = mutableListOf<ChatData>()

        for(idx in 0..50){
            chatList.add(
                    ChatData("Yuta1004", "Hello!!\nWorld!!!!!\nHello!!\n" +
                            "World!!!!!\nHello!!\n" +
                            "World!!!!!\nHello!!\n" +
                            "World!!!!!\nHello!!\n" +
                            "World!!!!!\n" + idx.toString(), "10:04")
            )
        }

        return chatList.toList()
    }
}
