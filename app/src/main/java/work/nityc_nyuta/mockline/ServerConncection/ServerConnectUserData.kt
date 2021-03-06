package work.nityc_nyuta.mockline.ServerConncection

import android.util.Log
import com.github.kittinunf.fuel.Fuel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.squareup.moshi.Moshi
import org.json.JSONObject

class ServerConnectUserData{
    fun sendUserData(name: String?, notifyToken: String?, iconUrl: String?, headerImageUrl: String?, path: String){
        if(FirebaseAuth.getInstance().currentUser != null) {
            val id = FirebaseAuth.getInstance().currentUser!!.email

            // Jsonパース
            val adapter = Moshi.Builder().build().adapter(JsonBaseSendUserData::class.java)
            val jsonData = adapter.toJson(JsonBaseSendUserData(id, name, notifyToken, iconUrl, headerImageUrl))
            val header = hashMapOf("Content-Type" to "application/json")

            // http Post
            val serverAddress = FirebaseRemoteConfig.getInstance().getString("ServerAddress")
            Log.d("Json", jsonData)
            Fuel.post("$serverAddress/$path").header(header).body(jsonData).responseString()
        }
    }

    // データクラス
    data class JsonBaseSendUserData(private val id: String ?,
                        private val name: String?, private val notify_token: String?,
                        private val icon_url: String?, private val header_image_url: String?)

    fun getUserData(userIds_args: List<String>): JSONObject{
        // ログイン中のユーザIDを取得
        val userIds = userIds_args

        // Jsonパース
        val adapter = Moshi.Builder().build().adapter(JsonBaseGetUserData::class.java)
        val jsonData = adapter.toJson(JsonBaseGetUserData(userIds))
        val header = hashMapOf("Content-Type" to "application/json")

        // http post
        val serverAddress = FirebaseRemoteConfig.getInstance().getString("ServerAddress")
        val (request, response, result) =
                Fuel.post("$serverAddress/get_user_data").header(header).body(jsonData).response()

        // JSONObjectを返す
        return JSONObject(String(response.data))
    }

    data class JsonBaseGetUserData(val user_ids: List<String>)
}