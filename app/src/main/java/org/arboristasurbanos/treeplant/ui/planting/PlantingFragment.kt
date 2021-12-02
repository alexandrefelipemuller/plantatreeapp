package org.arboristasurbanos.treeplant.ui.planting

import android.Manifest
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.LocationManager
import com.google.android.gms.location.LocationRequest
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.material.snackbar.Snackbar
import org.arboristasurbanos.treeplant.database.DatabaseHandler
import org.arboristasurbanos.treeplant.databinding.FragmentPlantingBinding
import org.arboristasurbanos.treeplant.model.TreeModelClass
import java.text.SimpleDateFormat
import java.util.*

class PlantingFragment : Fragment() {

    private var lat: Double = 0.0
    private var long: Double = 0.0
    private lateinit var slideshowViewModel: PlantingViewModel
    private var _binding: FragmentPlantingBinding? = null

    // declare a global variable of FusedLocationProviderClient
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    // globally declare LocationRequest
    private lateinit var locationRequest: LocationRequest

    // globally declare LocationCallback
    private lateinit var locationCallback: LocationCallback


    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        slideshowViewModel =
            ViewModelProvider(this).get(PlantingViewModel::class.java)

        _binding = FragmentPlantingBinding.inflate(inflater, container, false)
        val root: View = binding.root

        // in onCreate() initialize FusedLocationProviderClient
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this.requireContext())
        checkLocation()

        val button: Button = binding.submitButton
        button.setOnClickListener {
            val newTree = TreeModelClass(
                Name = _binding!!.editTextPlantName.text.toString(),
                Date = _binding!!.editTextDate.text.toString(),
                Lat =_binding!!.editTextLat.text.toString().toDouble(),
                Long = _binding!!.editTextLong.text.toString().toDouble()
            )
            val databaseHandler: DatabaseHandler = DatabaseHandler(this.requireContext())
            databaseHandler.addTree(newTree)
            this.activity?.getSupportFragmentManager()?.popBackStack()
            startActivity(Intent.makeRestartActivityTask(this.activity?.intent?.component))
            view?.let { it1 ->
                Snackbar.make(it1, "Tree create with success", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show()
            }
        }
        val sdf = SimpleDateFormat("dd/MM/yyyy")
        binding.editTextDate.setText(sdf.format(Date()))
        return root
    }

    private fun checkLocation(){
        val manager = requireContext().getSystemService(Context.LOCATION_SERVICE) as LocationManager
        if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            showAlertLocation(this.requireContext())
        }
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this.requireContext())
        getLocationUpdates()
    }

    private fun getLocationUpdates() {
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this.requireContext())
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
                    binding.editTextLat.setText(lat.toString())
                    binding.editTextLong.setText(long.toString())
                }
            }
        }
    }

    // Start location updates
    private fun startLocationUpdates() {
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
            ActivityCompat.requestPermissions(this.requireActivity(),
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
    private fun stopLocationUpdates() {
        fusedLocationClient.removeLocationUpdates(locationCallback)
    }

    // Stop receiving location update when activity not visible/foreground
    override fun onPause() {
        super.onPause()
        stopLocationUpdates()
    }

    // Start receiving location update when activity  visible/foreground
    override fun onResume() {
        super.onResume()
        startLocationUpdates()
    }

    private fun showAlertLocation(context: Context) {
        val dialog = AlertDialog.Builder(context)
        dialog.setMessage("Your location settings is set to Off, Please enable location to use this application")
        dialog.setPositiveButton("Settings") { _, _ ->
            val myIntent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
            startActivity(myIntent)
        }
        dialog.setNegativeButton("Cancel") { _, _ ->
            //finish()
        }
        dialog.setCancelable(false)
        dialog.show()
    }

}