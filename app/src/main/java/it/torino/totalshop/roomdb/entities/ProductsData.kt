package it.torino.totalshop.roomdb.entities

import androidx.annotation.NonNull
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(indices = [Index("id")], foreignKeys = [ForeignKey(StoreData::class, arrayOf("Id") ,arrayOf("storeId"), ForeignKey.CASCADE)])
class ProductsData(var name: String,var description: String,var price: Float,var storeId: Int) {
    @PrimaryKey(autoGenerate = true)
    @NonNull
    var id: Int = 0

    override fun toString(): String {
        return "ID: "+name+", Description: "+description+", Price: "+price+", storeId: "+storeId
    }
}