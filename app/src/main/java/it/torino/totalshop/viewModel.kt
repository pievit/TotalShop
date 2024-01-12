package it.torino.totalshop

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import it.torino.totalshop.roomdb.Repository
import it.torino.totalshop.roomdb.entities.OrdersData
import it.torino.totalshop.roomdb.entities.ProductsData
import it.torino.totalshop.roomdb.entities.StoreData
import it.torino.totalshop.roomdb.entities.UsersData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

class viewModel(application: Application): AndroidViewModel(application) {
    private val repository: Repository = Repository(application)
    var modelRoom: modelRoom = modelRoom(application,repository,viewModelScope)
    var usersList: MutableLiveData<MutableList<UsersData>?> = MutableLiveData<MutableList<UsersData>?>()
    var storesList: MutableLiveData<MutableList<StoreData>?> = MutableLiveData<MutableList<StoreData>?>()
    var prodsList: MutableLiveData<MutableList<ProductsData>?> = MutableLiveData<MutableList<ProductsData>?>()
    var ordList: MutableLiveData<MutableList<OrdersData>?> = MutableLiveData<MutableList<OrdersData>?>()
    var user: MutableLiveData<UsersData>? = MutableLiveData<UsersData>()
    var store: MutableLiveData<StoreData>? = MutableLiveData<StoreData>()
    private var prvuser: UsersData? = null
    var newProd: MutableLiveData<Boolean>? = MutableLiveData<Boolean>()
    var inserito: MutableLiveData<Boolean>? = MutableLiveData<Boolean>()
    fun getUsers(){
        viewModelScope.launch(Dispatchers.IO) {
            val res = getAllUsers()
            withContext(Dispatchers.Main){
                usersList.value = res
            }
        }
    }
    private suspend fun getAllUsers(): MutableList<UsersData>? {
        return repository.dbUsersDataDao?.getAllUsers()
    }

    fun getUser(email: String,userType: Boolean,prv: Boolean=false){
        viewModelScope.launch(Dispatchers.IO){
            val res = getUserSus(email,userType)
            Log.d("debug","resuser: "+res.toString())
            withContext(Dispatchers.Main){
                if(!prv){
                    user!!.value = res
                }
                prvuser = res
            }
        }
    }


    private suspend fun getUserSus(email: String,userType: Boolean): UsersData?{
        return repository.dbUsersDataDao?.getUser(email,userType)
    }

    fun insertUser(usersData: UsersData){
        var flag = false
        viewModelScope.launch(Dispatchers.IO) {
            getUser(usersData.email,usersData.userType,true)
            if(prvuser==null){
                insertUserSus(usersData)
                flag=true
            }
            withContext(Dispatchers.Main){
                inserito!!.value = flag
                user!!.value = usersData
            }
        }
    }
    private suspend fun insertUserSus(usersData: UsersData){
        repository.dbUsersDataDao?.insert(usersData)
    }


    fun getStores(){
        viewModelScope.launch(Dispatchers.IO) {
            val res = getAllStores()
            withContext(Dispatchers.Main){
                storesList.value = res
            }
        }
    }

    private suspend fun getAllStores() :MutableList<StoreData>?{
        return repository.dbStoreDataDAO?.getAllStores()
    }


    fun getStore(email:String){
        viewModelScope.launch(Dispatchers.IO){
            val res = getStoreSus(email)
            withContext(Dispatchers.Main){
                store!!.value = res
            }
        }
    }

    private suspend fun getStoreSus(email: String): StoreData?{
        return repository.dbStoreDataDAO?.getStore(email)
    }

    fun insertStore(storeData: StoreData){
        viewModelScope.launch(Dispatchers.IO){
           insertStoreSus(storeData)
            getStore(storeData.owner)
        }

    }

    private suspend fun insertStoreSus(storeData: StoreData){
       repository.dbStoreDataDAO?.insert(storeData)
    }

    fun getAllProds(){
        viewModelScope.launch(Dispatchers.IO){
            var res = getAllProdsSus()
            withContext(Dispatchers.Main){
                prodsList.value = res
            }
        }
    }

    private suspend fun getAllProdsSus(): MutableList<ProductsData>?{
        return repository.dbProdsDataDAO?.getAllProducts()
    }


    fun getAllProdsFromStore(id: Int){
        viewModelScope.launch(Dispatchers.IO) {
            var res = getAllProdsFromStoreSus(id)
            withContext(Dispatchers.Main){
                prodsList.value = res
            }
        }
    }

    private suspend fun getAllProdsFromStoreSus(id: Int): MutableList<ProductsData>?{
      return repository.dbProdsDataDAO?.getStoreProducts(id)
    }

    fun insertProd(prodData: ProductsData){
        viewModelScope.launch(Dispatchers.IO){
            insertProduct(prodData)
            getAllProdsFromStore(prodData.storeId)
        }
    }

    private suspend fun insertProduct(prod: ProductsData){
        repository.dbProdsDataDAO?.insert(prod)
    }

    fun deleteProd(prod: ProductsData){
        viewModelScope.launch(Dispatchers.IO){
            deleteProdSus(prod)
            getAllProdsFromStore(prod.storeId)
        }
    }

    private suspend fun deleteProdSus(prod: ProductsData){
        repository.dbProdsDataDAO?.delete(prod)
    }

    fun getAllOrdersFromStoreID(id: Int){
        viewModelScope.launch(Dispatchers.IO){
            var res = getAllOrdStoreSus(id)
            withContext(Dispatchers.Main){
                ordList.value = res
            }
        }
    }

    private suspend fun getAllOrdStoreSus(id:Int): MutableList<OrdersData>? {
        return repository.dbOrdersDataDAO?.getOrdersFromStoreID(id)
    }

    fun getAllOrdersFromEmail(email: String){
        viewModelScope.launch(Dispatchers.IO){
            var res = getAllOrdEmailSus(email)
            withContext(Dispatchers.Main){
                ordList.value = res
            }
        }
    }

    private suspend fun getAllOrdEmailSus(email: String): MutableList<OrdersData>? {
        return repository.dbOrdersDataDAO?.getOrdersFromMail(email)
    }

    fun insertOrder(ord: OrdersData){
        viewModelScope.launch(Dispatchers.IO){
            insertOrderSus(ord)
            getAllOrdersFromEmail(ord.usermail)
            withContext(Dispatchers.Main){
                newProd!!.value = true
            }
        }
    }

    private suspend fun insertOrderSus(ord: OrdersData){
        repository.dbOrdersDataDAO?.insert(ord)
    }
}