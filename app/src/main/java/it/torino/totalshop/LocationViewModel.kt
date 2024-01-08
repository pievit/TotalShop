package it.torino.totalshop

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.app.Application
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.util.Consumer
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import it.torino.totalshop.roomdb.entities.LocationData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.NonDisposableHandle.parent
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
            Log.d("Test", "location Listener change "+ location.latitude +" "+location.longitude)
            if(_latitude != location.latitude || _longitude!=location.longitude){
                _latitude = location.latitude
                _longitude = location.longitude
                getCoord()
            }
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
