package org.arboristasurbanos.treeplant.ui.myforest

import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.ui.AppBarConfiguration
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import org.arboristasurbanos.treeplant.R
import org.arboristasurbanos.treeplant.database.DatabaseHandler
import org.arboristasurbanos.treeplant.databinding.ActivityMapsBinding
import org.arboristasurbanos.treeplant.model.TreeModelClass

class MyForestActivity : Fragment(), OnMapReadyCallback, GoogleMap.OnMarkerClickListener {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding
    private var clickCount = 0
    private var lastClickedId = -1

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreate(savedInstanceState)
        binding = ActivityMapsBinding.inflate(layoutInflater)
        val root: View = binding.root

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = childFragmentManager
            .findFragmentById(R.id.maps) as SupportMapFragment
        mapFragment.getMapAsync(this)
        return root
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        val databaseHandler: DatabaseHandler = DatabaseHandler(requireContext())
        val trees: List<TreeModelClass> = databaseHandler.viewTree()
        mMap = googleMap

        for (tree in trees) {
            // Add a marker to tree and move the camera
            val treeLocation = LatLng(tree.getLat(), tree.getLong())
            val treeMarker = mMap.addMarker(
                MarkerOptions()
                    .position(treeLocation)
                    .title(tree.Name + " : " + tree.Date)
                    .snippet(getString(R.string.click_marker_delete))
                  // .draggable(true)
            )
            treeMarker?.tag = tree.Id
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(treeLocation, 15.0f))
        }
        mMap.setOnMarkerClickListener(this)
    }
    /** Called when the user clicks a marker.  */
    override fun onMarkerClick(marker: Marker): Boolean {

        // Retrieve the data from the marker.
        val markerId = marker.tag as? Int
        clickCount++
        markerId?.let {
            if (lastClickedId == markerId) {
                val builder: AlertDialog.Builder = AlertDialog.Builder(this.requireContext())
                builder.setMessage("Confirm delete this tree?")
                    .setNegativeButton(getString(R.string.no),  DialogInterface.OnClickListener { dialog, id ->
                        })
                    .setPositiveButton(getString(R.string.yes),
                        DialogInterface.OnClickListener { dialog, id ->
                            val databaseHandler: DatabaseHandler = DatabaseHandler(this.requireContext())
                            val dbReturn = databaseHandler.deleteTree(markerId)
                            if (dbReturn) {
                                startActivity(Intent.makeRestartActivityTask(this.requireActivity().intent?.component))
                                Toast.makeText(
                                    this.requireContext(),
                                    "Deleted ${marker.title}",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        })
                builder.show()
        }
        else
        {
            clickCount = 0
        }
        this.lastClickedId=markerId
        }
        return false
    }

}