package it.torino.totalshop.venditore

import android.content.res.Configuration
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentContainerView
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import it.torino.totalshop.R
import it.torino.totalshop.ordAdapter
import it.torino.totalshop.roomdb.entities.OrdersData
import it.torino.totalshop.roomdb.entities.StoreData
import it.torino.totalshop.viewModel

class OrdiniFragmentVenditore : Fragment() {
    var vm: viewModel? = null
    private lateinit var recyclerView: RecyclerView
    private var ordList = ArrayList<OrdersData>()
    private lateinit var adapter: ordAdapter
    private lateinit var myStore : StoreData
    private var frag1 = OrdersListFragmentVenditore()
    private var frag2 = DettagliFragmentVenditore()
    override fun onCreateView(inflater: LayoutInflater,container: ViewGroup?,savedInstanceState: Bundle?): View? {
        var view = inflater.inflate(R.layout.venditore_ordini, container, false)
        vm = ViewModelProvider(requireActivity())[viewModel::class.java]


        if(isOrientationPortrait()){
            var fm = childFragmentManager.beginTransaction()
            fm.add(R.id.ordiniListFragment,frag1)
//            fm.commit()
//            fm = childFragmentManager.beginTransaction()
            fm.add(R.id.ordiniInfoFragment,frag2)
            fm.commit()

            //carica nel fragment
//                val newDetails = DettagliFragmentVenditore.newInstance(myCourseCode)
//                childFragmentManager.beginTransaction().replace(R.id.ordini_info, newDetails)
//                setTransition(androidx.fragment.app.FragmentTransaction.TRANSIT_FRAGMENT_FADE)
//                commit()
        } else {
            with(childFragmentManager.beginTransaction()) {
                replace(R.id.ordiniListFragment, frag1 )
//                    arguments?.putBundle("OrderInfo", bun)
                setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                commit()
            }

        }
//        var fm = childFragmentManager.beginTransaction()
//        fm.replace(R.id.ordiniInfoFragment,OrdersListFragmentVenditore())
//
//        fm.add(R.id.ordiniInfoFragment,frag1)
//        fm.commit()
//        Log.d("test","logtest")
        return view
    }

    fun isOrientationPortrait(): Boolean {
        val orientation = resources.configuration.orientation
        return orientation == Configuration.ORIENTATION_PORTRAIT
    }



}