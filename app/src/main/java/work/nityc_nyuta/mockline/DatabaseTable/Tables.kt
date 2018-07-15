package work.nityc_nyuta.mockline.DatabaseTable

import io.realm.RealmList
import io.realm.RealmObject
import io.realm.annotations.Ignore
import io.realm.annotations.PrimaryKey

open class TalkroomTable(
        @PrimaryKey var talkroomId: String,
        var talkroomName: String,
        var talkroomUserList: RealmList<String>,
        var talkHistory: RealmList<TalkHistoryTable>
): RealmObject()

open class TalkHistoryTable(
        var senderId: String,
        var message: String,
        var timestamp: Long
): RealmObject()