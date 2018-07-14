package work.nityc_nyuta.mockline.Adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import work.nityc_nyuta.mockline.R

class TalkroomViewListAdapter: BaseAdapter(){
    // トークルーム一覧を保持するlist
    var talkroomList: List<Talkroom>? = null

    private var layoutInflater: LayoutInflater? = null

    // LayoutInflaterをセットする
    fun setInfo(layoutInflater: LayoutInflater){
        this.layoutInflater = layoutInflater
    }

    override fun getCount(): Int {
        return talkroomList?.size ?: 0
    }

    override fun getItem(position: Int): Any {
        return if(talkroomList != null) {
            talkroomList!![position]
        }else{
            0
        }
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    // ListViewの要素のViewを返す
    @SuppressLint("ViewHolder")
    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val myConvertView = this.layoutInflater!!.inflate(R.layout.talkroom_list_item, parent, false)

        if(talkroomList != null){
            myConvertView.findViewById<TextView>(R.id.talkroom_name).text = talkroomList!![position].name
        }

        return myConvertView
    }
}

// トークルームのデータクラス
data class Talkroom(val id: String, val name:String)