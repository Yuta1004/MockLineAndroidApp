package work.nityc_nyuta.mockline.DatabaseTable

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

class TalkHistoryTable: RealmObject(){
    @PrimaryKey
    var talkTimeStamp: Long = 0

    var senderId = ""
    var message = ""
}