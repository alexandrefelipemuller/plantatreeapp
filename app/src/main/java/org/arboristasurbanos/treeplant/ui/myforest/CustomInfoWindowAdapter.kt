package org.arboristasurbanos.treeplant.ui.myforest

import android.content.Context
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.RequiresApi
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.Marker
import org.arboristasurbanos.treeplant.R
import org.arboristasurbanos.treeplant.database.DatabaseHandler
import org.arboristasurbanos.treeplant.model.TreeModelClass


class CustomInfoWindowAdapter : GoogleMap.InfoWindowAdapter {
    private var mWindow: View
    private var mContext: Context

    constructor(mContext: Context) {
        this.mContext = mContext
        this.mWindow = LayoutInflater.from(mContext).inflate(org.arboristasurbanos.treeplant.R.layout.custom_marker_maps, null)
    }

    private fun renderWindow(marker: Marker, view: View): View {
        val title = marker.title
        val tvTitle = view.findViewById(org.arboristasurbanos.treeplant.R.id.title) as TextView

        if (title != "") {
            tvTitle.text = title
        }

        val snippet = marker.snippet
        val tvSnippet = view.findViewById(org.arboristasurbanos.treeplant.R.id.description) as TextView

        if (snippet != "") {
            tvSnippet.text = snippet
        }
        val markerId = marker.tag as? Int
        markerId?.let {
            val ivAvatar = view.findViewById(org.arboristasurbanos.treeplant.R.id.treeAvatar) as ImageView
            val tree = markerId?.let { getTree(markerId) }
            if (tree?.Photo != null) {
                ivAvatar.setImageBitmap(tree.Photo)
            } else
            {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    ivAvatar.setImageDrawable(mContext.getDrawable(R.drawable.ic_tree_map_marker))
                }
            }
        }
        return mWindow
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
}