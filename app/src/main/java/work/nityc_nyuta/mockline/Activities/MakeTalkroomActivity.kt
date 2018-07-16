package work.nityc_nyuta.mockline.Activities

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.design.widget.TextInputEditText
import android.support.design.widget.TextInputLayout
import android.util.Log
import android.widget.Button
import android.widget.ListView
import android.widget.TextView
import work.nityc_nyuta.mockline.Adapters.SelectFriendsListAdapter
import work.nityc_nyuta.mockline.R

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
            var userIdListToStr = ""
            for(userId in selectUserIdList){ userIdListToStr += "$userId;" }
        }
    }
}
