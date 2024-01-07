package it.torino.totalshop.utente

import android.os.Bundle
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
import it.torino.totalshop.R
import it.torino.totalshop.roomdb.entities.StoreData
import it.torino.totalshop.storeAdapter
import it.torino.totalshop.viewModel

class HomeFragmentUtente: Fragment() {
    var vm: viewModel? = null
    private lateinit var recyclerView: RecyclerView
    private lateinit var searchView: SearchView
    private var mList = ArrayList<StoreData>()
    private lateinit var adapter: storeAdapter
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        vm = ViewModelProvider(requireActivity())[viewModel::class.java]

        return inflater.inflate(R.layout.utente_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerView = view.findViewById(R.id.storeList)
        searchView = view.findViewById(R.id.searchBar)

        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = LinearLayoutManager(requireActivity())
//        addDataToList()
        adapter = storeAdapter(mList)
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

        vm?.storesList?.observe(viewLifecycleOwner){
                sl ->
            mList = sl as ArrayList<StoreData>
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
        if(newList.isEmpty()){
            Toast.makeText(requireActivity(),"Nessuno Store trovato.",Toast.LENGTH_SHORT).show()
        }else{
            adapter.setFilteredList(newList as List<StoreData>)
        }
    }
}