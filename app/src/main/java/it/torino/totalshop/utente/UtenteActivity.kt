package it.torino.totalshop.utente


import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
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

        val host: NavHostFragment = supportFragmentManager
            .findFragmentById(R.id.utente_nav_host_fragment) as NavHostFragment? ?: return

        val navController = host.navController
        setupBottomNavMenu(navController)

        findViewById<View>(R.id.backButton).setOnClickListener{
            navController.popBackStack()
        }

        navController.addOnDestinationChangedListener { _, destination, _ ->
            Log.d("ActivityUtente", "Destination changed to ${destination.id}")
        }

        this.locationVM = ViewModelProvider(this)[LocationViewModel::class.java]

        this.locationVM!!.locationData.observe(this){
                res -> Log.d("Test","Users: " + res.toString())
        }

        locationVM?.getCoord()

    }

    private fun setupBottomNavMenu(navController: NavController) {
        val bottomNav = findViewById<BottomNavigationView>(R.id.bottom_nav_view_utente)
        bottomNav?.setupWithNavController(navController)

    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray){
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

}