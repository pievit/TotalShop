package it.torino.totalshop.venditore

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import it.torino.totalshop.LocationViewModel
import it.torino.totalshop.R

class VenditoreActivity : AppCompatActivity() {

    var locationVM : LocationViewModel? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.venditore_activity)
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        var backbtn = findViewById<ImageButton>(R.id.backButtonVendor)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        val host: NavHostFragment = supportFragmentManager
            .findFragmentById(R.id.venditore_nav_host_fragment) as NavHostFragment? ?: return

        val navController = host.navController
        setupBottomNavMenu(navController)

        navController.addOnDestinationChangedListener { _, destination, _ ->
            when(destination.id){
                R.id.venditore_home -> {
                    backbtn.visibility = View.GONE
                    findViewById<TextView>(R.id.toolbarTitle).setText("Home Negozio")
                }
                R.id.venditore_ordini -> {
                    backbtn.visibility = View.GONE
                    findViewById<TextView>(R.id.toolbarTitle).setText("Ordini Store")
                }
                R.id.venditore_settings -> {
                    backbtn.visibility = View.GONE
                    findViewById<TextView>(R.id.toolbarTitle).setText("Settings")
                }
            }
            Log.d("ActivityVenditore", "Destination changed to ${destination.id}")
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

        }
    }



    private fun setupBottomNavMenu(navController: NavController) {
        val bottomNav = findViewById<BottomNavigationView>(R.id.bottom_nav_view_venditore)
        bottomNav?.setupWithNavController(navController)

    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray){
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            locationVM!!.MY_PERMISSIONS_REQUEST_LOCATION -> {
                if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED
                ) {
                    locationVM!!.startLocationService()
                    locationVM!!.stopLocationUpdates()
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