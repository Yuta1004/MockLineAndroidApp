package work.nityc_nyuta.mockline.Database

import android.content.Context
import io.realm.Realm
import io.realm.RealmList
import io.realm.internal.Table
import work.nityc_nyuta.mockline.Adapters.Talkroom

class TalkroomDatabaseHelper(context: Context){
    private var realmInstance: Realm? = null

    init{
        // contextがnullでない場合は強制的にそのcontextで初期化
        Realm.init(context)
        realmInstance = Realm.getDefaultInstance()
    }

    fun existenceTalkroom(talkroomId: String): Boolean{
        val talkroom = realmInstance!!.where(TalkroomTable::class.java)
                .equalTo("talkroomId", talkroomId)
                .findFirst()

        return talkroom != null
    }

    fun makeTalkroom(talkroomId: String, talkroomName: String, talkroomUserList: List<String>){
        // トランザクション
        realmInstance!!.executeTransaction {
            // トークルームオブジェクトを作って情報を設定
            val talkroom = realmInstance!!.createObject(TalkroomTable::class.java, talkroomId)
            talkroom.talkroomName = talkroomName
            for(userId in talkroomUserList){
                talkroom.talkroomUserList.add(userId)
            }

            realmInstance!!.copyToRealm(talkroom)
        }
    }

    fun removeTalkroom(talkroomId: String){
        // トランザクション
        realmInstance!!.executeTransaction {
            val talkroom = realmInstance!!.where(TalkroomTable::class.java)
                    .equalTo("talkroomId", talkroomId)
                    .findAll()

            // トークルーム削除
            if(talkroom.size != 0){ talkroom.deleteAllFromRealm() }
        }
    }

    fun addTalkHistory(talkroomId: String, senderId: String, message: String, timestamp: Long): Boolean{
        var successFlag = false

        // トランザクション
        realmInstance!!.executeTransaction {
            val talkroom = realmInstance!!.where(TalkroomTable::class.java)
                    .equalTo("talkroomId", talkroomId)
                    .findFirst()

            // トークルームが作成されていないなら
            if(talkroom == null){
                successFlag = false
                return@executeTransaction
            }

            // トーク履歴オブジェクトを作って情報設定
            val talkHistory = realmInstance!!.createObject(TalkHistoryTable::class.java)
            talkHistory.senderId = senderId
            talkHistory.message = message
            talkHistory.timestamp = timestamp

            talkroom.talkHistory.add(talkHistory)
            successFlag = true
        }

        return successFlag
    }

    fun getTalkHistory(talkroomId: String): List<TalkHistoryTable>?{
        // IDをもとにトークルームDBからデータを取り出す
        // トークルームが存在しない場合はnullを返す
        val talkroom = realmInstance!!.where(TalkroomTable::class.java)
                .equalTo("talkroomId", talkroomId)
                .findFirst() ?: return null

        return talkroom.talkHistory.toList()
    }

    fun close(){
        realmInstance!!.close()
    }
}