package it.torino.totalshop.roomdb.entities

import androidx.annotation.NonNull
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(indices = [Index(value = ["id","owner"], unique=true)], foreignKeys = [ForeignKey(UsersData::class, arrayOf("email") ,arrayOf("owner"), ForeignKey.CASCADE)])
class StoreData(var storeName:String,var storeAddress: String,var storeCategory: String, var owner: String) {
    @PrimaryKey(autoGenerate = true)
    @NonNull
    var id: Int = 0



    override fun toString(): String {
        return "ID: "+id+", StoreName: "+storeName+", Address: "+storeAddress+", Category: "+storeCategory+", Owner: "+owner
    }
}