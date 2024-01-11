package it.torino.totalshop

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import it.torino.totalshop.roomdb.entities.ProductsData
import it.torino.totalshop.roomdb.entities.StoreData
import it.torino.totalshop.utente.UtenteProdListOrders
import it.torino.totalshop.venditore.HomeFragmentVenditore
import it.torino.totalshop.venditore.VenditoreActivity

class ProdsOrdersAdapter(var pList: List<ProductsData>,val activity: UtenteProdListOrders): RecyclerView.Adapter<ProdsOrdersAdapter.ProdsViewHolder>() {
    inner class ProdsViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        val nomeP : TextView = itemView.findViewById(R.id.userProdName)
        val descP : TextView = itemView.findViewById(R.id.userProdDesc)
        val priceP : TextView = itemView.findViewById(R.id.userProdPrice)
        val addP: ImageView = itemView.findViewById(R.id.addProd)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProdsOrdersAdapter.ProdsViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.prods_store_item_list , parent , false)
        return ProdsViewHolder(view)
    }

    override fun onBindViewHolder(holder: ProdsViewHolder, position: Int) {
        holder.nomeP.text = pList[position].name
        holder.descP.text = pList[position].description
        holder.priceP.text = pList[position].price.toString() + "€"
        holder.addP.setOnClickListener{
            activity.showQuantityDialog(pList[position])
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