package work.nityc_nyuta.mockline.Activities

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import com.google.firebase.auth.FirebaseAuth
import work.nityc_nyuta.mockline.R

class SignSelectActivity : AppCompatActivity() {
    private val firebaseAuth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_select)

        // 新規登録ボタンが押された時のリスナ
        findViewById<Button>(R.id.select_register_button).setOnClickListener{
            val loginIntent = Intent(this, LoginActivity::class.java)
            startActivity(loginIntent)
        }

        // ログインボタンが押された時のリスナ
        findViewById<Button>(R.id.select_login_button).setOnClickListener{
            val registerIntent = Intent(this, RegisterActivity::class.java)
            startActivity(registerIntent)
        }
    }

    override fun onStart() {
        super.onStart()

        // ログイン中であればこのActivityを終了させる
        val currentUser = firebaseAuth.currentUser
        if(currentUser != null){ // ログイン中
            finish()
        }
    }

    // 戻るボタン無効化
    override fun onBackPressed() {
        return
    }
}
