package org.arboristasurbanos.treeplant.model

import android.graphics.Bitmap

class TreeModelClass {
    fun getLat(): Double {
        return this.Lat!!
    }
    fun getLong(): Double {
        return this.Long!!
    }

    constructor(Id: Int? = null, Name: String, Date: String, Lat: Double? = null, Long: Double? = null, Photo: Bitmap? = null){
        this.Id = Id
        this.Name = Name
        this.Date = Date
        this.Lat = Lat
        this.Long = Long
        this.Photo = Photo

    }
    var Id: Int? = null
    var Name: String? = null
    var Date: String? = null
    var Lat: Double? = null
    var Long: Double? = null
    var Photo: Bitmap? = null
}
