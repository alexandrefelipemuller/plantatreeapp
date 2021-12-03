package org.arboristasurbanos.treeplant.ui.planting

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationResult
import com.google.android.material.snackbar.Snackbar
import org.arboristasurbanos.treeplant.database.DatabaseHandler
import org.arboristasurbanos.treeplant.databinding.FragmentPlantingBinding
import org.arboristasurbanos.treeplant.helper.locationServiceHelper
import org.arboristasurbanos.treeplant.model.TreeModelClass
import java.text.SimpleDateFormat
import java.util.*


class PlantingFragment : Fragment() {
    private lateinit var slideshowViewModel: PlantingViewModel
    private var _binding: FragmentPlantingBinding? = null
    private lateinit var locationServiceHelper: locationServiceHelper

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
        locationServiceHelper = locationServiceHelper(this.requireContext(), requireActivity())
        locationServiceHelper.checkLocation()
        locationServiceHelper.internalLocationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult?) {
                if (binding.editTextLat.text.isEmpty()) {
                    binding.editTextLat.setText(locationServiceHelper.lat.toString())
                }
                if (binding.editTextLong.text.isEmpty()) {
                    binding.editTextLong.setText(locationServiceHelper.long.toString())
                }
            }
        }

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
        binding.editTextDate.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus){
                val cldr = Calendar.getInstance()
                val day = cldr[Calendar.DAY_OF_MONTH]
                val month = cldr[Calendar.MONTH]
                val year = cldr[Calendar.YEAR]
                // date picker dialog
                // date picker dialog
                var picker = DatePickerDialog(
                    requireActivity(),
                    { view, year, monthOfYear, dayOfMonth -> binding.editTextDate.setText(dayOfMonth.toString() + "/" + (monthOfYear + 1) + "/" + year) },
                    year,
                    month,
                    day
                )
                picker.show()
            }
        }

        return root
    }

    // Stop receiving location update when activity not visible/foreground
    override fun onPause() {
        super.onPause()
        locationServiceHelper.stopLocationUpdates()
    }

    // Start receiving location update when activity  visible/foreground
    override fun onResume() {
        super.onResume()
        locationServiceHelper.startLocationUpdates()
    }

}