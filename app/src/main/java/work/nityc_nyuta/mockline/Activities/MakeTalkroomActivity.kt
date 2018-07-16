package work.nityc_nyuta.mockline.Activities

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.support.design.widget.TextInputEditText
import android.support.design.widget.TextInputLayout
import android.util.Log
import android.widget.Button
import android.widget.ListView
import android.widget.TextView
import android.widget.Toast
import com.github.kittinunf.result.Result
import com.google.firebase.auth.FirebaseAuth
import work.nityc_nyuta.mockline.Adapters.SelectFriendsListAdapter
import work.nityc_nyuta.mockline.Fragments.TalkroomViewFragment
import work.nityc_nyuta.mockline.R
import work.nityc_nyuta.mockline.ServerConncection.ServerConnectFriendsData
import work.nityc_nyuta.mockline.ServerConncection.ServerConnectTalkroomData
import kotlin.concurrent.thread

class MakeTalkroomActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_make_talkroom)

        title = "トークルーム作成"

        // 選択できる友達一覧をListViewに表示
        val selectFriendListView = findViewById<ListView>(R.id.select_friend)
        val adapter = SelectFriendsListAdapter(layoutInflater)
        selectFriendListView.adapter = adapter
        adapter.notifyDataSetChanged()

        // トークルーム作成ボタンが押された時のリスナ
        findViewById<Button>(R.id.make_talkroom_button).setOnClickListener {
            // トークルーム名取得 -> 入力判定
            val talkroomName = findViewById<TextInputEditText>(R.id.talkroom_name_inp).text.toString()
            if(talkroomName == ""){
                findViewById<TextInputLayout>(R.id.talkroom_name_inp_layout)
                        .error = "トークルーム名が入力されていません"
                return@setOnClickListener
            }

            // チェック中のユーザIDを取得して「；」区切りの文字列へ
            val selectUserIdList = adapter.getSelectUserIDList()
            var userIdListToStr = FirebaseAuth.getInstance().currentUser!!.email!! + ";"
            for(userId in selectUserIdList){ userIdListToStr += "$userId;" }

            // サーバと通信 -> Activity終了
            val handler = Handler()
            thread {
                ServerConnectTalkroomData().sendMakeTalkroom(userIdListToStr, talkroomName)

                handler.post {
                    Toast.makeText(this, "トークルームが作成されました", Toast.LENGTH_SHORT).show()

                    // TalkroomViewFragmentのListViewを再読み込みしてもらうためにadapterをnullにする
                    TalkroomViewFragment.talkroomListAdapter = null

                    finish()
                }
            }
        }
    }
}
