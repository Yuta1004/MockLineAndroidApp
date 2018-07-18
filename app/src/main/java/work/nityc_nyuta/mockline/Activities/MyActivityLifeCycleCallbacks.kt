package work.nityc_nyuta.mockline.Activities

import android.app.Activity
import android.app.Application
import android.os.Bundle
import android.util.Log

class MyActivityLifeCycleCallbacks: Application.ActivityLifecycleCallbacks {
    // 現在表示中のトークルームIdを取得し，保存する
    // 通知を行う際に利用する
    companion object {
        var nowApplicationForeground = false
        var nowDisplayTalkroomID = ""
    }

    override fun onActivityDestroyed(activity: Activity?) {
        nowApplicationForeground = false
        nowDisplayTalkroomID = ""
    }

    override fun onActivityResumed(activity: Activity?) {
        nowApplicationForeground = true
        if(activity!!.intent.hasExtra("id")){
            nowDisplayTalkroomID = activity.intent.getStringExtra("id")
        }
    }

    override fun onActivityPaused(activity: Activity?) {}

    override fun onActivityCreated(activity: Activity?, savedInstanceState: Bundle?) {}

    override fun onActivitySaveInstanceState(activity: Activity?, outState: Bundle?) {}

    override fun onActivityStarted(activity: Activity?) {}

    override fun onActivityStopped(activity: Activity?) {}
}
