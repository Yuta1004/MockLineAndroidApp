package work.nityc_nyuta.mockline.Activities

import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.support.design.widget.TabLayout
import android.support.v4.view.ViewPager
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.widget.Toast
import com.github.kittinunf.fuel.Fuel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.iid.FirebaseInstanceId
import io.realm.Realm
import work.nityc_nyuta.mockline.ConfigurationDataClass
import work.nityc_nyuta.mockline.R
import work.nityc_nyuta.mockline.Adapters.SelectTabsAdapter
import work.nityc_nyuta.mockline.ServerConncection.ServerConnectUserData
import kotlin.concurrent.thread

class MainActivity : AppCompatActivity() {
    private val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // サーバに最新の通知トークンを送信
        if(FirebaseAuth.getInstance().currentUser != null) {
            thread {
                val senderId = "1048318911529"
                val token = FirebaseInstanceId.getInstance().getToken(senderId, "FCM")
                ServerConnectUserData().sendUserData("", token, "", "", "update_user")
            }
        }

        Realm.init(this)

    }

    override fun onStart() {
        super.onStart()

        // ネットワーク接続チェック
        if (!networkConnectCheck(this.applicationContext)) {
            Toast.makeText(this, "インターネットに接続されていません", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        // サーバ接続チェック
        val handler = Handler()
        val serverAddress = ConfigurationDataClass().serverAddress
        thread {
            val (request, response, result) =
                    Fuel.get("$serverAddress/check_server").responseString()
            val (data, error) = result
            if(error != null){
                handler.post { Toast.makeText(this, "サーバに接続できません", Toast.LENGTH_SHORT).show() }
            }
        }


        // ログインしていない場合はログインか新規登録を選ぶ画面へ
        val currentUser = firebaseAuth.currentUser
        if(currentUser == null){
            val signSelectActivity = Intent(this, SignSelectActivity::class.java)
            startActivity(signSelectActivity)
        }

        val chatActivity = Intent(this, ChatActivity::class.java)
//        startActivity(chatActivity)
    }

    override fun onResume() {
        super.onResume()

        // タブ表示
        val tabPager = findViewById<ViewPager>(R.id.select_tabs_pager)
        val fragmentManager = supportFragmentManager
        tabPager.adapter = SelectTabsAdapter(fragmentManager)
        findViewById<TabLayout>(R.id.select_tabs_layout).setupWithViewPager(tabPager)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.option_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        Log.d("OptionmenuSelect", "item")
        when(item!!.itemId){
            // トークルーム作成
            R.id.make_talkroom -> {
                val makeTalkroomActivity = Intent(this, MakeTalkroomActivity::class.java)
                startActivity(makeTalkroomActivity)
            }

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
