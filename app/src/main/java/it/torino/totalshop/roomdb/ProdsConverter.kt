package it.torino.totalshop.roomdb

import android.util.Log
import androidx.room.ProvidedTypeConverter
import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import it.torino.totalshop.roomdb.entities.ProductsData
import java.lang.reflect.Type


class ProdsConverter {
    @TypeConverter
    fun prodsToJson(prods: ProdsList): String{
        return Gson().toJson(prods.prods)
    }

    @TypeConverter
    fun jsonToProds(json: String?): ProdsList{
        var respl: ProdsList = ProdsList(emptyMap<ProductsData,Int>())
        if (json == null) {
            return respl
        }
        val type = object : TypeToken<Map<Any, Int>>() {}.type
        Log.d("test",json)
        val objectMap: Map<Any, Int> = Gson().fromJson(json, type)


        // Convert the keys from String to Object
//        val objectMap: Map<ProductsData, Int> = map.mapKeys { it.key as ProductsData }
        respl = ProdsList(objectMap as Map<ProductsData, Int>)
        return respl


//        return Gson().fromJson(json,object: TypeToken<Map<ProductsData,Int>>() {}.type)
    }

}