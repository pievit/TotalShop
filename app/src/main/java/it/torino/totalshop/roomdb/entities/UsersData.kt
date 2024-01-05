package it.torino.totalshop.roomdb.entities

import androidx.annotation.NonNull
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import java.util.*

@Entity(primaryKeys = ["email"])
open class UsersData(var email:String, var password: String, var phone: String,var userType: Boolean) {


    override fun toString(): String {
        return "Email: "+this.email + ",Password: "+ this.password + ",Phone: "+phone+ ",UserType: "+this.userType
    }

}