package work.nityc_nyuta.mockline.Fragments

import android.os.Bundle
import android.os.Handler
import android.support.v4.app.Fragment
import android.util.Log
import android.view.*
import android.widget.ListView
import com.google.firebase.auth.FirebaseAuth
import work.nityc_nyuta.mockline.Adapters.FriendsViewListAdapter
import work.nityc_nyuta.mockline.R
import work.nityc_nyuta.mockline.ServerConncection.ServerConnectFriendsData
import kotlin.concurrent.thread

class FriendsViewFragment : Fragment() {
    // LayoutViewを保持する
    private var layoutView: View? = null

    // FriendListViewのAdapterを保持しておく <- 無駄な通信をしないため
    companion object {
        var usedUserID = ""
        var friendListAdapter: FriendsViewListAdapter? = null
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,savedInstanceState: Bundle?): View? {
        layoutView = inflater.inflate(R.layout.fragment_friends_view, container, false)

        return layoutView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if(FirebaseAuth.getInstance().currentUser == null){ return }

        // ListViewで表示しているデータのユーザIDと現在ログインしているユーザIDが異なる場合はListViewリセット
        if(FirebaseAuth.getInstance().currentUser!!.email == usedUserID) {
            if (friendListAdapter == null) { setFriendsListViewAdapter() }
            setFriendsListView()
        }else{
            friendListAdapter = null
            setFriendsListViewAdapter()
            setFriendsListView()
            usedUserID = FirebaseAuth.getInstance().currentUser!!.email!!
        }
    }

    private fun setFriendsListViewAdapter(){
        // スレッド終了監視フラグ
        var connectEnd = false
        var friendIdList: List<String> ? = null

        // スレッド
        thread {
            friendIdList = ServerConnectFriendsData().getFriendsList()
            connectEnd = true
        }

        // スレッド終了まで待機
        while(!connectEnd){}

        // 友達情報が取得できたら
        if (friendIdList != null) {
            val adapter = FriendsViewListAdapter(friendIdList!!, activity!!.layoutInflater)
            friendListAdapter = adapter
        }
    }

    // ListViewにAdapterをセット
    private fun setFriendsListView(){
        val friendListView = layoutView!!.findViewById<ListView>(R.id.friends_list_view)
        friendListView.adapter = friendListAdapter
        if(friendListAdapter != null) {
            friendListAdapter!!.notifyDataSetChanged()
        }
    }
}
