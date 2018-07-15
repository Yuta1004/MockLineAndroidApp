package work.nityc_nyuta.mockline.Fragments

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.*
import work.nityc_nyuta.mockline.R

class FriendsViewFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_friends_view, container, false)
    }
}
