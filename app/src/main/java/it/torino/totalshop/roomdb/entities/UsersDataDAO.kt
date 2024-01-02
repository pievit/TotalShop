package it.torino.totalshop.roomdb.entities

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy

@Dao
interface UsersDataDAO {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(usersData: UsersData)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAll(vararg usersData: UsersData)

    @Delete
    fun delete(usersData: UsersData?)

    @Delete
    fun deleteAll(vararg usersData: UsersData?)
}