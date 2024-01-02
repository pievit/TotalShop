package it.torino.totalshop

import android.app.Application
import android.content.Context
import it.torino.totalshop.roomdb.Repository
import it.torino.totalshop.roomdb.entities.UsersData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class modelRoom(val context: Context,val repository:Repository, viewModelScope: CoroutineScope){

    init{
        viewModelScope.launch(Dispatchers.IO) {
            //prova db
            var ud = UsersData("gv@gmail.com","venggeng",userType = true)
            insertIntoDB(ud)
        }
    }
    private suspend fun insertIntoDB(usersData: UsersData) {
        repository.dbUsersDataDao?.insert(usersData)
    }
}