package it.torino.totalshop

import android.Manifest
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


    private val locationManager =
        application.getSystemService(android.content.Context.LOCATION_SERVICE) as LocationManager

    private val locationListener = LocationListener {
        location ->
            _latitude = location.latitude
            _longitude = location.longitude
    }

    init{
        if (ActivityCompat.checkSelfPermission(
                getApplication(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                getApplication(),
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.


        }

        locationManager.requestLocationUpdates(
            LocationManager.GPS_PROVIDER,
            timeBeforeRefreshMicroSec, // minimum time interval between updates in milliseconds
            50f,   // minimum distance between updates in meters
            locationListener
        )

        getCoord()
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
