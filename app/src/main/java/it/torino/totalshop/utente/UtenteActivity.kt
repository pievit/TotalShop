package it.torino.totalshop.utente


import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import it.torino.totalshop.R

class UtenteActivity : AppCompatActivity() {
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



    }

    private fun setupBottomNavMenu(navController: NavController) {
        val bottomNav = findViewById<BottomNavigationView>(R.id.bottom_nav_view_utente)
        bottomNav?.setupWithNavController(navController)

    }

}