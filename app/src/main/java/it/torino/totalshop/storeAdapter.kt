package it.torino.totalshop

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import it.torino.totalshop.roomdb.entities.StoreData

class storeAdapter(var mList: List<StoreData>) : RecyclerView.Adapter<storeAdapter.LanguageViewHolder>(){

    inner class LanguageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val logo : ImageView = itemView.findViewById(R.id.storeLogo)
        val storeName : TextView = itemView.findViewById(R.id.storeName)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LanguageViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.store_item_list , parent , false)
        return LanguageViewHolder(view)
    }

    override fun onBindViewHolder(holder: LanguageViewHolder, position: Int) {
        holder.logo.setImageResource(R.drawable.store_logo_24px)
        holder.storeName.text = mList[position].storeName
    }

    override fun getItemCount(): Int {
        return mList.size
    }
}