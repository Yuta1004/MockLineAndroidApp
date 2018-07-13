package work.nityc_nyuta.mockline

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.design.widget.TextInputEditText
import android.support.design.widget.TextInputLayout
import android.util.Log
import android.widget.Button
import android.widget.Toast
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.iid.FirebaseInstanceId
import kotlin.concurrent.thread

class RegisterActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        title = "新規登録"

        // 新規登録ボタンが押された時のリスナ
        findViewById<Button>(R.id.register_button).setOnClickListener {
            val userName = findViewById<TextInputEditText>(R.id.name_inp).text.toString()
            val mailAddress = findViewById<TextInputEditText>(R.id.mailaddress_inp).text.toString()
            val passwordNo1 = findViewById<TextInputEditText>(R.id.password_inp_1).text.toString()
            val passwordNo2 = findViewById<TextInputEditText>(R.id.password_inp_2).text.toString()

            val userNameLayout = findViewById<TextInputLayout>(R.id.name_inp_layout)
            val mailAddressLayout = findViewById<TextInputLayout>(R.id.mailaddress_inp_layout)
            val passwordNo1Layout = findViewById<TextInputLayout>(R.id.password_inp_1_layout)
            val passwordNo2Layout = findViewById<TextInputLayout>(R.id.password_inp_2_layout)

            // 入力された情報が正しいか判別
            var inputTrueFlag = true
            userNameLayout.isErrorEnabled = false
            if(userName == ""){
                userNameLayout.error = "名前が入力されていません"
                inputTrueFlag = false
            }

            // メールアドレスの正規表現マッチ
            mailAddressLayout.isErrorEnabled = false
            if(!Regex("([0-9a-zA-Z])*@([0-9a-zA-Z])*\\.([0-9a-zA-Z])*").matches(mailAddress)){
                mailAddressLayout.error = "メールアドレスが不正です"
                inputTrueFlag = false
            }

            passwordNo1Layout.isErrorEnabled = false
            passwordNo2Layout.isErrorEnabled = false
            if(passwordNo1 != passwordNo2){
                passwordNo1Layout.error = "パスワードが一致しません"
                inputTrueFlag = false
            }

            if(passwordNo1 == ""){
                passwordNo1Layout.error = "パスワードが入力されていません"
                inputTrueFlag = false
            }
            if(passwordNo2 == ""){
                passwordNo2Layout.error = "パスワードが入力されていません"
                inputTrueFlag = false
            }

            // 入力内容が全て正常なら新規登録処理
            if(inputTrueFlag){
                val mAuth = FirebaseAuth.getInstance()
                // ユーザ作成リスナ
                mAuth.createUserWithEmailAndPassword(mailAddress, passwordNo1)
                        .addOnCompleteListener{task: Task<AuthResult> ->

                    // 正常に登録できたか
                    if(task.isSuccessful){
                        Log.d("CreateNewUser", "Successful")
                        Toast.makeText(this, "新規ユーザ登録が正常に完了しました", Toast.LENGTH_SHORT).show()

                        // サーバに通知トークンを送信
                        thread{
                            val sender_id = "1048318911529"
                            val token = FirebaseInstanceId.getInstance().getToken(sender_id, "FCM")
                            SendUserData().sendUserData(userName, token, "", "", "add_user")
                        }

                        finish()
                    }else{
                        Log.d("CreateNewUser", "Failed")
                        Toast.makeText(this, "新規ユーザ登録に失敗しました", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }
}
