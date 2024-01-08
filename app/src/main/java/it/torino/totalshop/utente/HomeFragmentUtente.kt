package it.torino.totalshop.utente

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
//import android.widget.SearchView
import androidx.appcompat.widget.SearchView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import it.torino.totalshop.LocationViewModel
import it.torino.totalshop.R
import it.torino.totalshop.roomdb.entities.StoreData
import it.torino.totalshop.storeAdapter
import it.torino.totalshop.viewModel
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

class HomeFragmentUtente: Fragment() {
    var vm: viewModel? = null
    var locationVM: LocationViewModel? = null
    var radius: Int = 500
    private lateinit var recyclerView: RecyclerView
    private lateinit var searchView: SearchView
    private var mList = ArrayList<StoreData>()
    private lateinit var adapter: storeAdapter
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        vm = ViewModelProvider(requireActivity())[viewModel::class.java]
        locationVM = ViewModelProvider(requireActivity())[LocationViewModel::class.java]
        return inflater.inflate(R.layout.utente_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerView = view.findViewById(R.id.storeList)
        searchView = view.findViewById(R.id.searchBar)

        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = LinearLayoutManager(requireActivity())
//        addDataToList()
        adapter = storeAdapter(mList) { selectedItem ->
            Log.d("Item clicked: ",selectedItem.toString())
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

        vm?.getStores()

        vm!!.storesList.observe(viewLifecycleOwner){
                sl ->
            Log.d("debug",sl.toString())
            if(sl!=null){
                mList = sl as ArrayList<StoreData>
                adapter.setFilteredList(mList)
                locationVM?.getCoord()
            }
        }


        locationVM!!.locationData.observe(viewLifecycleOwner){
            coord ->
                var nearStoreList: ArrayList<StoreData> = ArrayList()

                for(r: StoreData in vm!!.storesList.value!!){
                    if(r.lat != null && r.lon != null){
                        val R = 6371e3; // metres
                        val var1 = coord.latitude * Math.PI/180; // φ, λ in radians
                        val var2 = (r.lat!! * Math.PI)/180;
                        val delt1 = (r.lat!!-coord.latitude) * Math.PI/180;
                        val delt2 = (r.lon!!-coord.longitude) * Math.PI/180;

                        val a = sin(delt1/2) * sin(delt1/2) +
                                cos(var1) * cos(var2) *
                                sin(delt2/2) * sin(delt2/2);
                        val c = 2 * atan2(sqrt(a), sqrt(1-a));

                        val d = R * c; //distanza in metri

                        if(d<= radius){
                            nearStoreList.add(r)
                        }
                    }

                }

                mList = nearStoreList
                Log.d("Test",mList.toString())
                adapter.setFilteredList(mList)


        }
    }

    private fun updateList(newText: String?){
        val newList = ArrayList<StoreData?>()
        if(newText!=null){
            for(i in mList){
                if(i.storeName.contains(newText as CharSequence,true) || i.storeCategory.contains(newText as CharSequence,true)){
                    newList.add(i)
                }
            }
        }
        adapter.setFilteredList(newList as List<StoreData>)

    }
}