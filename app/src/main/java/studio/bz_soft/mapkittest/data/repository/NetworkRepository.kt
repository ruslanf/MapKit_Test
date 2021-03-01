package studio.bz_soft.mapkittest.data.repository

import studio.bz_soft.mapkittest.data.http.ApiInterface
import studio.bz_soft.mapkittest.root.Either
import studio.bz_soft.mapkittest.root.safeRequest

class NetworkRepository(
    private val api: ApiInterface
) : Network {
    override suspend fun getPointDescription(latitude: Double, longitude: Double): Either<Exception, String> =
        safeRequest { api.getLocationDefinition(latitude, longitude) }

    override suspend fun getTest(): Either<Exception, String> =
        safeRequest { api.getTest() }
}