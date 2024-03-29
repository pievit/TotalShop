package it.torino.totalshop.views.utente

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.gson.Gson
import it.torino.totalshop.R
import it.torino.totalshop.RoomViewModel
import it.torino.totalshop.adapter.ProdsCartAdapter
import it.torino.totalshop.adapter.ProdsOrdersAdapter
import it.torino.totalshop.roomdb.entities.OrdersData
import it.torino.totalshop.roomdb.entities.ProductsData
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.math.roundToInt


class UtenteProdListOrders: Fragment() {
    var vm: RoomViewModel? = null
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
        vm = ViewModelProvider(requireActivity())[RoomViewModel::class.java]
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
            if(newProdIns && bool){
                newProdIns = false
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
        dialogView.findViewById<TextView>(R.id.prodReminder).setText(prod.name+" "+((prod.price*100).roundToInt().toDouble()/100).toString()+" €")
        dialogView.findViewById<TextView>(R.id.subTotPrice).setText(if(prodMap.containsKey(prod)) ((prodMap.get(prod)!!*prod.price*100).roundToInt().toDouble()/100).toString() else  ((prod.price*100).roundToInt().toDouble()/100).toString()+" €")
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
            dialogView.findViewById<TextView>(R.id.subTotPrice).setText(((price*(q-1)*100).roundToInt().toDouble()/100).toString() +" €")
        }
    }

    fun incProdQuantity(q: Int,price: Float){
        if(q<50){
            dialogView.findViewById<TextView>(R.id.prodQuant).setText("${q+1}")
            dialogView.findViewById<TextView>(R.id.subTotPrice).setText(((price*(q+1)*100).roundToInt().toDouble()/100).toString() +" €")
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
        val windowManager = context?.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val displayMetrics = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(displayMetrics)

        cartRecycler.layoutParams.width = (displayMetrics.widthPixels*0.85).toInt()
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
        val order = OrdersData(orderedProd,usermail,storeId,"nuovo","",orderDate)
        vm?.insertOrder(order)
        newProdIns = true


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