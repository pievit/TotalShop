package it.torino.totalshop


import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.PopupMenu
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class UserSettingsAdapter(var context: Context, var settingsMenu: ArrayList<Pair<String,String>>, val onItemClick: (String) -> Unit) : RecyclerView.Adapter<UserSettingsAdapter.SettingsViewHolder>(){


    inner class SettingsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val image : ImageView = itemView.findViewById(R.id.user_settings_image)
        val settings : TextView = itemView.findViewById(R.id.user_settings_settname)


    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SettingsViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.user_settings_card , parent , false)
        return SettingsViewHolder(view)
    }

    override fun getItemCount(): Int {
        return settingsMenu.size
    }

    override fun onBindViewHolder(holder: SettingsViewHolder, position: Int) {
        val imgsrc = context.resources.getIdentifier(settingsMenu.get(position).first,"drawable",context.packageName)
        holder.image.setImageResource(imgsrc)
        holder.settings.text = settingsMenu[position].second
        holder.itemView.setOnClickListener{
            onItemClick(settingsMenu[position].second)
        }
    }



    fun setFilteredList(menu: ArrayList<Pair<String,String>>){
        this.settingsMenu = menu
        notifyDataSetChanged()
    }


}