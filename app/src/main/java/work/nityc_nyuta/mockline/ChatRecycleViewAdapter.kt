package work.nityc_nyuta.mockline

import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.google.firebase.auth.FirebaseAuth

class ChatRecycleViewAdapter(chatList_args: List<ChatData>): RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private val chatList = chatList_args

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        when(viewType){
            0 -> {
                val rowView = LayoutInflater.from(parent.context).inflate(R.layout.chat_holder_opponent, parent, false)
                return ChatViewHolderOpponent(rowView)
            }

            1 ->{
                val rowView = LayoutInflater.from(parent.context).inflate(R.layout.chat_holder_me, parent, false)
                return ChatViewHolderMe(rowView)
            }

            else ->{
                val rowView = LayoutInflater.from(parent.context).inflate(R.layout.chat_holder_opponent, parent, false)
                return ChatViewHolderOpponent(rowView)
            }
        }

    }

    override fun onBindViewHolder(holder_args: RecyclerView.ViewHolder, position: Int) {
        when(holder_args.itemViewType){
            0 -> {
                val holder = holder_args as ChatViewHolderOpponent
                holder.senderNameView.text = chatList[position].senderName
                holder.bodyView.text = chatList[position].body
                holder.timeView.text = chatList[position].time
                holder.iconView.layoutParams.width = 100
                holder.iconView.layoutParams.height = 100
                holder.iconLinearLauout.layoutParams.width = 20
                holder.iconLinearLauout.layoutParams.height = 100
            }

            1 ->{
                val holder = holder_args as ChatViewHolderMe
                holder.bodyView.text = chatList[position].body
                holder.timeView.text = chatList[position].time
            }
        }
    }

    override fun getItemCount(): Int {
        return chatList.size
    }

    override fun getItemViewType(position: Int): Int {
        var meId = ""
        if (FirebaseAuth.getInstance().currentUser != null){
            meId = FirebaseAuth.getInstance().currentUser!!.email!!
        }

        return if(chatList[position].senderId != meId){
            0
        }else{
            1
        }
    }

}

class ChatViewHolderOpponent(itemView: View): RecyclerView.ViewHolder(itemView){
    val senderNameView = itemView.findViewById<TextView>(R.id.sender_name)!!
    val bodyView = itemView.findViewById<TextView>(R.id.body)!!
    val timeView = itemView.findViewById<TextView>(R.id.time)!!
    val iconView = itemView.findViewById<ImageView>(R.id.icon)!!
    val iconLinearLauout = itemView.findViewById<LinearLayout>(R.id.icon_linear_layout)!!
}

class ChatViewHolderMe(itemView: View): RecyclerView.ViewHolder(itemView){
    val bodyView = itemView.findViewById<TextView>(R.id.body)!!
    val timeView = itemView.findViewById<TextView>(R.id.time)!!
}

data class ChatData(val senderId: String, val senderName: String, val body: String, val time: String)