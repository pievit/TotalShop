package it.torino.totalshop.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import it.torino.totalshop.R
import it.torino.totalshop.roomdb.entities.ProductsData
import it.torino.totalshop.views.venditore.HomeFragmentVenditore

class ProdsAdapter(var pList: List<ProductsData>,val activity: HomeFragmentVenditore): RecyclerView.Adapter<ProdsAdapter.ProdsViewHolder>() {
    inner class ProdsViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        val nomeP : TextView = itemView.findViewById(R.id.prodName)
        val descP : TextView = itemView.findViewById(R.id.prodDesc)
        val priceP : TextView = itemView.findViewById(R.id.prodPrice)
        val deleteP: ImageView = itemView.findViewById(R.id.deleteProd)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProdsViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.prods_item_list, parent , false)
        return ProdsViewHolder(view)
    }

    override fun onBindViewHolder(holder: ProdsViewHolder, position: Int) {
        holder.nomeP.text = pList[position].name
        holder.descP.text = pList[position].description
        holder.priceP.text = pList[position].price.toString() + "€"
        holder.deleteP.setOnClickListener{
            activity.deleteProd(pList[position])
        }
    }

    override fun getItemCount(): Int {
        return pList.size
    }

    fun setFilteredList(pList: List<ProductsData>){
        this.pList = pList
        notifyDataSetChanged()
    }
}