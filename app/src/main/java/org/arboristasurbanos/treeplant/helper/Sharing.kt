package org.arboristasurbanos.treeplant.helper

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.widget.Toast
import androidx.core.content.FileProvider
import org.arboristasurbanos.treeplant.R
import org.arboristasurbanos.treeplant.model.TreeModelClass
import java.io.File
import java.io.FileOutputStream
import java.text.DecimalFormat
import kotlin.math.absoluteValue
import kotlin.math.ceil
import kotlin.math.floor
import kotlin.math.roundToInt


class Sharing {
    fun recognizePlant(bitmap: Bitmap?, mContext: Context) {
        val packageName = "org.plantnet"
        val intent =  Intent(Intent.ACTION_SEND)
        val uri = this.getmageToShare(bitmap!!, mContext,Bitmap.CompressFormat.JPEG)
        // putting uri of image to be shared
        intent.putExtra(Intent.EXTRA_STREAM, uri)
        // setting type to image
        intent.type = "image/jpeg"
        intent.setPackage(packageName)
        try {
            mContext.startActivity(intent)
        } catch (e: Exception){
            mContext.startActivity(
                Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse("https://play.google.com/store/apps/details?id=$packageName")
                )
            )
        }

    }

    fun shareImageandText(tree: TreeModelClass?, mContext: Context) {
        var bitmap = tree?.Photo
        val intent = Intent(Intent.ACTION_SEND)

        if (bitmap == null){
            intent.type = "text/plain"
        }
        else
        {
            val uri = this.getmageToShare(bitmap, mContext,Bitmap.CompressFormat.PNG)
            // putting uri of image to be shared
            intent.putExtra(Intent.EXTRA_STREAM, uri)
            // setting type to image
            intent.type = "image/png"
        }
        // adding text to share
        //TODO SCREENSHOT LOCATION, FANCY INFO
        intent.putExtra(Intent.EXTRA_TEXT, "I planted a tree, check my activity at Plant a Tree App\nCordinates:\nLatitude ${convertfromDecimal(tree?.Lat, true)}\nLongitude ${convertfromDecimal(tree?.Long, false)}\n #plantedatree #savetheearth")

        mContext.startActivity(Intent.createChooser(intent, mContext.getString(R.string.share)))
    }

    private fun convertfromDecimal(value: Double?, isLat: Boolean): String {
        if (value == null)
            return ""
        val degrees: Double = floor(value!!.absoluteValue)
        val minutesDecimal: Double = (value.absoluteValue - degrees)
        val minutes: Int = floor(minutesDecimal*60).roundToInt()
        var df: DecimalFormat = DecimalFormat("0.00")
        val seconds: String = df.format((minutesDecimal - (minutes.toDouble()/60))*3600)
        if (value < 0.0)
            if (isLat)
                return """${degrees.toInt()} º ${minutes}' $seconds'' S"""
            else
                return """-${degrees.toInt()} º ${minutes}' $seconds'' E"""
        else
            if (isLat)
                return """${degrees.toInt()} º ${minutes}' $seconds'' N"""
            else
                return """${degrees.toInt()} º ${minutes}' $seconds'' W"""

    }

    // Retrieving the url to share
    private fun getmageToShare(bitmap: Bitmap, mContext: Context, format:Bitmap.CompressFormat): Uri? {
        val imagefolder = File(mContext.cacheDir, "images")
        var uri: Uri? = null
        try {
            imagefolder.mkdirs()
            val file = File(imagefolder, "shared_image.png")
            val outputStream = FileOutputStream(file)
            bitmap.compress(format, 90, outputStream)
            outputStream.flush()
            outputStream.close()
            uri = FileProvider.getUriForFile(mContext, "org.arboristasurbanos.treeplant.helper.GenericFileProvider", file)
        } catch (e: Exception) {
            Toast.makeText(mContext, "" + e.message, Toast.LENGTH_LONG).show()
        }
        return uri
    }
}