package work.nityc_nyuta.mockline.DatabaseTable

import io.realm.RealmList
import io.realm.RealmObject
import io.realm.annotations.Ignore
import io.realm.annotations.PrimaryKey

open class TalkroomTable(
        @PrimaryKey open var talkroomId: String = "",
        open var talkroomName: String = "",
        open var talkroomUserList: RealmList<String> = RealmList(),
        open var talkHistory: RealmList<TalkHistoryTable> = RealmList()
): RealmObject()

open class TalkHistoryTable(
        open var senderId: String = "",
        open var message: String = "",
        open var timestamp: Long = 0
): RealmObject()