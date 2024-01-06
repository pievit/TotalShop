package it.torino.totalshop.roomdb.entities

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface StoreDataDAO {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(storeData: StoreData)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAll(vararg storeData: StoreData)

    @Query("SELECT * FROM StoreData")
    fun getAllStores(): MutableList<StoreData>?

    @Query("SELECT * FROM StoreData WHERE StoreData.owner=:email")
    fun getStore(email:String): StoreData?

    @Delete
    fun delete(storeData: StoreData)

    @Delete
    fun deleteAll(vararg storeData: StoreData)
}