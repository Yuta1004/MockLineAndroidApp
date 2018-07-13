package work.nityc_nyuta.mockline

import android.app.AlertDialog
import android.app.FragmentManager
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.net.ConnectivityManager
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.design.widget.TabLayout
import android.support.v4.view.ViewPager
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.widget.Button
import android.widget.TableLayout
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.iid.FirebaseInstanceId
import kotlin.concurrent.thread
import kotlin.system.exitProcess

class MainActivity : AppCompatActivity() {
    private val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

    }

    override fun onStart() {
        super.onStart()

        // ネットワーク接続チェック
        if (!networkConnectCheck(this.applicationContext)) {
            Toast.makeText(this, "インターネットに接続されていません", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        // ログインしていない場合はログインか新規登録を選ぶ画面へ
        val currentUser = firebaseAuth.currentUser
        if(currentUser == null){
            val signSelectActivity = Intent(this, SignSelectActivity::class.java)
            startActivity(signSelectActivity)
        }

        val chatActivity = Intent(this, ChatActivity::class.java)
        startActivity(chatActivity)
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

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        MenuInflater(this).inflate(R.menu.option_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when(item!!.itemId){

            // ログアウト
            R.id.logout -> {
                firebaseAuth.signOut()
                Toast.makeText(this, "ログアウトしました", Toast.LENGTH_SHORT).show()
                val signSelectActivity = Intent(this, SignSelectActivity::class.java)
                startActivity(signSelectActivity)
            }
        }

        return true
    }

    // ネットワーク接続確認
    private fun networkConnectCheck(context: Context): Boolean{
        val connectManager: ConnectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        return connectManager.activeNetworkInfo != null
    }
}
