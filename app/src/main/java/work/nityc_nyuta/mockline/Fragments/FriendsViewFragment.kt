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

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,savedInstanceState: Bundle?): View? {
        layoutView = inflater.inflate(R.layout.fragment_friends_view, container, false)

        return layoutView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val friendListView = layoutView!!.findViewById<ListView>(R.id.friends_list_view)

        // スレッド内でUIを弄るためにHandlerを生成する
        val handler = Handler()

        thread{
            val friendIdList = ServerConnectFriendsData().getFriendsList()

            // 友達情報が取得できたら
            if(friendIdList != null) {
                val adapter = FriendsViewListAdapter(friendIdList, activity!!.layoutInflater)

                // ListViewに値をセット
                handler.post {
                    friendListView.adapter = adapter
                    adapter.notifyDataSetChanged()
                }
            }
        }
    }
}
