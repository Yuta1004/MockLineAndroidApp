package work.nityc_nyuta.mockline

import android.support.v7.view.menu.ActionMenuItemView
import android.support.v7.widget.RecyclerView
import android.text.Layout
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView

class ChatRecycleViewAdapter(chatList_args: List<ChatData>): RecyclerView.Adapter<ChatViewHolder>() {
    private val chatList = chatList_args

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatViewHolder {
        Log.d("RecycleAdapter", "onCreateViewAdapter")
        val rowView = LayoutInflater.from(parent.context).inflate(R.layout.chat_holder, parent, false)

        return ChatViewHolder(rowView)
    }

    override fun onBindViewHolder(holder: ChatViewHolder, position: Int) {
        holder.senderNameView.text = chatList[position].senderName
        holder.bodyView.text = chatList[position].body
        holder.timeView.text = chatList[position].time
        holder.iconView.layoutParams.width = 100
        holder.iconView.layoutParams.height = 100
        holder.iconLinearLauout.layoutParams.width = 40
        holder.iconLinearLauout.layoutParams.height = 100
    }

    override fun getItemCount(): Int {
        return chatList.size
    }

}

class ChatViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
    val senderNameView = itemView.findViewById<TextView>(R.id.sender_name)!!
    val bodyView = itemView.findViewById<TextView>(R.id.body)!!
    val timeView = itemView.findViewById<TextView>(R.id.time)!!
    val iconView = itemView.findViewById<ImageView>(R.id.icon)!!
    val iconLinearLauout = itemView.findViewById<LinearLayout>(R.id.icon_linear_layout)!!
}

data class ChatData(val senderName: String, val body: String, val time: String)