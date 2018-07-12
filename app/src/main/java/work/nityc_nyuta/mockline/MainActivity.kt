package work.nityc_nyuta.mockline

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.google.firebase.iid.FirebaseInstanceId
import kotlin.concurrent.thread

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        thread{
            val sender_id = "1048318911529"
            val token = FirebaseInstanceId.getInstance().getToken(sender_id, "FCM")
            Log.d("Firebase-Token", token)
        }
    }
}
