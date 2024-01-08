package it.torino.totalshop

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.app.Application
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import androidx.core.app.ActivityCompat
import androidx.core.util.Consumer
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import it.torino.totalshop.roomdb.entities.LocationData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class LocationViewModel(application: Application) : AndroidViewModel(application) {
    private val timeBeforeRefreshMicroSec : Long = 60000
    private var _latitude : Double = 0.0
    private var _longitude : Double = 0.0
    var locationData : MutableLiveData<LocationData> = MutableLiveData<LocationData>()
    val MY_PERMISSIONS_REQUEST_LOCATION : Int = 50

    private val locationManager =
        application.getSystemService(android.content.Context.LOCATION_SERVICE) as LocationManager

    private val locationListener = LocationListener {
        location ->
            _latitude = location.latitude
            _longitude = location.longitude
    }

    private fun requestLocationPermission() {

        ActivityCompat.requestPermissions(
            Activity(),
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
            ),
            MY_PERMISSIONS_REQUEST_LOCATION
        )
    }

    init{
        startLocationService()
    }

    fun startLocationService(){
        if (ActivityCompat.checkSelfPermission(
                getApplication(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(
                getApplication(),
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    Activity(),
                    Manifest.permission.ACCESS_FINE_LOCATION
                )
            ) {
                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                AlertDialog.Builder(Activity())
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
        } else {
            locationManager.requestLocationUpdates(
                LocationManager.GPS_PROVIDER,
                timeBeforeRefreshMicroSec, // minimum time interval between updates in milliseconds
                50f,   // minimum distance between updates in meters
                locationListener
            )

            getCoord()
        }
    }

    fun getCoord(){
        viewModelScope.launch(Dispatchers.IO) {
            val res = getLastLocation()
            withContext(Dispatchers.Main){
                locationData.value = res
            }
        }
    }

    private suspend fun getLastLocation() : LocationData {
        var res : LocationData = LocationData(_latitude,_longitude)
        return res
    }




    fun stopLocationUpdates() {
        // Stop receiving location updates
        locationManager.removeUpdates(locationListener)
    }
}
