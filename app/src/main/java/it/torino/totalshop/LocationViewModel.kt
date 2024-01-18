package it.torino.totalshop

import android.annotation.SuppressLint
import android.app.Application
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import it.torino.totalshop.roomdb.entities.LocationData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class LocationViewModel(application: Application) : AndroidViewModel(application) {
    private val timeBeforeRefreshMicroSec : Long = 30000
    private var _latitude : Double = 0.0
    private var _longitude : Double = 0.0
    var locationData : MutableLiveData<LocationData> = MutableLiveData<LocationData>()
    val MY_PERMISSIONS_REQUEST_LOCATION : Int = 50

    private val locationManager =
        application.getSystemService(android.content.Context.LOCATION_SERVICE) as LocationManager

    private val locationListener : LocationListener = object : LocationListener {
        override fun onLocationChanged(location: Location) {
            Log.d("SensorLocationLog", "location Listener change "+ location.latitude +" "+location.longitude)
            if(_latitude != location.latitude || _longitude!=location.longitude){
                _latitude = location.latitude
                _longitude = location.longitude
                getCoord()
            }
        }

        override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {

        }

        override fun onProviderDisabled(provider: String) {
            Toast.makeText(application.applicationContext,"Attiva il gps per prendere la posizione!",Toast.LENGTH_SHORT).show()
        }

        override fun onProviderEnabled(provider: String) {
            Toast.makeText(application.applicationContext,"Gps attivato! Attendi qualche secondo...",Toast.LENGTH_SHORT).show()
        }

    }

@SuppressLint("MissingPermission")
    fun startLocationService(){
        locationManager.requestLocationUpdates(
        LocationManager.GPS_PROVIDER,
        timeBeforeRefreshMicroSec, // minimum time interval between updates in milliseconds
        50f,   // minimum distance between updates in meters
        locationListener
        )

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
