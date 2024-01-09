package it.torino.totalshop.roomdb

import androidx.room.ProvidedTypeConverter
import androidx.room.TypeConverter
import com.google.gson.Gson


class ProdsConverter {
    @TypeConverter
    fun prodsToJson(prods: ProdsList): String{
        return Gson().toJson(prods)
    }

    @TypeConverter
    fun jsonToProds(json: String): ProdsList{
        return Gson().fromJson(json,ProdsList::class.java)
    }
}