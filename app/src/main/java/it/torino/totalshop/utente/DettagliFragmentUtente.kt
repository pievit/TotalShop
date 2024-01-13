package it.torino.totalshop.utente

import android.content.res.Configuration
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import it.torino.totalshop.R
import it.torino.totalshop.roomdb.ProdsList
import it.torino.totalshop.roomdb.entities.OrdersData
import it.torino.totalshop.roomdb.entities.ProductsData
import it.torino.totalshop.viewModel
import org.apache.commons.text.StringEscapeUtils
import kotlin.math.roundToInt

class DettagliFragmentUtente : Fragment() {
    var vm: viewModel? = null
    var order : OrdersData? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.utente_ordini_info, container, false)
        view.findViewById<FloatingActionButton>(R.id.user_floatingbtn).setOnClickListener {
            parentFragmentManager.popBackStack()
        }
        if(isOrientationLandscape()){
            view.findViewById<FloatingActionButton>(R.id.user_floatingbtn).visibility = View.INVISIBLE
        }else{
            view.findViewById<FloatingActionButton>(R.id.user_floatingbtn).visibility = View.VISIBLE
        }
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        if(order != null){
            update()
        }

        vm?.store?.observe(viewLifecycleOwner){
            store->
            view.findViewById<TextView>(R.id.user_ord_inf_store)?.text = store.storeName
        }

    }
    fun update(){

        vm?.getStoreFromId(order!!.storeId)
        var lp = order?.listaProd
        if(lp?.length!=0) {
            lp = StringEscapeUtils.unescapeJava(lp)
            val type = object : TypeToken<Map<String, Int>>() {}.type
            val objectMap: Map<String, Int> = Gson().fromJson(lp, type)
            var prods: MutableMap<ProductsData, Int> = mutableMapOf<ProductsData, Int>()

            objectMap.keys.forEach {
                var v = it.substring(13, it.length - 2).split("',")
                var p = ProductsData(
                    name = v[0].split("='")[1], description = v[1].split("='")[1],
                    price = v[2].split("='")[1].toFloat(), storeId = v[3].split("='")[1].toInt()
                )
                prods[p] = objectMap.get(it)!!
            }

            view?.findViewById<TextView>(R.id.user_ord_inf_id)?.text = "Ordine N." + order?.id
            view?.findViewById<TextView>(R.id.user_ord_inf_data)?.text = order?.dataOrd
            view?.findViewById<TextView>(R.id.user_ord_inf_status)?.text = order?.status
            view?.findViewById<TextView>(R.id.user_ord_inf_commento)?.text = order?.comment

            var sum = prods.keys.sumOf { key ->
                (key.price * prods.get(key)!!).toDouble()
            }

            view?.findViewById<TextView>(R.id.user_ord_inf_totprice)?.text = "Prezzo Totale: "+((sum*100).roundToInt().toDouble()/100).toString() + " €"
            view?.findViewById<TextView>(R.id.user_ord_inf_prodlist)?.text = ProdsList(prods).toString()
        }

    }

    fun isOrientationLandscape(): Boolean {
        val orientation = resources.configuration.orientation
        return orientation == Configuration.ORIENTATION_LANDSCAPE
    }
}