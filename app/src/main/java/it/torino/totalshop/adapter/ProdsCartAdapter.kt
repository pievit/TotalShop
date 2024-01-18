package it.torino.totalshop.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.LayoutParams
import it.torino.totalshop.R
import it.torino.totalshop.roomdb.entities.ProductsData
import it.torino.totalshop.views.utente.UtenteProdListOrders

class ProdsCartAdapter(var pList: List<ProductsData>,var pMap: Map<ProductsData,Int>,val activity: UtenteProdListOrders): RecyclerView.Adapter<ProdsCartAdapter.ProdsViewHolder>() {
    inner class ProdsViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        val nomeP : TextView = itemView.findViewById(R.id.prodNameCart)
        val descP : TextView = itemView.findViewById(R.id.prodDescCart)
        val priceP : TextView = itemView.findViewById(R.id.prodPriceCart)
        val quantityP: TextView =  itemView.findViewById(R.id.prodQuantityCart)
        val deleteP: ImageView = itemView.findViewById(R.id.deleteProdCart)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProdsViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.prods_cart_list, parent , false)
        return ProdsViewHolder(view)
    }

    override fun onBindViewHolder(holder: ProdsViewHolder, position: Int) {
        holder.nomeP.text = pList[position].name
        holder.descP.text = pList[position].description
        holder.priceP.text = pList[position].price.toString() + "€"
        holder.quantityP.text = "Quantità:  "+pMap.get(pList[position]).toString()
        holder.deleteP.setOnClickListener{
            activity.removeProdFromCart(pList[position],position)
        }
        holder.itemView.layoutParams.width = LayoutParams.MATCH_PARENT

    }

    override fun getItemCount(): Int {
        return pList.size
    }

    fun setFilteredList(pList: List<ProductsData>,pMap: Map<ProductsData,Int>){
        this.pList = pList
        this.pMap = pMap
        notifyDataSetChanged()
    }
}