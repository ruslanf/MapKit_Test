package studio.bz_soft.mapkittest.data.db.dao

import androidx.room.*
import kotlinx.coroutines.flow.Flow
import studio.bz_soft.mapkittest.data.models.db.Location

@Dao
interface LocationDao: BaseDao<Location> {

    @Query("Select * from location")
    suspend fun getAllFromLocation(): List<Location>

    @Query("select * from location order by uuid desc")
    suspend fun getLastLocation(): Location

    @Query("Select * from location")
    fun watchLocation(): Flow<List<Location>>

    @Query("select * from location order by uuid desc")
    fun watchLastLocation(): Flow<Location>
}