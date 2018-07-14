package work.nityc_nyuta.mockline

import android.util.Log
import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.android.extension.responseJson
import com.google.firebase.auth.FirebaseAuth
import com.squareup.moshi.Moshi
import org.json.JSONObject

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

            Log.d("RenponseJson", String(response.data))

            // JsonObjectを返す
            return JSONObject(String(response.data))
        }

        return null
    }
}

// データクラス
data class JsonBase(val id: String)