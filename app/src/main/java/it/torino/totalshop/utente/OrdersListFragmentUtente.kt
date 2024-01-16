package it.torino.totalshop.utente

import android.content.res.Configuration
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import it.torino.totalshop.R
import it.torino.totalshop.ordAdapter
import it.torino.totalshop.roomdb.entities.OrdersData
import it.torino.totalshop.roomdb.entities.StoreData
import it.torino.totalshop.viewModel

class OrdersListFragmentUtente : Fragment() {

    var vm: viewModel? = null
    private lateinit var recyclerView: RecyclerView
    private var ordList = ArrayList<OrdersData>()
    private lateinit var adapter: ordAdapter
    private var frag2 = DettagliFragmentUtente()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        var view = inflater.inflate(R.layout.utente_ordini_list,container,false)
        return view
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        vm = ViewModelProvider(requireActivity())[viewModel::class.java]

        recyclerView = view.findViewById(R.id.utenteOrdersList)
        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = LinearLayoutManager(requireActivity())
        adapter = ordAdapter(ordList) { selectedItem ->
            frag2.order = selectedItem
            if(isOrientationLandscape()){
                //carica nel fragment
                with(parentFragmentManager.beginTransaction()) {
                    replace(R.id.utenteOrdiniInfoFragment, frag2 )
                    setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                    addToBackStack("utente_ord_info_list")
                    commit()
                }


                Log.d("YOLA","YOLA")
//                val newDetails = DettagliFragmentVenditore.newInstance(myCourseCode)
//                childFragmentManager.beginTransaction().replace(R.id.ordini_info, newDetails)
//                setTransition(androidx.fragment.app.FragmentTransaction.TRANSIT_FRAGMENT_FADE)
//                commit()
            } else {
                //due pagine diverse
                Log.d("YOLA","YOLISSIMA")
                with(parentFragmentManager.beginTransaction()) {
                    replace(R.id.utenteOrdiniListFragment, frag2 )
                    setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                    addToBackStack("utente_ord_info_list")
                    commit()
                }
            }
            Log.d("Item clicked: ",selectedItem.toString())
        }
        recyclerView.adapter = adapter
        var noordtext : TextView = view.findViewById(R.id.utente_no_ord)
        Log.d("Test", requireActivity().intent.getStringExtra("email")!!)
        vm?.getAllOrdersFromEmail(requireActivity().intent.getStringExtra("email")!!)
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