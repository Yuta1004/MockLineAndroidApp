package work.nityc_nyuta.mockline.Adapters

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.google.firebase.auth.FirebaseAuth
import work.nityc_nyuta.mockline.R

class TalkRecycleViewAdapter(talkList_args: MutableList<TalkData>): RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private val talkList = talkList_args

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        // ViewTypeによって作るViewHolderを変更
        when(viewType){
            0 -> { // 相手からのメッセージ用ViewHolder
                val rowView = LayoutInflater.from(parent.context).inflate(R.layout.talk_holder_opponent, parent, false)
                return ChatViewHolderOpponent(rowView)
            }

            1 ->{ // 自分のメッセージ用ViewHolder
                val rowView = LayoutInflater.from(parent.context).inflate(R.layout.talk_holder_me, parent, false)
                return ChatViewHolderMe(rowView)
            }

            else ->{
                val rowView = LayoutInflater.from(parent.context).inflate(R.layout.talk_holder_opponent, parent, false)
                return ChatViewHolderOpponent(rowView)
            }
        }

    }

    // ViewHolderに情報を埋め込む
    override fun onBindViewHolder(holder_args: RecyclerView.ViewHolder, position: Int) {
        // ViewTypeで埋め込む情報を変える
        when(holder_args.itemViewType){
            0 -> { // 相手からのメッセージ
                val holder = holder_args as ChatViewHolderOpponent
                //holder.senderNameView.text = talkList[position].senderName
                holder.senderNameView.text = ""
                holder.bodyView.text = talkList[position].body
                holder.timeView.text = talkList[position].time

                holder.iconView.layoutParams.width = 100
                holder.iconView.layoutParams.height = 100
                holder.iconLinearLauout.layoutParams.width = 20
                holder.iconLinearLauout.layoutParams.height = 100
            }

            1 ->{ // 自分のメッセージ
                val holder = holder_args as ChatViewHolderMe
                holder.bodyView.text = talkList[position].body
                holder.timeView.text = talkList[position].time
            }
        }
    }

    override fun getItemCount(): Int {
        return talkList.size
    }

    override fun getItemViewType(position: Int): Int {
        var meId = ""
        if (FirebaseAuth.getInstance().currentUser != null){
            meId = FirebaseAuth.getInstance().currentUser!!.email!!
        }

        // ViewTypeを返す
        return if(talkList[position].senderId != meId){
            0
        }else{
            1
        }
    }
    
    fun addTalkList(senderId: String, message: String, time: Long){
        talkList.add(TalkData(senderId, message, time.toString()))
    }

    // 相手からのメッセージ用ViewHolder
    private class ChatViewHolderOpponent(itemView: View): RecyclerView.ViewHolder(itemView){
        val senderNameView = itemView.findViewById<TextView>(R.id.sender_name)!!
        val bodyView = itemView.findViewById<TextView>(R.id.body)!!
        val timeView = itemView.findViewById<TextView>(R.id.time)!!
        val iconView = itemView.findViewById<ImageView>(R.id.icon)!!
        val iconLinearLauout = itemView.findViewById<LinearLayout>(R.id.icon_linear_layout)!!
    }


    // 自分のメッセージ用ViewHolder
    private class ChatViewHolderMe(itemView: View): RecyclerView.ViewHolder(itemView){
        val bodyView = itemView.findViewById<TextView>(R.id.body)!!
        val timeView = itemView.findViewById<TextView>(R.id.time)!!
    }
}

// データクラス
data class TalkData(val senderId: String, val body: String, val time: String)