package it.torino.totalshop

import android.app.Activity
import android.content.Intent
import android.content.res.Resources
import android.os.Build
import android.os.Bundle
import android.service.autofill.UserData
import android.util.Log
import android.view.View
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import it.torino.totalshop.roomdb.Repository
import it.torino.totalshop.roomdb.entities.UsersData
import kotlinx.coroutines.CoroutineScope


class MainActivity : AppCompatActivity() {
    var vm: viewModel? = null
    @RequiresApi(Build.VERSION_CODES.P)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.nav_activity)

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        val host: NavHostFragment = supportFragmentManager
            .findFragmentById(R.id.my_nav_host_fragment) as NavHostFragment? ?: return


        findViewById<View>(R.id.backButton).setOnClickListener{
            vm?.user?.value = null
            vm?.store?.value = null
            host.findNavController().popBackStack()
        }

        host.findNavController().addOnDestinationChangedListener { _, destination, _ ->
            // Per non far visualizzare la toolbar nella MainActivity
            when (destination.id) {
                R.id.activity_main -> toolbar.visibility = View.GONE;
                else -> toolbar.visibility = View.VISIBLE
            }
            Log.d("MyActivity", "Destination changed to ${destination.id}")
        }

        this.vm = ViewModelProvider(this)[viewModel::class.java]

        this.vm!!.usersList.observe(this){
            list -> Log.d("Test","Users: " + list.toString())
        }

        this.vm!!.storesList.observe(this){
            list -> Log.d("Test","Stores: " + list.toString())
        }


        this.vm?.getUsers()
        this.vm?.getStores()
    }


}