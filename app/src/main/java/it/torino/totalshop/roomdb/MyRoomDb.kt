package it.torino.totalshop.roomdb

import android.content.Context
import androidx.room.Database
import androidx.room.RoomDatabase
import it.torino.totalshop.roomdb.entities.StoreData
import it.torino.totalshop.roomdb.entities.StoreDataDAO
import it.torino.totalshop.roomdb.entities.UsersData
import it.torino.totalshop.roomdb.entities.UsersDataDAO

@Database(
    entities = [UsersData::class, StoreData::class],
    version = 6,
    exportSchema = true
)
abstract class MyRoomDb: RoomDatabase() {
    abstract fun myUsersDataDao() : UsersDataDAO?
    abstract fun myStoreDataDao(): StoreDataDAO?
    companion object{
        @Volatile
        private var INSTANCE: MyRoomDb? = null

        fun getDatabase(context: Context): MyRoomDb? {
            if (INSTANCE == null) {
                synchronized(MyRoomDb::class.java) {
                    if (INSTANCE == null) {
                        INSTANCE = androidx.room.Room.databaseBuilder(
                            context.applicationContext,
                            MyRoomDb::class.java, "TotalShopDb"
                        )
                            .addMigrations()
                            .fallbackToDestructiveMigration()
                            .addCallback(roomDatabaseCallback)
                            .build()
                    }
                }
            }
            return INSTANCE
        }

        private val roomDatabaseCallback: Callback =
            object : Callback() {
            }
    }
}