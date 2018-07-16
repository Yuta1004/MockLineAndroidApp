package work.nityc_nyuta.mockline.Adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import work.nityc_nyuta.mockline.R

class TalkroomViewListAdapter(talkroomList_args: List<Talkroom>, layoutInflater_args: LayoutInflater): BaseAdapter(){
    // トークルーム一覧を保持するlist
    val talkroomList = talkroomList_args

    private val layoutInflater = layoutInflater_args

    override fun getCount(): Int {
        return talkroomList.size
    }

    override fun getItem(position: Int): Any {
        return talkroomList[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    // ListViewの要素のViewを返す
    @SuppressLint("ViewHolder")
    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val myConvertView = this.layoutInflater.inflate(R.layout.talkroom_list_item, parent, false)

        myConvertView.findViewById<TextView>(R.id.talkroom_name).text = talkroomList[position].name

        return myConvertView
    }
}

// トークルームのデータクラス
data class Talkroom(val id: String, val name:String)