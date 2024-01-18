package it.torino.totalshop.utente

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.AppCompatImageButton
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import it.torino.totalshop.LocationViewModel
import it.torino.totalshop.R
import it.torino.totalshop.RoomViewModel
import it.torino.totalshop.adapter.storeAdapter
import it.torino.totalshop.roomdb.entities.LocationData
import it.torino.totalshop.roomdb.entities.ProductsData
import it.torino.totalshop.roomdb.entities.StoreData
import kotlin.math.abs
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.pow
import kotlin.math.sin
import kotlin.math.sqrt


class HomeFragmentUtente: Fragment() {
    var vm: RoomViewModel? = null
    var locationVM: LocationViewModel? = null
    var radius: Int = 500
    private lateinit var recyclerView: RecyclerView
    private lateinit var searchView: SearchView
    private var mList = ArrayList<StoreData>()
    private var prodList = ArrayList<ProductsData>()
    private lateinit var adapter: storeAdapter
    lateinit var nostoretxt : TextView
    var myCoord: LocationData = LocationData(0.0,0.0)
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        vm = ViewModelProvider(requireActivity())[RoomViewModel::class.java]
        locationVM = ViewModelProvider(requireActivity())[LocationViewModel::class.java]
        return inflater.inflate(R.layout.utente_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        nostoretxt = view.findViewById(R.id.txtNoStoreArea)

        recyclerView = view.findViewById(R.id.storeList)
        searchView = view.findViewById(R.id.searchBar)

        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = LinearLayoutManager(requireActivity())

        adapter = storeAdapter(mList) { selectedItem ->
            arguments?.putInt("storeId",selectedItem.id)
            arguments?.putString("storeName",selectedItem.storeName)
            arguments?.putString("storeCategory",selectedItem.storeCategory)
            arguments?.putString("storeAddress",selectedItem.storeAddress)
            findNavController().navigate(R.id.utente_prod_sel,arguments)

        }
        recyclerView.adapter = adapter
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                updateList(newText)
                return true
            }

        })


        var filterSearch = view.findViewById<AppCompatImageButton>(R.id.filterSearch)
        val radiusMenu = PopupMenu(requireActivity(), filterSearch)
        radiusMenu.getMenuInflater().inflate(R.menu.menu_radius, radiusMenu.getMenu())
        radiusMenu.setOnMenuItemClickListener(PopupMenu.OnMenuItemClickListener { menuItem -> // Toast message on menu item clicked
            radius = menuItem.title.toString().toInt()
            Toast.makeText(requireActivity(),"Raggio di ricerca stores impostato su $radius metri.",Toast.LENGTH_SHORT).show()
            updateStoresList()
            true
        })

        filterSearch.setOnClickListener{
            radiusMenu.show()
        }

        vm?.getStores()
        vm?.getAllProds()

        vm!!.storesList.observe(viewLifecycleOwner){
                sl ->
            if(sl!=null){
                mList = sl as ArrayList<StoreData>
                adapter.setFilteredList(mList)
                locationVM?.getCoord()
            }
        }

        vm!!.prodsList.observe(viewLifecycleOwner){
            pl ->
            prodList = pl as ArrayList<ProductsData>
        }


        locationVM!!.locationData.observe(viewLifecycleOwner){
            coord ->
            myCoord = coord
            updateStoresList()
        }


    }

    private fun updateStoresList(){
        var nearStoreList: ArrayList<StoreData> = ArrayList()

        for(r: StoreData in mList){
            if(r.lat != null && r.lon != null){
                val earthRadius = 6371.0 // Earth's radius in kilometers

                val dLat = Math.toRadians(r.lat!! - myCoord.latitude)
                val dLon = Math.toRadians(r.lon!! - myCoord.longitude)

                val a = sin(dLat / 2) * sin(dLat / 2) +
                        cos(Math.toRadians(r.lat!!)) * cos(Math.toRadians(myCoord.latitude)) *
                        sin(dLon / 2) * sin(dLon / 2)

                val c = 2 * atan2(sqrt(a), sqrt(1 - a))

                val distance = earthRadius * c
                if(distance<= (radius/1000)){
                    nearStoreList.add(r)
                }
            }

        }

        if(nearStoreList.size == 0){
            nostoretxt.visibility = View.VISIBLE
        }else{
            nostoretxt.visibility = View.GONE
        }
        adapter.setFilteredList(nearStoreList)

    }

    private fun updateList(newText: String?){
        var negList: List<StoreData> = ArrayList()
        if(newText!=null){
             negList = mList.filter {
                negozio -> negozio.storeName.contains(newText as CharSequence,true) ||
                    prodList.any {
                        prodotto -> prodotto.storeId == negozio.id && prodotto.name.contains(newText as CharSequence,true)
                    }
            }
        }
        adapter.setFilteredList(negList)

    }
}