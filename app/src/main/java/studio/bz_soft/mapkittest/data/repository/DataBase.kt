package studio.bz_soft.mapkittest.data.repository

import kotlinx.coroutines.flow.Flow
import studio.bz_soft.mapkittest.data.models.db.Location
import studio.bz_soft.mapkittest.root.Either

interface DataBase {
    suspend fun insertLocation(location: Location): Either<Exception, Unit>
    suspend fun deleteLocation(location: Location): Either<Exception, Unit>
    suspend fun updateLocation(location: Location): Either<Exception, Unit>
    suspend fun getAllFromLocations(): Either<Exception, List<Location>>
    suspend fun getLastLocation(): Either<Exception, Location>
    fun watchAllLocation(): Flow<List<Location>>
    fun watchLastLocation(): Flow<Location>
}