package work.nityc_nyuta.mockline

import android.os.Bundle
import android.os.Handler
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ListView
import org.json.JSONObject
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
        val talkroomViewListview = layoutView!!.findViewById<ListView>(R.id.talkroom_view_listview)
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
                talkroomViewListview.adapter = talkroomListAdapter
                talkroomListAdapter.notifyDataSetChanged()
            }
        }
    }
}