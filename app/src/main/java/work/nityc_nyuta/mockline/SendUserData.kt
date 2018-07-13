package work.nityc_nyuta.mockline

import android.util.Log
import com.github.kittinunf.fuel.Fuel
import com.google.firebase.auth.FirebaseAuth
import com.squareup.moshi.Moshi

class SendUserData{
    fun sendUserData(name: String?, notifyToken: String?, iconUrl: String?, headerImageUrl: String?){
        if(FirebaseAuth.getInstance().currentUser != null) {
            val id = FirebaseAuth.getInstance().currentUser!!.email

            // Jsonパース
            val adapter = Moshi.Builder().build().adapter(JsonBase::class.java)
            val jsonData = adapter.toJson(JsonBase(id, name, notifyToken, iconUrl, headerImageUrl))
            val header = hashMapOf("Content-Type" to "application/json")

            // http Post
            Log.d("Json", jsonData)
            Fuel.post("https://e8d4ec4d.ngrok.io/update_user").header(header).body(jsonData).responseString()
        }
    }

    // データクラス
    data class JsonBase(private val id: String ?,
                        private val name: String?, private val notify_token: String?,
                        private val icon_url: String?, private val header_image_url: String?)
}