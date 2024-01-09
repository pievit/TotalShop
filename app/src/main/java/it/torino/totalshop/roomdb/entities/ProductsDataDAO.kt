package it.torino.totalshop.roomdb.entities

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface ProductsDataDAO {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(productsData: ProductsData)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAll(vararg productsData: ProductsData)

    @Query("SELECT * FROM ProductsData WHERE ProductsData.storeId=:storeId")
    fun getStoreProducts(storeId:Int): MutableList<ProductsData>?

    @Query("SELECT * FROM ProductsData")
    fun getAllProducts(): MutableList<ProductsData>?

    @Delete
    fun delete(productsData: ProductsData)

    @Delete
    fun deleteAll(vararg productsData: ProductsData)
}