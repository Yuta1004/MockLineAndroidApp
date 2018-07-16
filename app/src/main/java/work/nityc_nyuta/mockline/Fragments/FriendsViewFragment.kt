package work.nityc_nyuta.mockline.Fragments

import android.os.Bundle
import android.os.Handler
import android.support.v4.app.Fragment
import android.util.Log
import android.view.*
import android.widget.ListView
import work.nityc_nyuta.mockline.Adapters.FriendsViewListAdapter
import work.nityc_nyuta.mockline.R
import work.nityc_nyuta.mockline.ServerConncection.ServerConnectFriendsData
import kotlin.concurrent.thread

class FriendsViewFragment : Fragment() {
    // LayoutViewを保持する
    private var layoutView: View? = null

    // FriendListViewのAdapterを保持しておく <- 無駄な通信をしないため
    companion object {
        var friendListAdapter: FriendsViewListAdapter? = null
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,savedInstanceState: Bundle?): View? {
        layoutView = inflater.inflate(R.layout.fragment_friends_view, container, false)

        return layoutView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val handler = Handler()
        thread {
            if (friendListAdapter == null) { setFriendsListViewAdapter() }
            handler.post{ setFriendsListView() }
        }

    }

    // ListViewリセット
    fun resetFriendsListView(){
        friendListAdapter = null
        val handler = Handler()
        thread {
            setFriendsListViewAdapter()
            handler.post{ setFriendsListView() }
        }
    }

    private fun setFriendsListViewAdapter(){
        val friendIdList = ServerConnectFriendsData().getFriendsList()

        // 友達情報が取得できたら
        if (friendIdList != null) {
            val adapter = FriendsViewListAdapter(friendIdList, activity!!.layoutInflater)
            friendListAdapter = adapter
        }
    }

    // ListViewにAdapterをセット
    private fun setFriendsListView(){
        val friendListView = layoutView!!.findViewById<ListView>(R.id.friends_list_view)
        friendListView.adapter = friendListAdapter
        friendListAdapter!!.notifyDataSetChanged()
    }
}
