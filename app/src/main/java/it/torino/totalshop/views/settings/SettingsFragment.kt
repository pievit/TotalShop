package it.torino.totalshop.views.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import it.torino.totalshop.R
import it.torino.totalshop.RoomViewModel

class SettingsFragment : Fragment() {

    private var vm: RoomViewModel? = null

    override fun onCreateView(inflater: LayoutInflater,container: ViewGroup?,savedInstanceState: Bundle?): View? {
        vm = ViewModelProvider(requireActivity())[RoomViewModel::class.java]
        return inflater.inflate(R.layout.user_settings, container,false)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        var fragMan = childFragmentManager.beginTransaction()
        var fraglist = SettingsListFragment()
        fragMan.replace(R.id.user_settings_fragmentview,fraglist)
        fragMan.commit()


    }
}