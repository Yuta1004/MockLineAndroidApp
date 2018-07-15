package work.nityc_nyuta.mockline.ServerConncection

import android.util.Log
import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.android.extension.responseJson
import com.google.firebase.auth.FirebaseAuth
import com.squareup.moshi.Moshi
import org.json.JSONObject
import work.nityc_nyuta.mockline.ConfigurationDataClass

class GetTalkroomData{
    fun getJoinTalkrooms(): JSONObject?{
        if(FirebaseAuth.getInstance().currentUser != null){
            val id = FirebaseAuth.getInstance().currentUser!!.email!!

            // Json生成
            val adapter =  Moshi.Builder().build().adapter(JsonBase::class.java)
            val jsonData = adapter.toJson(JsonBase(id))
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

    // データクラス
    data class JsonBase(val id: String)
}