package it.torino.totalshop

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import it.torino.totalshop.roomdb.Repository
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
    var user: MutableLiveData<UsersData>? = MutableLiveData<UsersData>()
    var store: MutableLiveData<StoreData>? = MutableLiveData<StoreData>()
    private var prvuser: UsersData? = null
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

    fun getNearStores(lat: Double,long: Double,dist: Int){
        viewModelScope.launch(Dispatchers.IO){
            var res = getAllStores()
            withContext(Dispatchers.Main){
                if(res!=null){
                    var nearStoreList: MutableList<StoreData>? = null
                    for(r: StoreData in res){
                        if(r.lat != null && r.long != null){
                            val R = 6371e3; // metres
                            val var1 = lat * Math.PI/180; // φ, λ in radians
                            val var2 = (r.lat!! * Math.PI)/180;
                            val delt1 = (r.lat!!-lat) * Math.PI/180;
                            val delt2 = (r.long!!-long) * Math.PI/180;

                            val a = sin(delt1/2) * sin(delt1/2) +
                                    cos(var1) * cos(var2) *
                                    sin(delt2/2) * sin(delt2/2);
                            val c = 2 * atan2(sqrt(a), sqrt(1-a));

                            val d = R * c; //distanza in metri

                            if(d<= dist){
                                nearStoreList?.add(r)
                            }
                        }

                    }

                    storesList.value = nearStoreList
                }
            }
        }
    }
}