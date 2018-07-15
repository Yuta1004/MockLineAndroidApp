package work.nityc_nyuta.mockline.Fragments

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.support.v4.app.Fragment
import android.view.*
import android.widget.ListView
import org.json.JSONObject
import work.nityc_nyuta.mockline.Activities.ChatActivity
import work.nityc_nyuta.mockline.ServerConncection.GetTalkroomData
import work.nityc_nyuta.mockline.R
import work.nityc_nyuta.mockline.Adapters.Talkroom
import work.nityc_nyuta.mockline.Adapters.TalkroomViewListAdapter
import kotlin.concurrent.thread


class TalkroomViewFragment : Fragment() {
    private var layoutView: View? = null

    // FragmentのViewが生成されるときに呼ばれる
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        layoutView = inflater.inflate(R.layout.fragment_talkroom_view, container, false)
        return layoutView
    }

    // Viewの生成が終了したら呼ばれる
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // トークルーム一覧を表示する
        val talkroomListview = layoutView!!.findViewById<ListView>(R.id.talkroom_view_listview)
        val talkroomListAdapter = TalkroomViewListAdapter()
        talkroomListAdapter.setInfo(LayoutInflater.from(activity))

        // スレッド内からUIを弄るためにハンドラを作成
        val handler = Handler()

        // 通信を行うため，スレッドを建てる
        thread{
            val retJsonObj = GetTalkroomData().getJoinTalkrooms()
            val listviewSetList = mutableListOf<Talkroom>()

            // 通信結果がnullでないなら
            if (retJsonObj != null) {
                val talkroomsArray = retJsonObj.getJSONArray("talkrooms")

                if(talkroomsArray.length() > 0) {
                    for (idx in 0..(talkroomsArray.length() - 1)) {
                        val talkroom = JSONObject(talkroomsArray[idx].toString())
                        val talkroomId = talkroom.getString("id")
                        val talkroomName = talkroom.getString("name")

                        listviewSetList.add(Talkroom(talkroomId, talkroomName))
                    }
                }
            }

            // ListView反映
            handler.post {
                talkroomListAdapter.talkroomList = listviewSetList.toList()
                talkroomListview.adapter = talkroomListAdapter
                talkroomListAdapter.notifyDataSetChanged()
            }
        }

        talkroomListview.setOnItemClickListener { parent, view, position, id ->
            val chatActivity = Intent(activity, ChatActivity::class.java)
            chatActivity.putExtra("id", talkroomListAdapter.talkroomList!![position].id)
            startActivity(chatActivity)
        }
    }


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        MenuInflater(activity!!.baseContext).inflate(R.menu.talkroom_fragment_option_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        return super.onOptionsItemSelected(item)
    }
}