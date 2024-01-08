package it.torino.totalshop.roomdb.entities

import androidx.annotation.NonNull
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import it.torino.totalshop.roomdb.ProdsList


@Entity(indices = [Index("id")], foreignKeys = arrayOf(ForeignKey(StoreData::class, arrayOf("id") ,arrayOf("storeId")), ForeignKey(UsersData::class, arrayOf("email"),
    arrayOf("usermail"),ForeignKey.CASCADE)  ))
class OrdersData(var listaProd: ProdsList, var usermail: String, var storeId: Int, var status: String, var comment: String) {
    @PrimaryKey(autoGenerate = true)
    @NonNull
    var id: Int = 0

    override fun toString(): String {
        return "Order ID: "+id+",Products List: "+listaProd.toString()+", User Mail: "+usermail+", Store ID: "+storeId+", Status: "+status+", Comment: "+comment
    }
}