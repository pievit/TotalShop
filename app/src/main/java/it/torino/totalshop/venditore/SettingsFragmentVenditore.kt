package it.torino.totalshop.venditore

import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import it.torino.totalshop.R
import it.torino.totalshop.UserSettingsAdapter
import it.torino.totalshop.roomdb.entities.StoreData
import it.torino.totalshop.roomdb.entities.UsersData
import it.torino.totalshop.viewModel

class SettingsFragmentVenditore : Fragment() {
    private var vm: viewModel? = null
    private lateinit var user: UsersData
    private lateinit var store: StoreData
    private lateinit var recview: RecyclerView
    private lateinit var adapter: UserSettingsAdapter

    override fun onCreateView(inflater: LayoutInflater,container: ViewGroup?,savedInstanceState: Bundle?): View? {
        vm = ViewModelProvider(requireActivity())[viewModel::class.java]
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