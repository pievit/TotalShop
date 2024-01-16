package it.torino.totalshop.utente

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

class OrdiniFragment : Fragment() {
    var vm: viewModel? = null
    private var frag1 = OrdersListFragmentUtente()
    private var frag2 = DettagliFragmentUtente()
    override fun onCreateView(inflater: LayoutInflater,container: ViewGroup?,savedInstanceState: Bundle?): View? {
        var view = inflater.inflate(R.layout.utente_ordini, container, false)
        vm = ViewModelProvider(requireActivity())[viewModel::class.java]


        if(isOrientationLandscape()){
            var fm = childFragmentManager.beginTransaction()
            Log.d("Test","landscape")
            fm.replace(R.id.utenteOrdiniListFragment,frag1)
//            fm.commit()
//            fm = childFragmentManager.beginTransaction()
            fm.commit()

            //carica nel fragment
//                val newDetails = DettagliFragmentVenditore.newInstance(myCourseCode)
//                childFragmentManager.beginTransaction().replace(R.id.ordini_info, newDetails)
//                setTransition(androidx.fragment.app.FragmentTransaction.TRANSIT_FRAGMENT_FADE)
//                commit()
        } else {
            Log.d("Test","NOT landscape")
            with(childFragmentManager.beginTransaction()) {
                replace(R.id.utenteOrdiniListFragment, frag1 )
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

    fun isOrientationLandscape(): Boolean {
        val orientation = resources.configuration.orientation
        return orientation == Configuration.ORIENTATION_LANDSCAPE
    }



}