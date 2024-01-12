package it.torino.totalshop

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import it.torino.totalshop.roomdb.entities.OrdersData
import it.torino.totalshop.roomdb.entities.ProductsData
import it.torino.totalshop.roomdb.entities.StoreData
import java.lang.Float

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
        var c = ordList[position].listaProd.prods.values.sum()
        var item = ordList[position]
        holder.stato.text = ordList[position].status
//        holder.dataOrd.text = ordList[position].data

        holder.countTot.text = if (c > 1) {"$c articoli"} else {"$c articolo"}

        var sum = ordList[position].listaProd.prods.keys.sumOf{
                key ->
            (key.price * ordList[position].listaProd.prods.get(key)!!).toDouble()
        }
        holder.prezzoTot.text = sum.toString() + "€"

        holder.itemView.setOnClickListener{
            onItemClick(item)
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