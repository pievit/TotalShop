package it.torino.totalshop.venditore

import android.content.res.Configuration
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentContainerView
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import it.torino.totalshop.R
import it.torino.totalshop.ordAdapter
import it.torino.totalshop.roomdb.entities.OrdersData
import it.torino.totalshop.roomdb.entities.StoreData
import it.torino.totalshop.viewModel

class OrdersListFragmentVenditore : Fragment() {

    var vm: viewModel? = null
    private lateinit var recyclerView: RecyclerView
    private var ordList = ArrayList<OrdersData>()
    private lateinit var adapter: ordAdapter
    private lateinit var myStore : StoreData
    private var frag2 = DettagliFragmentVenditore()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        var view = inflater.inflate(R.layout.venditore_ordini_list,container,false)
        return view
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        vm = ViewModelProvider(requireActivity())[viewModel::class.java]
        recyclerView = view.findViewById(R.id.venditoreOrdersList)
        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = LinearLayoutManager(requireActivity())
        adapter = ordAdapter(ordList) { selectedItem ->
            frag2.order = selectedItem
            if(isOrientationLandscape()){
                //carica nel fragment
                with(parentFragmentManager.beginTransaction()) {
                    replace(R.id.venditoreOrdiniInfoFragment, frag2 )
                    setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                    addToBackStack("utente_ord_info_list")
                    commit()
                }
//                val newDetails = DettagliFragmentVenditore.newInstance(myCourseCode)
//                childFragmentManager.beginTransaction().replace(R.id.ordini_info, newDetails)
//                setTransition(androidx.fragment.app.FragmentTransaction.TRANSIT_FRAGMENT_FADE)
//                commit()
                Log.d("YOLA","YOLA")
            } else {
                //due pagine diverse
                with(parentFragmentManager.beginTransaction()) {
                    replace(R.id.venditoreOrdiniListFragment, frag2 )
                    setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                    addToBackStack("ord_info_list")
                    commit()
                }
            }
            Log.d("Item clicked: ",selectedItem.toString())
        }
        recyclerView.adapter = adapter
        Log.d("Test", requireActivity().intent.getStringExtra("email")!!)
        vm?.getStore(requireActivity().intent.getStringExtra("email")!!)
        vm?.store?.observe(viewLifecycleOwner){
                store ->
            myStore = store
            Log.d("Test",myStore.storeName)
            vm?.getAllOrdersFromStoreID(myStore.id)
        }
        var noordtext : TextView = view.findViewById(R.id.venditore_no_ord)
        vm!!.ordList.observe(viewLifecycleOwner){
                pl ->
            Log.d("Test","qui")
            if(pl!=null){
                if(pl.size>0){
                    noordtext.visibility = View.GONE
                    Log.d("Test",pl.toString())
                    ordList = pl as ArrayList<OrdersData>
                    adapter.setFilteredList(ordList)
                }else{
                    noordtext.visibility = View.VISIBLE
                }
            }
        }
    }

    fun isOrientationLandscape(): Boolean {
        val orientation = resources.configuration.orientation
        return orientation == Configuration.ORIENTATION_LANDSCAPE
    }
}