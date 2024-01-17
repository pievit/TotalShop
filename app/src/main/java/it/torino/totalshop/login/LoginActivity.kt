package it.torino.totalshop.login

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import it.torino.totalshop.NotificationService
import it.torino.totalshop.R
import it.torino.totalshop.RoomViewModel


class LoginActivity : AppCompatActivity() {
    var vm: RoomViewModel? = null
    @RequiresApi(Build.VERSION_CODES.P)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.login_activity)

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)



        val host: NavHostFragment = supportFragmentManager
            .findFragmentById(R.id.login_nav_host_fragment) as NavHostFragment? ?: return


        findViewById<View>(R.id.backButton).setOnClickListener{
            vm?.user?.value = null
            vm?.store?.value = null
            host.findNavController().popBackStack()
        }

        host.findNavController().addOnDestinationChangedListener { _, destination, _ ->
            // Per non far visualizzare la toolbar nella LoginActivity
            when (destination.id) {
                R.id.home_activity -> toolbar.visibility = View.GONE;
                else -> toolbar.visibility = View.VISIBLE
            }
            Log.d("MyActivity", "Destination changed to ${destination.id}")
        }



        this.vm = ViewModelProvider(this)[RoomViewModel::class.java]

    }


    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray){
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            NotificationService.NOTIFICATION_PERMISSION_ID -> {
                if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED
                ) {
                    getSharedPreferences("NOTIFY",Context.MODE_PRIVATE).edit().putBoolean("NOTIFICATIONS",true).apply()
                    val intentNotif = Intent(this,NotificationService::class.java)
                    startService(intentNotif)
                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return
            }
        }
    }




}