package it.torino.totalshop.roomdb.entities

open class LocationData(var latitude : Double, var longitude : Double) {
    override fun toString(): String {
        return "Latitudine: "+this.latitude + ", Longitudine: "+ this.longitude
    }
}
