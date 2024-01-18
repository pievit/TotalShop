package it.torino.totalshop.views.venditore

import android.content.res.Configuration
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.ViewModelProvider
import it.torino.totalshop.R
import it.torino.totalshop.RoomViewModel

class OrdiniFragmentVenditore : Fragment() {
    var vm: RoomViewModel? = null
    private var frag1 = OrdersListFragmentVenditore()
    override fun onCreateView(inflater: LayoutInflater,container: ViewGroup?,savedInstanceState: Bundle?): View? {
        var view = inflater.inflate(R.layout.venditore_ordini, container, false)
        vm = ViewModelProvider(requireActivity())[RoomViewModel::class.java]

        with(childFragmentManager.beginTransaction()) {
            replace(R.id.venditoreOrdiniListFragment, frag1 )
            setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
            commit()
        }

        return view
    }

    fun isOrientationLandscape(): Boolean {
        val orientation = resources.configuration.orientation
        return orientation == Configuration.ORIENTATION_LANDSCAPE
    }



}