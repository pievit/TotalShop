package it.torino.totalshop.roomdb

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import it.torino.totalshop.roomdb.entities.UsersData
import it.torino.totalshop.roomdb.entities.UsersDataDAO
import it.torino.totalshop.roomdb.MyRoomDb
import it.torino.totalshop.roomdb.entities.StoreData
import it.torino.totalshop.roomdb.entities.StoreDataDAO

class Repository(application: Context?) : ViewModel(){
    val dbUsersDataDao: UsersDataDAO?
    private val usersList: MutableLiveData<List<UsersData>>?
    val dbStoreDataDAO: StoreDataDAO?
    private val storeList: MutableLiveData<List<StoreData>>?
    init{
        val db = MyRoomDb.getDatabase(application!!)
        dbUsersDataDao = db!!.myUsersDataDao()
        usersList = MutableLiveData()
        dbStoreDataDAO= db!!.myStoreDataDao()
        storeList = MutableLiveData()
    }
}