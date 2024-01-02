package it.torino.totalshop.roomdb

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import it.torino.totalshop.roomdb.entities.UsersData
import it.torino.totalshop.roomdb.entities.UsersDataDAO
import it.torino.totalshop.roomdb.MyRoomDb

class Repository(application: Context?) : ViewModel(){
    val dbUsersDataDao: UsersDataDAO?
    private val usersList: MutableLiveData<List<UsersData>>?
    init{
        val db = MyRoomDb.getDatabase(application!!)
        dbUsersDataDao = db!!.myUsersDataDao()
        usersList = MutableLiveData()
    }
}