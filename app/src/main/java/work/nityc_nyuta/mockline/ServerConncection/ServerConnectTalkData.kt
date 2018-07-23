package work.nityc_nyuta.mockline.ServerConncection

import com.github.kittinunf.fuel.Fuel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.squareup.moshi.Moshi

class ServerConnectTalkData(){
    fun sendTalkData(talkroomId: String, message: String, timestamp: Long): Boolean{
        val senderId = FirebaseAuth.getInstance().currentUser!!.email!!

        // Jsonパース
        val adapter = Moshi.Builder().build().adapter(TalkData::class.java)
        val jsonData = adapter.toJson(TalkData(talkroomId, senderId, message, timestamp))
        val header = hashMapOf("Content-Type" to "application/json")

        // http post
        val serverAddress = FirebaseRemoteConfig.getInstance().getString("ServerAddress")
        val (request, response, result) =
                Fuel.post("$serverAddress/send_message").header(header).body(jsonData).response()

        // 通信結果を返す
        val (data, error) = result
        return error == null
    }

    private data class TalkData(val talkroom_id: String, val sender_id: String,
                                val message: String, val timestamp: Long)
}