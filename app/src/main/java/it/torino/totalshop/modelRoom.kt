package it.torino.totalshop

import android.content.Context
import android.util.Log
import it.torino.totalshop.roomdb.Repository
import it.torino.totalshop.roomdb.entities.StoreData
import it.torino.totalshop.roomdb.entities.UsersData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class modelRoom(val context: Context,val repository:Repository, viewModelScope: CoroutineScope){

    init{

        viewModelScope.launch(Dispatchers.IO) {
            //prova db
            var ud = UsersData("gv@gmail.com","gvgv","gianni","3334445566",userType = false)
            insertIntoDB(ud)
//            ud = UsersData("paolo@gmail.com","paolo","Paolo","1256987453",userType = true)
//            var pstore = StoreData("Ferramenta Paolo", "via roma, 15", "Ferramenta", "paolo@gmail.com")
//            insertIntoDB(ud)

            Log.d("debug","qui")
        }
    }
    private suspend fun insertIntoDB(usersData: UsersData) {
        repository.dbUsersDataDao?.insert(usersData)
    }

    private suspend fun deleteAll(){
        repository.dbUsersDataDao?.deleteAll()
        repository.dbStoreDataDAO?.deleteAll()
    }
}