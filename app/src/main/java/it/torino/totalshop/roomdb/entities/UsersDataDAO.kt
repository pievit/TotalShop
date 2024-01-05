package it.torino.totalshop.roomdb.entities

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface UsersDataDAO {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(usersData: UsersData)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAll(vararg usersData: UsersData)

    @Query("SELECT * FROM UsersData")
    fun getAllUsers(): MutableList<UsersData>?

    @Query("SELECT * FROM UsersData WHERE UsersData.email=:email AND UsersData.userType=:userType")
    fun getUser(email:String,userType: Boolean): UsersData?

    @Delete
    fun delete(usersData: UsersData)

    @Delete
    fun deleteAll(vararg usersData: UsersData)
}