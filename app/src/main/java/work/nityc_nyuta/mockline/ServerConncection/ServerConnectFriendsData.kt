package work.nityc_nyuta.mockline.ServerConncection

import com.github.kittinunf.fuel.Fuel
import com.google.firebase.auth.FirebaseAuth
import com.squareup.moshi.Moshi
import org.json.JSONObject
import work.nityc_nyuta.mockline.ConfigurationDataClass

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
        val serverAddress = ConfigurationDataClass().serverAddress
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

    // データクラス
    data class JsonBase(val user_id: String)
}