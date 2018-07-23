package work.nityc_nyuta.mockline.Activities

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.os.Build
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.support.design.widget.TabLayout
import android.support.v4.view.ViewPager
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import com.github.kittinunf.fuel.Fuel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.iid.FirebaseInstanceId
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings
import io.realm.Realm
import io.realm.RealmConfiguration
import work.nityc_nyuta.mockline.R
import work.nityc_nyuta.mockline.Adapters.SelectTabsAdapter
import work.nityc_nyuta.mockline.BuildConfig
import work.nityc_nyuta.mockline.ServerConncection.ServerConnectUserData
import kotlin.concurrent.thread

class MainActivity : AppCompatActivity() {
    private val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()
    private var firebaseRemoteConfig: FirebaseRemoteConfig? = null
    private val myActivityLifeCycleCallbacks = MyActivityLifeCycleCallbacks()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // FirebaseRemoteConfigの設定
        firebaseRemoteConfig = FirebaseRemoteConfig.getInstance()
        val coreSettings = FirebaseRemoteConfigSettings.Builder()
                .setDeveloperModeEnabled(BuildConfig.DEBUG)
                .build()
        firebaseRemoteConfig!!.setConfigSettings(coreSettings)
        firebaseRemoteConfig!!.setDefaults(R.xml.firebase_remote_config_value)

        // 更新間隔(開発者モードの時は起動時毎回Fetch)
        var cacheExpression = 3600L
        if(firebaseRemoteConfig!!.info.configSettings.isDeveloperModeEnabled){
            cacheExpression = 0L
        }

        // Fetch
        firebaseRemoteConfig!!.fetch(cacheExpression)
                .addOnCompleteListener(this){ task ->
                    if(task.isSuccessful){
                        firebaseRemoteConfig!!.activateFetched()
                    }else{
                        Toast.makeText(this, "Firebaseサービスに接続できませんでした", Toast.LENGTH_SHORT).show()
                    }
                }

        // Activityのライフサイクルを監視する
        application.registerActivityLifecycleCallbacks(myActivityLifeCycleCallbacks)

        // サーバに最新の通知トークンを送信
        if(FirebaseAuth.getInstance().currentUser != null) {
            thread {
                val senderId = "1048318911529"
                val token = FirebaseInstanceId.getInstance().getToken(senderId, "FCM")
                ServerConnectUserData().sendUserData("", token, "", "", "update_user")
            }
        }

        // AndroidのバージョンがOreo以上なら通知チャンネルを作成する
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            val channelId = getString(R.string.notify_channel_id)
            val channelName = getString(R.string.notify_channel_name)
            val notificationManager =
                    getSystemService(NotificationManager::class.java) as NotificationManager
            notificationManager.createNotificationChannel(
                    NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_DEFAULT)
            )
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
        val serverAddress = FirebaseRemoteConfig.getInstance().getString("ServerAddress")
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

        val chatActivity = Intent(this, TalkActivity::class.java)
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

    override fun onDestroy() {
        super.onDestroy()

        application.unregisterActivityLifecycleCallbacks(myActivityLifeCycleCallbacks)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.option_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        Log.d("OptionmenuSelect", "item")
        when(item!!.itemId){
            R.id.add_friends ->{
                val addFriendActivity = Intent(this, AddFriendActivity::class.java)
                startActivity(addFriendActivity)
            }

            // トークルーム作成
            R.id.make_talkroom -> {
                val makeTalkroomActivity = Intent(this, MakeTalkroomActivity::class.java)
                startActivity(makeTalkroomActivity)
            }

            // ログアウト
            R.id.logout -> {
                // DB削除
                val realmConfiguration = RealmConfiguration.Builder().build()
                Realm.deleteRealm(realmConfiguration)

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
