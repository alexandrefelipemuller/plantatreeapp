package org.arboristasurbanos.treeplant.ui.myforest

import android.content.Context
import android.graphics.Bitmap
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.Marker
import org.arboristasurbanos.treeplant.R
import org.arboristasurbanos.treeplant.database.DatabaseHandler
import org.arboristasurbanos.treeplant.model.TreeModelClass
import java.io.ByteArrayOutputStream
import java.text.SimpleDateFormat
import java.util.*


class CustomInfoWindowAdapter : GoogleMap.InfoWindowAdapter {
    private var mWindow: View
    private var mContext: Context

    constructor(mContext: Context) {
        this.mContext = mContext
        this.mWindow = LayoutInflater.from(mContext).inflate(org.arboristasurbanos.treeplant.R.layout.custom_marker_maps, null)
    }

    private fun renderWindow(marker: Marker, view: View): View {
        val tvTitle = view.findViewById(org.arboristasurbanos.treeplant.R.id.title) as TextView
        val tvSnippet = view.findViewById(org.arboristasurbanos.treeplant.R.id.description) as TextView
        val ivAvatar = view.findViewById(org.arboristasurbanos.treeplant.R.id.treeAvatar) as ImageView
        val tvAge = view.findViewById(org.arboristasurbanos.treeplant.R.id.description_age) as TextView
        val tvDate = view.findViewById(org.arboristasurbanos.treeplant.R.id.description_date) as TextView

        val title = marker.title
        if (title != "") {
            tvTitle.text = title
        }

        val snippet = marker.snippet
        if (snippet != "") {
            tvSnippet.text = snippet
        }
        val markerId = marker.tag as? Int
        markerId?.let {
           if (markerId == -1){
                ivAvatar.visibility = View.GONE
                return mWindow
            }
            ivAvatar.visibility = View.VISIBLE


            val tree = markerId?.let { getTree(markerId) }
            val sdf = SimpleDateFormat("dd/MM/yyyy")
            tvAge.text = this.friendlyDate(Date().time-sdf.parse(tree?.Date).time)
            tvDate.text = tree?.Date
            if (tree?.Photo != null) {
                ivAvatar.setImageBitmap(tree.Photo)
            } else
            {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    ivAvatar.setImageDrawable(mContext.getDrawable(R.drawable.ic_tree_map_marker))
                }
                else
                    ivAvatar.visibility = View.GONE
            }
        }
        return mWindow
    }

    private fun bitmapToByte(photo: Bitmap): Any {
        var stream = ByteArrayOutputStream();
        photo.compress(Bitmap.CompressFormat.JPEG, 100, stream);
        var byteArray = stream.toByteArray();
        return byteArray;
    }

    private fun getTree(markerId: Int): TreeModelClass? {
        val databaseHandler: DatabaseHandler = DatabaseHandler(mContext)
        return databaseHandler.viewTree(markerId)
    }
    override fun getInfoContents(p0: Marker): View? {
        return renderWindow(p0, mWindow)
    }

    override fun getInfoWindow(p0: Marker): View? {
        return renderWindow(p0, mWindow)
    }

    private fun friendlyDate(input: Long):String?{
        val context = mContext
        var days = (input/86400000)
        if (days > 360)
            return (days).floorDiv(360).toString() + " " + context.getString(R.string.home_years)
        else
            return days.toString() + " " + context.getString(R.string.home_days)
    }
}