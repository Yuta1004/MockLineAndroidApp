package work.nityc_nyuta.mockline

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter

class SelectTabsAdapter(fm: FragmentManager): FragmentPagerAdapter(fm){
    private val tabTitles = listOf("友だち", "トーク")

    override fun getPageTitle(position: Int): CharSequence? {
        return tabTitles[position]
    }

    override fun getCount(): Int {
        return tabTitles.size
    }

    override fun getItem(position: Int): Fragment? {
        return when(position){
            0 -> {
                val friendFragment = FriendsViewFragment()
                friendFragment.retainInstance = true
                friendFragment
            }
            1 -> {
                val talkroomFragment = TalkroomViewFragment()
                talkroomFragment.retainInstance = true
                talkroomFragment
            }
            else -> null
        }
    }
}