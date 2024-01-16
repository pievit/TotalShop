package it.torino.totalshop.utente


import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.View.GONE
import android.view.View.INVISIBLE
import android.view.View.VISIBLE
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatImageButton
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import it.torino.totalshop.LocationViewModel
import it.torino.totalshop.R
import it.torino.totalshop.viewModel

class UtenteActivity : AppCompatActivity() {
    var locationVM : LocationViewModel? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.utente_activity)

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        val host: NavHostFragment = supportFragmentManager
            .findFragmentById(R.id.utente_nav_host_fragment) as NavHostFragment? ?: return

        val navController = host.navController
        setupBottomNavMenu(navController)

        val backbutUser = findViewById<AppCompatImageButton>(R.id.backButtonUser)
        backbutUser.setOnClickListener{
            navController.popBackStack()
        }
        navController.addOnDestinationChangedListener { _, destination, _ ->
            Log.d("ActivityUtente", "Destination changed to ${destination.id}")
            when (destination.id) {
                R.id.utente_prod_sel -> backbutUser.visibility = VISIBLE;
                else -> backbutUser.visibility = GONE;
            }
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
            Log.d("Test","start ls" )
            locationVM?.startLocationService()
        }

//        val notif = intent.getIntExtra("notifOrder",-1)
//        if(notif>=0){
//            navController.navigate(R.id.utente_ordini)
//         }
    }



    private fun setupBottomNavMenu(navController: NavController) {
        val bottomNav = findViewById<BottomNavigationView>(R.id.bottom_nav_view_utente)
        bottomNav?.setupWithNavController(navController)
        bottomNav?.setOnItemReselectedListener {
            navController.navigate(it.itemId)
            Log.d("Test",it.itemId.toString())
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
}