package studio.bz_soft.mapkittest.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import studio.bz_soft.mapkittest.data.db.converters.UUIDConverter
import studio.bz_soft.mapkittest.data.db.dao.LocationDao
import studio.bz_soft.mapkittest.data.models.db.Location
import studio.bz_soft.mapkittest.root.Constants.DB_NAME

@Database(entities = [Location::class],
    version = 1, exportSchema = false)
@TypeConverters(value = [UUIDConverter::class])
abstract class RoomDB : RoomDatabase() {

    abstract fun locationDao() : LocationDao

    companion object {
        @Volatile
        private var INSTANCE: RoomDB? = null

        fun getDataBase(context: Context): RoomDB =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: createDB(context).also { INSTANCE = it }
            }

        private fun createDB(context: Context) = Room
            .databaseBuilder(context.applicationContext, RoomDB::class.java, DB_NAME)
            .fallbackToDestructiveMigration()
            .build()
    }
}