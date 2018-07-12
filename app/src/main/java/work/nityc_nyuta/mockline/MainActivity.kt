package work.nityc_nyuta.mockline

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
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
        findViewById<Button>(R.id.logout_button).setOnClickListener{
            firebaseAuth.signOut()
            Toast.makeText(this, "ログアウトしました", Toast.LENGTH_SHORT).show()
            val signSelectActivity = Intent(this, SignSelectActivity::class.java)
            startActivity(signSelectActivity)
        }
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
        val currentUser = firebaseAuth.currentUser
        if(currentUser != null) {
            findViewById<TextView>(R.id.user_email).text = currentUser.email
            findViewById<TextView>(R.id.user_uid).text = currentUser.uid
            currentUser.getIdToken(true)
                    .addOnSuccessListener { task ->
                        findViewById<TextView>(R.id.user_token).text = task.token
                    }
        }
    }
}
