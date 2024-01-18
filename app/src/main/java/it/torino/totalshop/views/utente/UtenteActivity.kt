package it.torino.totalshop.views.utente


import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import it.torino.totalshop.LocationViewModel
import it.torino.totalshop.NotificationService
import it.torino.totalshop.R

class UtenteActivity : AppCompatActivity() {
    var locationVM : LocationViewModel? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.utente_activity)
        var backbtn = findViewById<ImageButton>(R.id.backButton)
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        val host: NavHostFragment = supportFragmentManager
            .findFragmentById(R.id.utente_nav_host_fragment) as NavHostFragment? ?: return

        val navController = host.navController
        setupBottomNavMenu(navController)

        backbtn.setOnClickListener{
            navController.popBackStack()
        }

        navController.addOnDestinationChangedListener { _, destination, _ ->
            when(destination.id){
                R.id.utente_prod_sel -> {
                    backbtn.visibility = VISIBLE
                    findViewById<TextView>(R.id.toolbarTitle).setText("Prodotti Store")
                }
                R.id.utente_home -> {
                    backbtn.visibility = View.GONE
                    findViewById<TextView>(R.id.toolbarTitle).setText("Home")
                }
                R.id.utente_ordini -> {
                    backbtn.visibility = View.GONE
                    findViewById<TextView>(R.id.toolbarTitle).setText("Ordini Utente")
                }
                R.id.utente_settings -> {
                    backbtn.visibility = View.GONE
                    findViewById<TextView>(R.id.toolbarTitle).setText("Settings")
                }
            }
            Log.d("ActivityUtente", "Destination changed to ${destination.id}")
        }


        locationVM = ViewModelProvider(this)[LocationViewModel::class.java]




        if (ActivityCompat.checkSelfPermission(
                application,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(
                application,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    this,
                    Manifest.permission.ACCESS_FINE_LOCATION
                )
            ) {
                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                AlertDialog.Builder(this)
                    .setTitle("Necessaria autorizzazione posizione esatta")
                    .setMessage("Per funzionare correttamente, l'applicazione necessita l'autorizzazione per accedere alla posizione precisa")
                    .setPositiveButton(
                        "OK"
                    ) { _, _ ->
                        //Prompt the user once explanation has been shown
                        requestLocationPermission()
                    }
                    .create()
                    .show()
            } else {
                // No explanation needed, we can request the permission.
                requestLocationPermission()
            }

        }else{
            Log.d("UtenteActivity","start location Service after permissions" )
            locationVM?.startLocationService()
        }

//        val notif = intent.getIntExtra("notifOrder",-1)
//        if(notif>=0){
//            navController.navigate(R.id.utente_ordini)
//         }


        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            requestNotificationPermission()
            return
        }else{
            Log.d("UtenteActivity","Start Notification service with permissions")
            var sp = getSharedPreferences("NOTIFY", Context.MODE_PRIVATE)
            if(sp.getBoolean("NOTIFICATIONS",false)){
                val intentNotif = Intent(this, NotificationService::class.java)
                startService(intentNotif)
            }
        }
    }



    private fun setupBottomNavMenu(navController: NavController) {
        val bottomNav = findViewById<BottomNavigationView>(R.id.bottom_nav_view_utente)
        bottomNav?.setupWithNavController(navController)
        bottomNav?.setOnItemReselectedListener {
            navController.navigate(it.itemId)
        }

    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray){
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            locationVM!!.MY_PERMISSIONS_REQUEST_LOCATION -> {
                if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED
                ) {
                    locationVM!!.startLocationService()
                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return
            }
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

    override fun onDestroy() {
        super.onDestroy()
        // Stop receiving location updates when the activity is destroyed
        locationVM?.stopLocationUpdates()
    }

    fun requestLocationPermission() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
            ),
            locationVM!!.MY_PERMISSIONS_REQUEST_LOCATION
        )
    }

    fun requestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(
                    Manifest.permission.POST_NOTIFICATIONS,
                ),
                NotificationService.NOTIFICATION_PERMISSION_ID
            )
        }
    }
}