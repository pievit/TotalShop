package it.torino.totalshop

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import it.torino.totalshop.roomdb.entities.StoreData

class storeAdapter(var mList: List<StoreData>, val onItemClick: (StoreData) -> Unit) : RecyclerView.Adapter<storeAdapter.StoresViewHolder>(){

    inner class StoresViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val logo : ImageView = itemView.findViewById(R.id.storeLogo)
        val storeName : TextView = itemView.findViewById(R.id.storeName)
        val storeCategory: TextView = itemView.findViewById(R.id.storeCategory)
        val storeAddress: TextView = itemView.findViewById(R.id.storeAddress)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StoresViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.store_item_list , parent , false)
        return StoresViewHolder(view)
    }

    override fun onBindViewHolder(holder: StoresViewHolder, position: Int) {
        var item = mList[position]
        holder.logo.setImageResource(R.drawable.store_logo_24px)
        holder.storeName.text = mList[position].storeName
        holder.storeCategory.text = "Categoria: "+mList[position].storeCategory
        holder.storeAddress.text = mList[position].storeAddress
        holder.itemView.setOnClickListener{
            onItemClick(item)
        }
    }



    override fun getItemCount(): Int {
        return mList.size
    }

    fun setFilteredList(mList: List<StoreData>){
        this.mList = mList
        notifyDataSetChanged()
    }


}