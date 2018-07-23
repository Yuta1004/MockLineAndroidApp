package work.nityc_nyuta.mockline.Activities

import android.content.Intent
import android.graphics.Bitmap
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.support.v7.app.AlertDialog
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.zxing.BarcodeFormat
import com.google.zxing.WriterException
import com.journeyapps.barcodescanner.BarcodeEncoder
import kotlinx.android.synthetic.main.talk_holder_me.*
import org.json.JSONObject
import work.nityc_nyuta.mockline.Fragments.FriendsViewFragment
import work.nityc_nyuta.mockline.R
import work.nityc_nyuta.mockline.ServerConncection.ServerConnectFriendsData
import work.nityc_nyuta.mockline.ServerConncection.ServerConnectUserData
import kotlin.concurrent.thread

class AddFriendActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_friend)

        // QRコードの内容
        val userId = FirebaseAuth.getInstance().currentUser!!.email!!
        val timestamp = System.currentTimeMillis().toString()
        val qrContent = "$userId:$timestamp"

        // QRコードを生成してImageviewにセット
        findViewById<ImageView>(R.id.my_qr_view).setImageBitmap(createQRBitmap(qrContent))

        // QRリーダー起動ボタンが押された時にリーダを起動する
        findViewById<Button>(R.id.start_qr_reader).setOnClickListener {
            val qrReadActivity = Intent(this, QRReadActivity::class.java)
            startActivityForResult(qrReadActivity, 0)
        }
    }

    // QR読み取り結果を受け取る
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // QRを読み取らなかった場合
        if(data == null || !data.hasExtra("Result")){ return }

        // QR読み取り結果がnullでない = 正しいものなら友達登録処理
        val readQRResult = readQRResult(data!!.getStringExtra("Result"))
        if(readQRResult != null){
            addFriendProcess(readQRResult)
        }else{
            Toast.makeText(this, "不正なQRコードです", Toast.LENGTH_SHORT).show()
        }
    }

    private fun createQRBitmap(content: String): Bitmap? {
        try {
            // QRコード生成
            val encoder = BarcodeEncoder()
            return encoder.encodeBitmap(content, BarcodeFormat.QR_CODE, 250, 250)
        }catch (e: WriterException){
            // エラーが起きたらnull
            return null
        }
    }

    // 読み取った情報からIDと使用期限を取得して返す
    private fun readQRResult(result: String): String?{
        // 正規表現で判定
        if(!Regex("([0-9a-zA-Z])*@([0-9a-zA-Z])*\\.([0-9a-zA-Z])*:[0-9]*").matches(result)){
            return null
        }

        // 情報取り出し
        val resultArray = result.split(":")
        val userId = resultArray[0]
        val timestamp = resultArray[1].toLong()
        val nowTimestamp = System.currentTimeMillis()

        // 使用期限チェック(発行されて1日以内のものか)
        if(timestamp <= nowTimestamp && nowTimestamp <= timestamp+600000){
            return userId
        }else{
            return null
        }
    }

    // 与えられたIDを友達として追加する
    private fun addFriendProcess(friendId: String){
        // スレッド終了確認監視フラグ
        var connectFlag = false
        var userInfo: JSONObject? = null
        thread {
            userInfo = ServerConnectUserData().getUserData(listOf(friendId))
            connectFlag = true
        }
        while(!connectFlag){ Thread.sleep(100) }

        if(!userInfo!!.has(friendId)){
            Toast.makeText(this, "存在しないユーザです", Toast.LENGTH_SHORT).show()
            return
        }

        // カスタムビュー
        val layout = layoutInflater.inflate(R.layout.friends_list_item, null)
        layout.findViewById<TextView>(R.id.name).text = userInfo!!.getJSONObject(friendId).getString("name")

        // AlertDialog
        val alertDialogBuilder = AlertDialog.Builder(this)
        alertDialogBuilder.setTitle("追加確認")
        alertDialogBuilder.setMessage("このユーザを友達追加しますか？")
        alertDialogBuilder.setView(layout)

        // 追加するを選択したら
        alertDialogBuilder.setPositiveButton("追加する") { _, _ ->
            // スレッド終了監視フラグ
            connectFlag = false
            var friendAddResult: String? = null
            thread {
                friendAddResult = ServerConnectFriendsData().addFriend(friendId)
                connectFlag = true
            }
            while(!connectFlag){ Thread.sleep(100) }

            // 通信成功
            if(friendAddResult != null){
                when(friendAddResult) {
                    "Success" -> {
                        Toast.makeText(this, "友達追加に成功しました", Toast.LENGTH_SHORT).show()
                        FriendsViewFragment.friendListAdapter = null
                    }

                    "Already Added User" -> {
                        Toast.makeText(this, "すでに友達になっているユーザです", Toast.LENGTH_SHORT).show()
                    }

                    "Userid is same" -> {
                        Toast.makeText(this, "自分自身を友達追加することはできません", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
        alertDialogBuilder.setNegativeButton("追加しない"){ _, _ -> }

        alertDialogBuilder.show()
    }
}
