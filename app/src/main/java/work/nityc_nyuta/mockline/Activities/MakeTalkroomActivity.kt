package work.nityc_nyuta.mockline.Activities

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import work.nityc_nyuta.mockline.R

class MakeTalkroomActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_make_talkroom)

        title = "トークルーム作成"
    }
}
