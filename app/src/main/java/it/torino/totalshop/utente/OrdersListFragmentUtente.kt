package it.torino.totalshop.utente

import android.content.res.Configuration
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import it.torino.totalshop.R
import it.torino.totalshop.adapter.ordAdapter
import it.torino.totalshop.roomdb.entities.OrdersData
import it.torino.totalshop.RoomViewModel

class OrdersListFragmentUtente : Fragment() {

    var vm: RoomViewModel? = null
    private lateinit var recyclerView: RecyclerView
    private var ordList = ArrayList<OrdersData>()
    private lateinit var adapter: ordAdapter
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
        vm = ViewModelProvider(requireActivity())[RoomViewModel::class.java]

        recyclerView = view.findViewById(R.id.utenteOrdersList)
        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = LinearLayoutManager(requireActivity())
        adapter = ordAdapter(ordList) { selectedItem ->
            val frag2 = DettagliFragmentUtente()
            frag2.order = selectedItem
            if(isOrientationLandscape()){
                //carica nel fragment
                with(parentFragmentManager.beginTransaction()) {
                    replace(R.id.utenteOrdiniInfoFragment, frag2 )
                    setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                    commit()
                }

            } else {
                //due pagine diverse
                with(parentFragmentManager.beginTransaction()) {
                    replace(R.id.utenteOrdiniListFragment, frag2 )
                    setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                    addToBackStack("utente_ord_info_list")
                    commit()
                }
            }
        }
        recyclerView.adapter = adapter
        var noordtext : TextView = view.findViewById(R.id.utente_no_ord)
        vm?.getAllOrdersFromEmail(requireActivity().intent.getStringExtra("email")!!)
        vm!!.ordList.observe(viewLifecycleOwner){
                pl ->
            if(pl!=null){
                if(pl.size>0){
                    noordtext.visibility = View.GONE
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