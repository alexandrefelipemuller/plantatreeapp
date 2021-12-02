package org.arboristasurbanos.treeplant.model

class TreeModelClass {
    fun getLat(): Double {
        return this.Lat!!
    }
    fun getLong(): Double {
        return this.Long!!
    }

    constructor(Id: Int?, Name: String, Date: String, Lat: Double, Long: Double){
        this.Id = Id
        this.Name = Name
        this.Date = Date
        this.Lat = Lat
        this.Long = Long

    }

    constructor(Name: String, Date: String, Lat: Double, Long: Double){
        this.Name = Name
        this.Date = Date
        this.Lat = Lat
        this.Long = Long
    }

    var Id: Int? = null
    var Name: String? = null
    var Date: String? = null
    var Lat: Double? = null
    var Long: Double? = null
}
