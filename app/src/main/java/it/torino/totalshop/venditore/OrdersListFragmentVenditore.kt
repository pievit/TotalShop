package it.torino.totalshop.venditore

import android.content.res.Configuration
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
        recyclerView = view.findViewById(R.id.ordersList)
        Log.d("test","entrato nel robo")
        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = LinearLayoutManager(requireActivity())
        adapter = ordAdapter(ordList) { selectedItem ->
            frag2.order = selectedItem
            if(isOrientationPortrait()){
                //carica nel fragment
                frag2.update()
//                val newDetails = DettagliFragmentVenditore.newInstance(myCourseCode)
//                childFragmentManager.beginTransaction().replace(R.id.ordini_info, newDetails)
//                setTransition(androidx.fragment.app.FragmentTransaction.TRANSIT_FRAGMENT_FADE)
//                commit()
            } else {

                with(childFragmentManager.beginTransaction()) {
                    replace(R.id.ordiniListFragment, frag2 )
                    setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                    commit()
                }
            }
            Log.d("Item clicked: ",selectedItem.toString())
        }
        recyclerView.adapter = adapter
        vm?.getStore(requireActivity().intent.getStringExtra("email")!!)
        vm?.store?.observe(viewLifecycleOwner){
                store ->
            myStore = store
            vm?.getAllOrdersFromStoreID(myStore.id)
        }
        vm!!.ordList.observe(viewLifecycleOwner){
                pl ->
            if(pl!=null){
                ordList = pl as ArrayList<OrdersData>
                adapter.setFilteredList(ordList)
            }
        }
    }

    fun isOrientationPortrait(): Boolean {
        val orientation = resources.configuration.orientation
        return orientation == Configuration.ORIENTATION_PORTRAIT
    }
}