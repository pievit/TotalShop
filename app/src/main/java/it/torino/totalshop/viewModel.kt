package it.torino.totalshop

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import it.torino.totalshop.roomdb.Repository
import androidx.lifecycle.viewModelScope
import it.torino.totalshop.roomdb.entities.UsersData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class viewModel(application: Application): AndroidViewModel(application) {
    private val repository: Repository = Repository(application)
    private var modelRoom: modelRoom = modelRoom(application,repository,viewModelScope)
    var usersList: MutableLiveData<MutableList<UsersData>?> = MutableLiveData<MutableList<UsersData>?>()


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
}