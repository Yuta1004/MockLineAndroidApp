package work.nityc_nyuta.mockline.Fragments

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.content.LocalBroadcastManager
import android.util.Log
import android.view.*
import android.widget.ListView
import com.google.firebase.auth.FirebaseAuth
import org.json.JSONObject
import work.nityc_nyuta.mockline.Activities.TalkActivity
import work.nityc_nyuta.mockline.ServerConncection.ServerConnectTalkroomData
import work.nityc_nyuta.mockline.R
import work.nityc_nyuta.mockline.Adapters.Talkroom
import work.nityc_nyuta.mockline.Adapters.TalkroomViewListAdapter

import kotlin.concurrent.thread


class TalkroomViewFragment : Fragment() {
    private var layoutView: View? = null
    private var localBroadcastReceiver: BroadcastReceiver? = null

    // TalkroomListViewのadapterを保持しておく　<- 無駄な通信をしない
    companion object {
        var usedUserID = ""
        var talkroomListAdapter: TalkroomViewListAdapter? = null
    }

    // FragmentのViewが生成されるときに呼ばれる
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // LocalBroadCastのレシーバ
        localBroadcastReceiver = object: BroadcastReceiver(){
            override fun onReceive(context: Context?, intent: Intent?) {
                if(intent!!.action == "UpdateTalkroomListView") {
                    // ListViewリセット
                    setTalkroomListViewAdapter()
                    setTalkroomListView()
                    usedUserID = FirebaseAuth.getInstance().currentUser!!.email!!
                }
            }
        }

        // レシーバを登録
        val intentFilter = IntentFilter()
        intentFilter.addAction("UpdateTalkroomListView")
        LocalBroadcastManager.getInstance(activity!!).registerReceiver(localBroadcastReceiver!!, intentFilter)

        layoutView = inflater.inflate(R.layout.fragment_talkroom_view, container, false)
        return layoutView
    }

    // Viewの生成が終了したら呼ばれる
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if(FirebaseAuth.getInstance().currentUser == null){ return }

        // ListViewで表示しているデータのユーザIDと現在ログインしているユーザIDが異なる場合はListViewリセット
        if(FirebaseAuth.getInstance().currentUser!!.email == usedUserID) {
            if (talkroomListAdapter == null) { setTalkroomListViewAdapter() }
            setTalkroomListView()
        }else{
            talkroomListAdapter = null
            setTalkroomListViewAdapter()
            setTalkroomListView()
            usedUserID = FirebaseAuth.getInstance().currentUser!!.email!!
        }
    }

    // Adapterセット
    private fun setTalkroomListViewAdapter(){
        // スレッド終了監視フラグ
        var connectEnd = false

        var retJsonObj: JSONObject? = null

        // スレッド
        thread {
            retJsonObj = ServerConnectTalkroomData().getJoinTalkrooms()
            connectEnd = true
        }

        // スレッド終了まで待機
                while(!connectEnd){ Thread.sleep(100) }

        // 通信結果がnullでないなら
        val listviewSetList = mutableListOf<Talkroom>()
        if (retJsonObj != null) {
            val talkroomsArray = retJsonObj!!.getJSONArray("talkrooms")

            if (talkroomsArray.length() > 0) {
                for (idx in 0..(talkroomsArray.length() - 1)) {
                    val talkroom = JSONObject(talkroomsArray[idx].toString())
                    val talkroomId = talkroom.getString("id")
                    val talkroomName = talkroom.getString("name")

                    listviewSetList.add(Talkroom(talkroomId, talkroomName))
                }
            }
        }

        talkroomListAdapter = TalkroomViewListAdapter(listviewSetList.toList(), activity!!.layoutInflater)
    }

    // ListViewセット
    private fun setTalkroomListView() {
        // トークルーム一覧を表示する
        val talkroomListview = layoutView!!.findViewById<ListView>(R.id.talkroom_view_listview)

        // ListView反映
        talkroomListview.adapter = talkroomListAdapter
        talkroomListAdapter!!.notifyDataSetChanged()

        // トークルームが選択されたら
        talkroomListview.setOnItemClickListener { parent, view, position, id ->
            val chatActivity = Intent(activity, TalkActivity::class.java)
            chatActivity.putExtra("id", talkroomListAdapter!!.talkroomList[position].id)
            chatActivity.putExtra("name", talkroomListAdapter!!.talkroomList[position].name)
            startActivity(chatActivity)
        }
    }

    override fun onDestroy() {
        super.onDestroy()

        // レシーバの登録解除
        LocalBroadcastManager.getInstance(activity!!).unregisterReceiver(localBroadcastReceiver!!)
    }


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        inflater!!.inflate(R.menu.talkroom_fragment_option_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }
}