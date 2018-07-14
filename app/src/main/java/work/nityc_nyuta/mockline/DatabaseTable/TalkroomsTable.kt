package work.nityc_nyuta.mockline.DatabaseTable

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

class TalkroomsTable: RealmObject(){
    @PrimaryKey
    var talkroomId = ""

    var talkloomName = ""
    var talkHistory = listOf<TalkHistoryTable>()
}