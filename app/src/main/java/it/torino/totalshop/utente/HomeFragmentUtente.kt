package it.torino.totalshop.utente

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
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


        vm?.storesList?.observe(viewLifecycleOwner){
            adapter = storeAdapter(mList)
            recyclerView.adapter = adapter
        }
    }
}