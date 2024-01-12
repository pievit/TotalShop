package it.torino.totalshop.roomdb

import it.torino.totalshop.roomdb.entities.ProductsData

data class ProdsList(var prods: Map<ProductsData,Int>) {
    override fun toString(): String {
        var s = prods.keys.joinToString("\n"){
            key->
            key.name+" ("+prods.get(key).toString()+"*"+key.price+"€)     "+(key.price*prods.get(key)!!)+"€"
        }
        return s
    }
}
