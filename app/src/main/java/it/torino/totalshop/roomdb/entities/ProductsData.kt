package it.torino.totalshop.roomdb.entities

import androidx.annotation.NonNull
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity(
    indices = [Index("id")],
    foreignKeys = [ForeignKey(
        StoreData::class,
        arrayOf("id"),
        arrayOf("storeId"),
        ForeignKey.CASCADE
    )]
)
class ProductsData(
    @SerializedName("name") var name: String,
    @SerializedName("description") var description: String,
    @SerializedName("price") var price: Float,
    @SerializedName("storeId") var storeId: Int
) {
    @PrimaryKey(autoGenerate = true)
    @NonNull
    @SerializedName("id")
    var id: Int = 0

    override fun toString(): String {
        return "ProductsData(name='" + name + "', description='" + description + "', price='" + price + "', storeId='" + storeId +"')"
    }
}