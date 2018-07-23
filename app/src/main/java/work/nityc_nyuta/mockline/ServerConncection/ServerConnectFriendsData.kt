package work.nityc_nyuta.mockline.ServerConncection

import com.github.kittinunf.fuel.Fuel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.squareup.moshi.Moshi
import org.json.JSONObject

class ServerConnectFriendsData {
    fun getFriendsList(): List<String>? {
        // ログイン中であれば
        if (FirebaseAuth.getInstance().currentUser == null) {
            return null
        }

        val userId = FirebaseAuth.getInstance().currentUser!!.email!!

        // Jsonパース
        val adapter = Moshi.Builder().build().adapter(JsonBase::class.java)
        val jsonData = adapter.toJson(JsonBase(userId))
        val header = hashMapOf("Content-type" to "application/json")

        // http post
        val serverAddress = FirebaseRemoteConfig.getInstance().getString("ServerAddress")
        val (request, response, result) =
                Fuel.post("$serverAddress/get_friends_list").header(header).body(jsonData).response()

        val (data, error) = result

        // 通信に成功したら
        if(data != null) {
            // 取得したJsonからJsonArrayを取り出してlistにする
            val friendsIdListJsonArray = JSONObject(String(response.data)).getJSONArray("friends_list")
            val friendsIdList = mutableListOf<String>()

            for (idx in 0 until friendsIdListJsonArray.length()) {
                friendsIdList.add(friendsIdListJsonArray.getString(idx))
            }

            return friendsIdList.toList()
        }else{
            return null
        }
    }

    fun addFriend(friendUserId: String): String?{
        val userId = FirebaseAuth.getInstance().currentUser!!.email!!

        // Jsonパース
        val adapter = Moshi.Builder().build().adapter(JsonBaseAddFriend::class.java)
        val jsonData = adapter.toJson(JsonBaseAddFriend(userId, friendUserId))
        val header = hashMapOf("Content-Type" to "application/json")

        // http post
        val serverAddress = FirebaseRemoteConfig.getInstance().getString("ServerAddress")
        val (request, response, result) =
                Fuel.post("$serverAddress/add_friends").header(header).body(jsonData).response()

        val (data, error) = result

        // 通信結果を返す
        if(error == null){
            return String(response.data)
        }else{
            return null
        }
    }

    // データクラス
    data class JsonBase(val user_id: String)
    data class JsonBaseAddFriend(val user_id: String, val add_friends_user_id: String)
}