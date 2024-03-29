package it.torino.totalshop.roomdb.entities

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface OrdersDataDAO {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(ordersData: OrdersData)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAll(vararg ordersData: OrdersData)

    @Query("SELECT * FROM OrdersData WHERE usermail=:email ORDER BY id DESC")
    fun getOrdersFromMail(email: String): MutableList<OrdersData>?

    @Query("SELECT * FROM OrdersData WHERE storeId=:id ORDER BY id DESC")
    fun getOrdersFromStoreID(id: Int): MutableList<OrdersData>?

    @Query("SELECT * FROM OrdersData")
    fun getAllOrders(): MutableList<OrdersData>?


    @Delete
    fun delete(ordersData: OrdersData)

    @Delete
    fun deleteAll(vararg ordersData: OrdersData)
}