package work.nityc_nyuta.mockline

import android.app.FragmentManager
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.design.widget.TabLayout
import android.support.v4.view.ViewPager
import android.util.Log
import android.widget.Button
import android.widget.TableLayout
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.iid.FirebaseInstanceId
import kotlin.concurrent.thread

class MainActivity : AppCompatActivity() {
    private val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // ログアウト処理
//        findViewById<Button>(R.id.logout_button).setOnClickListener{
//            firebaseAuth.signOut()
//            Toast.makeText(this, "ログアウトしました", Toast.LENGTH_SHORT).show()
//            val signSelectActivity = Intent(this, SignSelectActivity::class.java)
//            startActivity(signSelectActivity)
//        }
    }

    override fun onStart() {
        super.onStart()

        // ログインしていない場合はログインか新規登録を選ぶ画面へ
        val currentUser = firebaseAuth.currentUser
        if(currentUser == null){
            val signSelectActivity = Intent(this, SignSelectActivity::class.java)
            startActivity(signSelectActivity)
        }
    }

    override fun onResume() {
        super.onResume()

        // ユーザ情報取得
//        val currentUser = firebaseAuth.currentUser
//        if(currentUser != null) {
//            findViewById<TextView>(R.id.user_email).text = currentUser.email
//            findViewById<TextView>(R.id.user_uid).text = currentUser.uid
//            currentUser.getIdToken(true)
//                    .addOnSuccessListener { task ->
//                        findViewById<TextView>(R.id.user_token).text = task.token
//                    }
//        }

        // サーバに最新の通知トークンを送信
        thread{
            val senderId = "1048318911529"
            val token = FirebaseInstanceId.getInstance().getToken(senderId, "FCM")
            SendUserData().sendUserData("", token, "", "", "update_user")
        }

        // タブ表示
        val tabPager = findViewById<ViewPager>(R.id.select_tabs_pager)
        val fragmentManager = supportFragmentManager
        tabPager.adapter = SelectTabsAdapter(fragmentManager)
        findViewById<TabLayout>(R.id.select_tabs_layout).setupWithViewPager(tabPager)
    }
}
