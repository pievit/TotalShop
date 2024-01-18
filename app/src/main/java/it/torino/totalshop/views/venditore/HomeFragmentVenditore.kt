package it.torino.totalshop.views.venditore

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import it.torino.totalshop.adapter.ProdsAdapter
import it.torino.totalshop.R
import it.torino.totalshop.roomdb.entities.ProductsData
import it.torino.totalshop.roomdb.entities.StoreData
import it.torino.totalshop.RoomViewModel

class HomeFragmentVenditore : Fragment() {
    var vm: RoomViewModel? = null
    private lateinit var recyclerView: RecyclerView
    private lateinit var searchView: SearchView
    private var prodList = ArrayList<ProductsData>()
    private lateinit var adapter: ProdsAdapter
    private lateinit var butNewProd: Button
    private lateinit var myStore: StoreData
    private lateinit var newProdDialog: AlertDialog
    private lateinit var newProdView: View
    private lateinit var delProd: ProductsData
    private lateinit var delDialog: AlertDialog
    override fun onCreateView(inflater: LayoutInflater,container: ViewGroup?,savedInstanceState: Bundle?): View? {
        vm = ViewModelProvider(requireActivity())[RoomViewModel::class.java]
        return inflater.inflate(R.layout.venditore_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerView = view.findViewById(R.id.listaProd)
        searchView = view.findViewById(R.id.searchProdBar)

        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = LinearLayoutManager(requireActivity())

        butNewProd = view.findViewById(R.id.butAddprod)

        butNewProd.setOnClickListener{
            newProdDialog.show()
            newProdView.findViewById<Button>(R.id.newprodbutindietro).setOnClickListener{
                newProdDialog.dismiss()
            }
            newProdView.findViewById<Button>(R.id.newprodbutagg).setOnClickListener{
                addProd()
            }
        }

        adapter = ProdsAdapter(prodList,this)
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


        vm?.getStore(requireActivity().intent.getStringExtra("email")!!)
        vm?.store?.observe(viewLifecycleOwner){
            store ->
            myStore = store
            vm?.getAllProdsFromStore(myStore.id)
        }

        vm?.prodsList?.observe(viewLifecycleOwner){
            pl ->
            prodList = pl as ArrayList<ProductsData>
            adapter.setFilteredList(prodList)
        }

        val builder = AlertDialog.Builder(requireActivity())
        val infl8r = requireActivity().layoutInflater
        newProdView = infl8r.inflate(R.layout.dialog_add_prods,null)
        builder.setView(newProdView)
        newProdDialog = builder.create()


        val deleteBuilder = AlertDialog.Builder(requireActivity())
        deleteBuilder.setMessage("Vuoi eliminare questo Prodotto ?")
            .setPositiveButton("Elimina"){
                    _, _ ->
                vm?.deleteProd(delProd)
                Toast.makeText(requireActivity(),"Prodotto eliminato.",Toast.LENGTH_SHORT).show()
            }

            .setNegativeButton("Indietro"){
                _, _ -> delDialog.dismiss()
            }
        delDialog = deleteBuilder.create()
    }

    fun addProd(){
        val nomeP = newProdView.findViewById<EditText>(R.id.newprodname).text.toString()
        val descP = newProdView.findViewById<EditText>(R.id.newproddesc).text.toString()
        val priceP = newProdView.findViewById<EditText>(R.id.newprodprice).text.toString().toFloat()
        val pd = ProductsData(nomeP,descP,priceP,myStore.id)
        var flag = false
        for(prod in prodList){
            if(prod.name.equals(nomeP,true)){
                flag = true
                break
            }
        }
        if(!flag){
            vm?.insertProd(pd)
            Toast.makeText(requireActivity(),"Prodotto aggiunto con successo.",Toast.LENGTH_SHORT).show()
        }else{
            Toast.makeText(requireActivity(),"Prodotto già presente.",Toast.LENGTH_SHORT).show()
        }

        newProdView.findViewById<EditText>(R.id.newprodname).setText("")
        newProdView.findViewById<EditText>(R.id.newproddesc).setText("")
        newProdView.findViewById<EditText>(R.id.newprodprice).setText("")
        newProdDialog.dismiss()

    }

    fun deleteProd(prod: ProductsData){
        delProd = prod
        delDialog.show()
    }

    fun updateList(newText: String?){
        var pList: List<ProductsData> = ArrayList()
        if(newText!=null){
            pList = prodList.filter {
                    prod -> prod.name.contains(newText,true) || prod.description.contains(newText,true)
            }
        }
        adapter.setFilteredList(pList)
    }
}