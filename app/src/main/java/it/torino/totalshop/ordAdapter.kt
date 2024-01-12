package it.torino.totalshop

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import it.torino.totalshop.roomdb.ProdsList
import it.torino.totalshop.roomdb.entities.OrdersData
import it.torino.totalshop.roomdb.entities.ProductsData
import it.torino.totalshop.roomdb.entities.StoreData
import org.apache.commons.text.StringEscapeUtils
import java.lang.Float
import kotlin.math.roundToInt

class ordAdapter(var ordList: List<OrdersData>, val onItemClick: (OrdersData) -> Unit) : RecyclerView.Adapter<ordAdapter.OrdViewHolder>(){

    inner class OrdViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val stato : TextView = itemView.findViewById(R.id.statoOrd)
        val countTot : TextView = itemView.findViewById(R.id.countTot)
        val prezzoTot : TextView = itemView.findViewById(R.id.prezzoTot)
        val dataOrd : TextView = itemView.findViewById(R.id.dataOrd)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrdViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.orders_list , parent , false)
        return OrdViewHolder(view)
    }

    override fun onBindViewHolder(holder: OrdViewHolder, position: Int) {
        var lp = ordList[position].listaProd
        var ordProdList: ProdsList = ProdsList(emptyMap<ProductsData, Int>())
//        var ordProdList: ProdsList = ProdsList(emptyMap<ProductsData, Int>())
        if (lp.length != 0) {
            lp = StringEscapeUtils.unescapeJava(lp)
            Log.d("Debug",lp)
            val type = object : TypeToken<Map<String, Int>>() {}.type
            val objectMap: Map<String, Int> = Gson().fromJson(lp, type)


            // Convert the keys from String to Object
//        val objectMap: Map<ProductsData, Int> = map.mapKeys { it.key as ProductsData }

//            ordProdList = ProdsList(objectMap)

            var c = objectMap.values.sum()
//            var c = ordProdList.prods.values.sum()
            holder.stato.text = ordList[position].status
            holder.dataOrd.text = ordList[position].dataOrd

            holder.countTot.text = if (c > 1) {
                "$c articoli"
            } else {
                "$c articolo"
            }
            Log.d("Debug", objectMap.keys.toString())
            var prods : MutableMap<ProductsData, Int> = mutableMapOf<ProductsData, Int>()

            objectMap.keys.forEach {
                var v = it.substring(13,it.length-2).split("',")
                var p : ProductsData = ProductsData(name = v[0].split("='")[1], description = v[1].split("='")[1],
                    price = v[2].split("='")[1].toFloat(), storeId = v[3].split("='")[1].toInt())
                prods[p] = objectMap.get(it)!!
            }


            Log.d("Test1", prods.toString())

            var sum = prods.keys.sumOf{
                    key ->
                (key.price * prods.get(key)!!).toDouble()
            }
            holder.prezzoTot.text = ((sum*100).roundToInt().toDouble()/100).toString() + "€"

            var item = ordList[position]

            holder.itemView.setOnClickListener {
                onItemClick(item)
            }
        }
    }



    override fun getItemCount(): Int {
        return ordList.size
    }

    fun setFilteredList(ordList: List<OrdersData>){
        this.ordList = ordList
        notifyDataSetChanged()
    }


}