package org.arboristasurbanos.treeplant.helper

import android.Manifest
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.LocationManager
import android.provider.Settings
import androidx.core.app.ActivityCompat
import androidx.fragment.app.FragmentActivity
import com.google.android.gms.location.*

class locationServiceHelper {
    var context: Context
    var activity: FragmentActivity

    // globally declare LocationCallback
    private lateinit var locationCallback: LocationCallback

    // declare a global variable of FusedLocationProviderClient
    private var fusedLocationClient: FusedLocationProviderClient

    // globally declare LocationRequest
    private lateinit var locationRequest: LocationRequest

    public lateinit var internalLocationCallback: LocationCallback

    var lat: Double = 0.0
    var long: Double = 0.0

    constructor(context: Context, activity: FragmentActivity){
        this.context = context
        this.activity = activity
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(activity)
    }
    fun checkLocation(){
        val manager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            showAlertLocation(context)
        }
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
        getLocationUpdates()
    }
    private fun getLocationUpdates() {
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
        locationRequest = LocationRequest()
        locationRequest.interval = 50000
        locationRequest.fastestInterval = 50000
        locationRequest.smallestDisplacement = 170f //170 m = 0.1 mile
        locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult?) {
                locationResult ?: return
                if (locationResult.locations.isNotEmpty()) {
                    lat = locationResult.lastLocation.latitude
                    long = locationResult.lastLocation.longitude
                    internalLocationCallback.onLocationResult(locationResult)
                }
            }
        }
    }

    // Start location updates
    fun startLocationUpdates() {
        if (this.activity?.let {
                this.context?.let { it1 ->
                    ActivityCompat.checkSelfPermission(
                        it1,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    )
                }
            } != PackageManager.PERMISSION_GRANTED && this.activity?.let {
                this.context?.let { it1 ->
                    ActivityCompat.checkSelfPermission(
                        it1,
                        Manifest.permission.ACCESS_FINE_LOCATION
                    )
                }
            } != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(activity,
                arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION),1)
            return
        }
        fusedLocationClient.requestLocationUpdates(
            locationRequest,
            locationCallback,
            null /* Looper */
        )
    }

    // Stop location updates
    fun stopLocationUpdates() {
        this.fusedLocationClient.removeLocationUpdates(locationCallback)
    }

    private fun showAlertLocation(context: Context) {
        val dialog = AlertDialog.Builder(context)
        dialog.setMessage("Your location settings is set to Off, Please enable location to use this application")
        dialog.setPositiveButton("Settings") { _, _ ->
            val myIntent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
            activity.startActivity(myIntent)
        }
        dialog.setNegativeButton("Cancel") { _, _ ->
            //finish()
        }
        dialog.setCancelable(false)
        dialog.show()
    }

}