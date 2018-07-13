package work.nityc_nyuta.mockline

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ListView


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
        talkroomViewListview.adapter = talkroomListAdapter

        talkroomListAdapter.notifyDataSetChanged()
    }
}