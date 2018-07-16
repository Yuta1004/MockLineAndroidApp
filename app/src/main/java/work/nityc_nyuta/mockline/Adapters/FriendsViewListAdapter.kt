package work.nityc_nyuta.mockline.Adapters

import android.annotation.SuppressLint
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import work.nityc_nyuta.mockline.R
import work.nityc_nyuta.mockline.ServerConncection.ServerConnectUserData
import kotlin.concurrent.thread

class FriendsViewListAdapter(friendsIdList_args: List<String>, layoutInflater_args: LayoutInflater): BaseAdapter(){
    private val friendsIdList = friendsIdList_args
    private val friendsList = mutableListOf<Friend>()
    private val layoutInflater = layoutInflater_args

    // 与えられた友達IDリストから友達リストを作成
    init{
        // 接続終了確認フラグ
        var connnectionEnd = false

        thread {
            // ユーザ情報をサーバから取得
            val userInfo = ServerConnectUserData().getUserData(friendsIdList)

            for (userId in friendsIdList) {
                // ユーザ情報を取得する
                val user = userInfo.getJSONObject(userId)
                val userName = user.getString("name")
                val iconUrl = user.getString("icon_url")
                val headerImageUrl = user.getString("header_image_url")
                val friendData = Friend(userId, userName, iconUrl, headerImageUrl)

                // 友達リストに追加
                friendsList.add(friendData)
            }

            // 接続処理終了
            connnectionEnd = true
        }

        // スレッドが終了したら関数終了
        while(!connnectionEnd){}
    }

    override fun getCount(): Int {
        return friendsIdList.size
    }

    override fun getItem(position: Int): Any {
        return friendsIdList[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    @SuppressLint("ViewHolder")
    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val myConvertView = layoutInflater.inflate(R.layout.friends_list_item, parent, false)
        myConvertView.findViewById<TextView>(R.id.name).text = friendsList[position].name

        return myConvertView
    }

    // データクラス
    data class Friend(val id: String, val name: String, val icon_url:String, val header_image_url: String)
}