package studio.bz_soft.mapkittest.data.repository

import studio.bz_soft.mapkittest.root.Either

interface Network {
    suspend fun getPointDescription(latitude: Double, longitude: Double): Either<Exception, String>

    suspend fun getTest(): Either<Exception, String>
}