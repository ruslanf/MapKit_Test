package studio.bz_soft.mapkittest.data.repository

import kotlinx.coroutines.flow.Flow
import studio.bz_soft.mapkittest.data.db.dao.LocationDao
import studio.bz_soft.mapkittest.data.models.db.Location
import studio.bz_soft.mapkittest.root.Either
import studio.bz_soft.mapkittest.root.safeRequest

class DataBaseRepository(
    private val locationDao: LocationDao
) : DataBase {
    override suspend fun insertLocation(location: Location): Either<Exception, Unit> =
        safeRequest { locationDao.insert(location) }

    override suspend fun deleteLocation(location: Location): Either<Exception, Unit> =
        safeRequest { locationDao.delete(location) }

    override suspend fun updateLocation(location: Location): Either<Exception, Unit> =
        safeRequest { locationDao.update(location) }

    override suspend fun getAllFromLocations(): Either<Exception, List<Location>> =
        safeRequest { locationDao.getAllFromLocation() }

    override suspend fun getLastLocation(): Either<Exception, Location> =
        safeRequest { locationDao.getLastLocation() }

    override fun watchAllLocation(): Flow<List<Location>> = locationDao.watchLocation()

    override fun watchLastLocation(): Flow<Location> = locationDao.watchLastLocation()
}