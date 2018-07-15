package work.nityc_nyuta.mockline.Activities

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.design.widget.TextInputEditText
import android.support.design.widget.TextInputLayout
import android.util.Log
import android.widget.Button
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.iid.FirebaseInstanceId
import work.nityc_nyuta.mockline.R
import work.nityc_nyuta.mockline.ServerConncection.ServerConnectUserData
import kotlin.concurrent.thread

class LoginActivity : AppCompatActivity() {
    private val mAuth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        title = "ログイン"

        // ログインボタンが押された時のリスナ
        findViewById<Button>(R.id.login_button).setOnClickListener {
            val mailAddress = findViewById<TextInputEditText>(R.id.mailaddress_inp).text.toString()
            val password = findViewById<TextInputEditText>(R.id.password_inp).text.toString()

            val mailAddressLayout = findViewById<TextInputLayout>(R.id.mailaddress_inp_layout)
            val passwordLayout = findViewById<TextInputLayout>(R.id.password_inp_layout)

            var inputSuccessFlag = true

            mailAddressLayout.isErrorEnabled = false
            passwordLayout.isErrorEnabled = false
            if(mailAddress == ""){
                mailAddressLayout.error = "メールアドレスが入力されていません"
                inputSuccessFlag = false
            }
            if(password == ""){
                passwordLayout.error = "パスワードが入力されていません"
                inputSuccessFlag = false
            }

            // Firebaseを通してログイン
            if(inputSuccessFlag) {
                mAuth.signInWithEmailAndPassword(mailAddress, password)
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                Log.d("UserLogin", "Success")
                                Toast.makeText(this, "ログインしました", Toast.LENGTH_SHORT).show()

                                // サーバに通知トークンを送信
                                thread {
                                    val senderId = "1048318911529"
                                    val token = FirebaseInstanceId.getInstance().getToken(senderId, "FCM")
                                    ServerConnectUserData().sendUserData("", token, "", "", "update_user")
                                }

                                finish()
                            } else {
                                Log.d("UserLogin", "Failed")
                                Toast.makeText(this, "ログインに失敗しました", Toast.LENGTH_SHORT).show()
                            }
                        }
            }
        }
    }
}
