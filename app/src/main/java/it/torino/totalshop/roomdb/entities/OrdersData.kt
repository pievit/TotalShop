package it.torino.totalshop.roomdb.entities

import androidx.annotation.NonNull
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import it.torino.totalshop.roomdb.ProdsConverter
import it.torino.totalshop.roomdb.ProdsList
import java.util.Date

@Entity(
    indices = [Index("id")], foreignKeys = [
        ForeignKey(
            StoreData::class,
            arrayOf("id"),
            arrayOf("storeId")
        ),
        ForeignKey(
            UsersData::class,
            arrayOf("email"),
            arrayOf("usermail")
        )
    ]
)
class OrdersData(
    var listaProd: String,
    var usermail: String,
    var storeId: Int,
    var status: String,
    var comment: String,
    var dataOrd: String
) {
    @PrimaryKey(autoGenerate = true)
    @NonNull
    var id: Int = 0

    override fun toString(): String {
        return "Order ID: " + id + ",Data Ordine: " + dataOrd + ",Products List: " + listaProd + ", User Mail: " + usermail + ", Store ID: " + storeId + ", Status: " + status + ", Comment: " + comment
    }
}