package it.torino.totalshop.utente

import android.app.AlertDialog
import android.os.Bundle
import android.util.JsonWriter
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.LinearLayout.LayoutParams
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.core.view.isInvisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import it.torino.totalshop.ProdsCartAdapter
import it.torino.totalshop.ProdsOrdersAdapter
import it.torino.totalshop.R
import it.torino.totalshop.roomdb.ProdsList
import it.torino.totalshop.roomdb.entities.OrdersData
import it.torino.totalshop.roomdb.entities.ProductsData
import it.torino.totalshop.viewModel
import java.io.StringWriter
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.math.roundToInt

class UtenteProdListOrders: Fragment() {
    var vm: viewModel? = null
    private lateinit var recyclerView: RecyclerView
    private lateinit var searchView: SearchView
    private var prodList = ArrayList<ProductsData>()
    private lateinit var adapter: ProdsOrdersAdapter
    private lateinit var cartProdAdapter: ProdsCartAdapter
    private lateinit var addDialog: AlertDialog
    private lateinit var dialogView: View
    private var prodMap: MutableMap<ProductsData,Int> = mutableMapOf()
    private lateinit var cartView: View
    private lateinit var cartDialog: AlertDialog
    private var newProdIns: Boolean = false
    override fun onCreateView(inflater: LayoutInflater,container: ViewGroup?,savedInstanceState: Bundle?): View? {
        var view = inflater.inflate(R.layout.utente_prodlist_ord,container,false)
        vm = ViewModelProvider(requireActivity())[viewModel::class.java]
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerView = view.findViewById(R.id.listaProdStore)
        searchView = view.findViewById(R.id.searchProdStoreBar)

        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = LinearLayoutManager(requireActivity())

        adapter = ProdsOrdersAdapter(prodList,this)
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
        view.findViewById<TextView>(R.id.titleProdStore).setText(requireArguments().getString("storeName"))
        view.findViewById<TextView>(R.id.categoryProdStore).setText("Categoria: "+requireArguments().getString("storeCategory"))
        view.findViewById<TextView>(R.id.addressProdStore).setText(requireArguments().getString("storeAddress"))
        vm?.getAllProdsFromStore(requireArguments().getInt("storeId"))
        vm?.prodsList?.observe(viewLifecycleOwner){
            pl ->
            prodList = pl as ArrayList<ProductsData>
            adapter.setFilteredList(prodList)
        }


        vm?.newOrder?.observe(viewLifecycleOwner){
            bool ->
            if(bool){
                Toast.makeText(requireActivity(),"Ordine creato con successo",Toast.LENGTH_SHORT).show()
                cartDialog.dismiss()
                findNavController().popBackStack()

            }
        }


        val builder = AlertDialog.Builder(requireActivity())
        val infl8r = requireActivity().layoutInflater
        dialogView = infl8r.inflate(R.layout.dialog_add_prod_to_cart,null)
        builder.setView(dialogView)
        addDialog = builder.create()


        val cartBuilder = AlertDialog.Builder(requireActivity())
        val inf = requireActivity().layoutInflater
        cartView = inf.inflate(R.layout.shopping_cart,null)
        cartBuilder.setView(cartView)
        cartDialog = cartBuilder.create()


        view.findViewById<FloatingActionButton>(R.id.floatingCart).setOnClickListener{
            showCartDialog()
        }

    }

    fun showQuantityDialog(prod: ProductsData){
        addDialog.show()
        dialogView.findViewById<TextView>(R.id.prodReminder).setText(prod.name+" ${prod.price}€")
        dialogView.findViewById<TextView>(R.id.subTotPrice).setText("${ if(prodMap.containsKey(prod)) prodMap.get(prod)!!*prod.price else  prod.price} €")
        dialogView.findViewById<TextView>(R.id.prodQuant).setText("${ if(prodMap.containsKey(prod)) prodMap.get(prod) else  1}")
        dialogView.findViewById<ImageButton>(R.id.butDecQuan).setOnClickListener{
            decProdQuantity(dialogView.findViewById<TextView>(R.id.prodQuant).text.toString().toInt(),prod.price)
        }
        dialogView.findViewById<ImageButton>(R.id.butIncQuan).setOnClickListener{
            incProdQuantity(dialogView.findViewById<TextView>(R.id.prodQuant).text.toString().toInt(),prod.price)
        }
        dialogView.findViewById<Button>(R.id.butIndietroQuant).setOnClickListener{
            addDialog.dismiss()
        }
        dialogView.findViewById<Button>(R.id.butAddtoCart).setOnClickListener{
            addProdToCart(prod)
        }

    }

    fun decProdQuantity(q: Int,price: Float){
        if(q>1){
            dialogView.findViewById<TextView>(R.id.prodQuant).setText("${q-1}")
            dialogView.findViewById<TextView>(R.id.subTotPrice).setText("${price*(q-1)} €")
        }
    }

    fun incProdQuantity(q: Int,price: Float){
        if(q<50){
            dialogView.findViewById<TextView>(R.id.prodQuant).setText("${q+1}")
            dialogView.findViewById<TextView>(R.id.subTotPrice).setText("${price*(q+1)} €")
        }
    }

    fun addProdToCart(prod: ProductsData){
        prodMap[prod] = dialogView.findViewById<TextView>(R.id.prodQuant).text.toString().toInt()

        view?.findViewById<FloatingActionButton>(R.id.floatingCart)?.visibility = VISIBLE
        addDialog.dismiss()
    }

    fun removeProdFromCart(prod: ProductsData,pos: Int){
        prodMap.remove(prod)
        cartProdAdapter.setFilteredList(prodMap.keys.toList(),prodMap)
        var tot = prodMap.keys.sumOf { key -> (key.price*prodMap.get(key)!!).toDouble() }
        cartView.findViewById<TextView>(R.id.totaleCart).setText(((tot*100).roundToInt().toDouble()/100).toString() +"€")
        Toast.makeText(requireActivity(),"Prodotto rimosso",Toast.LENGTH_SHORT).show()
    }

    fun showCartDialog(){
        cartProdAdapter = ProdsCartAdapter(prodMap.keys.toList(),prodMap,this)
        var cartRecycler =  cartView.findViewById<RecyclerView>(R.id.cartProdList)
        cartRecycler.setHasFixedSize(true)
        cartRecycler.layoutManager = LinearLayoutManager(requireActivity())
        cartRecycler.layoutParams.width = 1250
        cartRecycler.adapter = cartProdAdapter
        cartDialog.show()

        cartView.findViewById<Button>(R.id.butCartIndietro).setOnClickListener{
            cartDialog.dismiss()
        }
        cartView.findViewById<Button>(R.id.butCartOrder).setOnClickListener{
            addOrder()
        }
        var tot = prodMap.keys.sumOf { key -> (key.price*prodMap.get(key)!!).toDouble() }
        cartView.findViewById<TextView>(R.id.totaleCart).setText(((tot*100).roundToInt().toDouble()/100).toString() +"€")

    }

    fun addOrder(){
        val currentDate = Date()
        val df = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
        val orderDate = df.format(currentDate)
        val usermail = requireActivity().intent.getStringExtra("email")!!
        val storeId = requireArguments().getInt("storeId")
//        val type = object : TypeToken<Map<ProductsData, Int>>() {}.type
        val orderedProd: String = Gson().toJson(prodMap.toMap())
        val order = OrdersData(orderedProd,usermail,storeId,"nuovo","Primo ordine",orderDate)
        vm?.insertOrder(order)


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