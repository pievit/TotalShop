package it.torino.totalshop.roomdb

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import it.torino.totalshop.roomdb.entities.OrdersData
import it.torino.totalshop.roomdb.entities.OrdersDataDAO
import it.torino.totalshop.roomdb.entities.ProductsData
import it.torino.totalshop.roomdb.entities.ProductsDataDAO
import it.torino.totalshop.roomdb.entities.StoreData
import it.torino.totalshop.roomdb.entities.StoreDataDAO
import it.torino.totalshop.roomdb.entities.UsersData
import it.torino.totalshop.roomdb.entities.UsersDataDAO

class Repository(application: Context?) : ViewModel(){
    val dbUsersDataDao: UsersDataDAO?
    private val usersList: MutableLiveData<List<UsersData>>?
    val dbStoreDataDAO: StoreDataDAO?
    private val storeList: MutableLiveData<List<StoreData>>?
    val dbProdsDataDAO: ProductsDataDAO?
    private val prodsList: MutableLiveData<List<ProductsData>>?
    val dbOrdersDataDAO: OrdersDataDAO?
    private val ordersList: MutableLiveData<List<OrdersData>>?
    init{
        val db = MyRoomDb.getDatabase(application!!)
        dbUsersDataDao = db!!.myUsersDataDao()
        usersList = MutableLiveData()
        dbStoreDataDAO= db.myStoreDataDao()
        storeList = MutableLiveData()
        dbProdsDataDAO = db.myProdsDataDao()
        prodsList = MutableLiveData()
        dbOrdersDataDAO = db.myOrdersDataDao()
        ordersList = MutableLiveData()
    }
}