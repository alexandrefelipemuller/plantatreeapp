package org.arboristasurbanos.treeplant.ui.planting

import android.app.DatePickerDialog
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationResult
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.analytics.ktx.logEvent
import com.google.firebase.ktx.Firebase
import org.arboristasurbanos.treeplant.database.DatabaseHandler
import org.arboristasurbanos.treeplant.databinding.FragmentPlantingBinding
import org.arboristasurbanos.treeplant.helper.locationServiceHelper
import org.arboristasurbanos.treeplant.model.TreeModelClass
import java.text.SimpleDateFormat
import java.util.*

import android.net.Uri
import org.arboristasurbanos.treeplant.R
import android.widget.Toast

import androidx.core.content.FileProvider
import org.arboristasurbanos.treeplant.helper.Sharing
import java.io.File
import java.io.FileOutputStream
import java.lang.Exception


class PlantingFragment : Fragment() {
    private lateinit var slideshowViewModel: PlantingViewModel
    private var _binding: FragmentPlantingBinding? = null
    private lateinit var locationServiceHelper: locationServiceHelper
    private var imageBitmap : Bitmap? = null
    private lateinit var mContext: Context

    private lateinit var firebaseAnalytics: FirebaseAnalytics

    enum class typeMode {
        NEW, EDIT
    }
    private var mode: typeMode = typeMode.NEW
    private var treeId = 0

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mContext = requireContext()
        slideshowViewModel =
            ViewModelProvider(this).get(PlantingViewModel::class.java)

        _binding = FragmentPlantingBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val bundle = arguments
        val Id = bundle?.getInt("Id")
        if (Id != null){
            this.mode = typeMode.EDIT
            this.treeId = Id
            val databaseHandler: DatabaseHandler = DatabaseHandler(requireContext())
            var tree =  databaseHandler.viewTree(Id)
            if (tree?.Name != null)
                _binding!!.editTextPlantName.setText(tree!!.Name)
            if (tree?.Date != null)
                _binding!!.editTextDate.setText(tree!!.Date)
            if (tree?.Photo != null)
                _binding!!.addPhotoIcon.setImageBitmap(tree!!.Photo)

            _binding!!.editTextLat.hint = getString(R.string.location_disable_message)
            _binding!!.editTextLat.isEnabled = false
            _binding!!.editTextLong.hint = getString(R.string.location_disable_message)
            _binding!!.editTextLong.isEnabled = false
        }

        locationServiceHelper = locationServiceHelper(this.requireContext(), requireActivity())
        if (this.mode == typeMode.NEW) {
            locationServiceHelper.internalLocationCallback = object : LocationCallback() {
                override fun onLocationResult(locationResult: LocationResult    ) {
                    if (binding.editTextLat.text.isEmpty()) {
                        binding.editTextLat.setText(locationServiceHelper.lat.toString())
                    }
                    if (binding.editTextLong.text.isEmpty()) {
                        binding.editTextLong.setText(locationServiceHelper.long.toString())
                    }
                }
            }
            locationServiceHelper.checkLocation()
        }


        val addPhotoButton: ImageView = binding.addPhotoIcon
        addPhotoButton.setOnClickListener {
            val REQUEST_IMAGE_CAPTURE = 1
            fun dispatchTakePictureIntent() {
                Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
                    takePictureIntent.resolveActivity(requireContext().packageManager)?.also {
                        startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
                    }
                }
            }
            dispatchTakePictureIntent()
        }
        val button: Button = binding.submitButton
        button.setOnClickListener {
            submit(root)
        }
        val sdf = SimpleDateFormat("dd/MM/yyyy")
        if (binding.editTextDate.text.isEmpty())
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

        firebaseAnalytics = Firebase.analytics
        firebaseAnalytics.logEvent(FirebaseAnalytics.Event.SCREEN_VIEW) {
            param(FirebaseAnalytics.Param.SCREEN_NAME, "PlantingFragment")
            param(FirebaseAnalytics.Param.SCREEN_CLASS, "MainActivity")
        }
        return root
    }

    fun submit(root: View) {
        var newTree: TreeModelClass
        if (this.mode == typeMode.EDIT){
            newTree = TreeModelClass(
                Id  = this.treeId,
                Name = _binding!!.editTextPlantName.text.toString(),
                Date = _binding!!.editTextDate.text.toString(),
                Photo = this.imageBitmap
            )
            val databaseHandler: DatabaseHandler = DatabaseHandler(this.requireContext())
            databaseHandler.updateTree(newTree)
        }
        else{
            if (_binding!!.editTextLat.text.toString().isEmpty() || _binding!!.editTextLong.text.toString().isEmpty() || _binding!!.editTextDate.text.toString().isEmpty()){
                Snackbar.make(root, getString(R.string.required_ino), Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show()
                return
            }

            newTree = TreeModelClass(
                Name = _binding!!.editTextPlantName.text.toString(),
                Date = _binding!!.editTextDate.text.toString(),
                Lat =_binding!!.editTextLat.text.toString().toDouble(),
                Long = _binding!!.editTextLong.text.toString().toDouble(),
                Photo = this.imageBitmap
            )
            val databaseHandler: DatabaseHandler = DatabaseHandler(this.requireContext())
            databaseHandler.addTree(newTree)
        }
        this.activity?.getSupportFragmentManager()?.popBackStack()
       // startActivity(Intent.makeRestartActivityTask(this.activity?.intent?.component))

        class shareListener : View.OnClickListener {
            override fun onClick(v: View) {
                Sharing().shareImageandText(newTree, mContext)
            }
        }

        view?.let { it1 ->
            val mySnackbar = Snackbar.make(it1, "Tree create with success", Snackbar.LENGTH_LONG)
            mySnackbar.setAction("SHARE", shareListener())
            mySnackbar.show()
        }
    }

    // Stop receiving location update when activity not visible/foreground
    override fun onPause() {
        super.onPause()
        if (this.mode == typeMode.NEW && ::locationServiceHelper.isInitialized)
            locationServiceHelper.stopLocationUpdates()
    }

    // Start receiving location update when activity  visible/foreground
    override fun onResume() {
        super.onResume()
        if (this.mode == typeMode.NEW && ::locationServiceHelper.isInitialized)
            locationServiceHelper.startLocationUpdates()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        val REQUEST_IMAGE_CAPTURE = 1
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == AppCompatActivity.RESULT_OK) {
            imageBitmap = data?.extras?.get("data") as Bitmap
            _binding?.addPhotoIcon?.setImageBitmap(imageBitmap)
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

}