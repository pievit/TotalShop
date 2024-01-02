package it.torino.totalshop.roomdb.entities

import androidx.annotation.NonNull
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import java.util.*

@Entity(indices = [Index("id")])
open class UsersData(var email:String, var password: String, var userType: Boolean) {
    @PrimaryKey(autoGenerate = true)
    @NonNull
    var id: Int = 0

    override fun toString(): String {
        return "Id: "+this.id+",Email: "+this.email + ",Password: "+ this.password + ",UserType: "+this.userType
    }

}