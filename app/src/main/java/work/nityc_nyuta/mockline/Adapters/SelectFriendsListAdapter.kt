package work.nityc_nyuta.mockline.Adapters

import android.annotation.SuppressLint
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.CheckBox
import android.widget.TextView
import work.nityc_nyuta.mockline.R
import work.nityc_nyuta.mockline.ServerConncection.ServerConnectFriendsData
import work.nityc_nyuta.mockline.ServerConncection.ServerConnectUserData
import kotlin.concurrent.thread

class SelectFriendsListAdapter(layoutInflater_args: LayoutInflater): BaseAdapter(){
    private val friendsList = mutableListOf<Friend>()
    private val layoutInflater = layoutInflater_args

    init{
        // スレッド終了監視フラグ
        var connectionEnd = false

        thread{
            val friendsIdList = ServerConnectFriendsData().getFriendsList()
            val userInfo = ServerConnectUserData().getUserData(friendsIdList!!)

            // 取得した友達IDリストをもとにfriendsListを作る
            for (friendId in friendsIdList) {
                val friend = userInfo.getJSONObject(friendId)
                val friendName = friend.getString("name")
                val friendIconUrl = friend.getString("icon_url")
                val friendHeaderImageUrl = friend.getString("header_image_url")

                friendsList.add(Friend(friendId, friendName, friendIconUrl, friendHeaderImageUrl))
            }

            // スレッド終了
            connectionEnd = true
        }

        // スレッドが終了するまで待つ
        while(!connectionEnd){ Thread.sleep(100) }
    }

    override fun getCount(): Int {
        return friendsList.size
    }

    override fun getItem(position: Int): Any {
        return friendsList[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    @SuppressLint("ViewHolder")
    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val myConvertView = layoutInflater.inflate(R.layout.select_friend_list_item, parent, false)

        myConvertView.findViewById<TextView>(R.id.name).text = friendsList[position].name

        // チェックボックスが押されるたびにFriendクラスのcheckBoxStatusを変更
        myConvertView.findViewById<CheckBox>(R.id.select_friend).setOnClickListener {
            friendsList[position].checkBoxStatus = !friendsList[position].checkBoxStatus
        }

        return myConvertView
    }

    // チェックボックスで選択中のユーザID一覧をListで返す
    fun getSelectUserIDList(findStatus: Boolean=true): List<String>{
        val retUserIdList = mutableListOf<String>()
        for(friend in friendsList){
            if(friend.checkBoxStatus){
                retUserIdList.add(friend.id)
            }
        }

        return retUserIdList.toList()
    }

    data class Friend(val id: String, val name: String, val icon_url: String, val header_image_url: String,
                      var checkBoxStatus: Boolean = false)
}