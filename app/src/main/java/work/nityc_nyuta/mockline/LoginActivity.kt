package work.nityc_nyuta.mockline

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button

class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        title = "ログイン"

        findViewById<Button>(R.id.login_button).setOnClickListener {
            finish()
        }
    }

    override fun onBackPressed() {
        return
    }
}
