package work.nityc_nyuta.mockline.ServerConncection

import android.util.Log
import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.android.extension.responseJson
import com.google.firebase.auth.FirebaseAuth
import com.squareup.moshi.Moshi
import org.json.JSONObject
import work.nityc_nyuta.mockline.ConfigurationDataClass

class ServerConnectTalkroomData{
    fun getJoinTalkrooms(): JSONObject?{
        if(FirebaseAuth.getInstance().currentUser != null){
            val id = FirebaseAuth.getInstance().currentUser!!.email!!

            // Json生成
            val adapter =  Moshi.Builder().build().adapter(JsonBaseGetJoinTalkrooms::class.java)
            val jsonData = adapter.toJson(JsonBaseGetJoinTalkrooms(id))
            val header = hashMapOf("Content-Type" to "application/json")

            // 通信
            val serverAddress = ConfigurationDataClass().serverAddress
            val (request, response, result) =
                    Fuel.post("$serverAddress/get_join_talkrooms").header(header).body(jsonData).responseJson()

            val (data, error) = result

            // 通信に成功したらJSONObjectを返す
            return if(error == null) {
                Log.d("RenponseJson", String(response.data))

                // JsonObjectを返す
                JSONObject(String(response.data))
            }else{
                null
            }
        }

        return null
    }

    fun getTalkroomData(talkroomId: String): JSONObject?{
        // Json生成
        val adapter = Moshi.Builder().build().adapter(JsonBaseGetTalkroomData::class.java)
        val jsonData = adapter.toJson(JsonBaseGetTalkroomData(talkroomId))
        val header = hashMapOf("Content-Type" to "applicaton/json")

        // http post
        val serverAddress = ConfigurationDataClass().serverAddress
        val (request, response, result) =
                Fuel.post("$serverAddress/get_talkroom_data").header(header).body(jsonData).response()

        // 通信エラーを起こさなかったらJsonObjectを返す
        val (data, error) = result
        if(error == null){
            return JSONObject(String(response.data))
        }else{
            return null
        }
    }

    fun sendMakeTalkroom(user_list: String, talkroom_name: String){
        //　Json生成
        val adapter = Moshi.Builder().build().adapter(JsonBaseSendMakeTalkroom::class.java)
        val jsonData = adapter.toJson(JsonBaseSendMakeTalkroom(user_list, talkroom_name))
        val header = hashMapOf("Content-Type" to "application/json")

        // http post
        val serverAddress = ConfigurationDataClass().serverAddress
        val (request, response, result) =
                Fuel.post("$serverAddress/make_talkroom").header(header).body(jsonData).response()
    }

    fun exitTalkroom(talkroomId: String): Boolean{
        val userId = FirebaseAuth.getInstance().currentUser!!.email!!

        // Jsonパース
        val adapter = Moshi.Builder().build().adapter(JsonBaseExitTalkroom::class.java)
        val jsonData = adapter.toJson(JsonBaseExitTalkroom(talkroomId, userId))
        val header = hashMapOf("Content-Type" to "application/json")

        // http_post
        val serverAddress = ConfigurationDataClass().serverAddress
        val (request, response, result) =
                Fuel.post("$serverAddress/exit_talkroom").header(header).body(jsonData).response()

        val (data, error) = result

        return error == null
    }

    fun inviteTalkroom(talkroomId: String, inviteUserIds: List<String>): Boolean{
        // Jsonパース
        val adapter = Moshi.Builder().build().adapter(JsonBaseInviteTalkroom::class.java)
        val jsonData = adapter.toJson(JsonBaseInviteTalkroom(talkroomId, inviteUserIds))
        val header = hashMapOf("Content-Type" to "application/json")

        // http_post
        val serverAddress = ConfigurationDataClass().serverAddress
        val (request, response, result) =
                Fuel.post("$serverAddress/join_talkroom").header(header).body(jsonData).response()

        val (data, error) = result

        return error == null
    }

    data class JsonBaseGetJoinTalkrooms(val id: String)
    data class JsonBaseGetTalkroomData(val talkroom_id: String)
    data class JsonBaseSendMakeTalkroom(val user_list: String, val talkroom_name: String)
    data class JsonBaseExitTalkroom(val talkroom_id: String, val user_id: String)
    data class JsonBaseInviteTalkroom(val talkroom_id: String, val user_ids: List<String>)
}