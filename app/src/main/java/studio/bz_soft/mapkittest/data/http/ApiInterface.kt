package studio.bz_soft.mapkittest.data.http

import retrofit2.http.GET
import retrofit2.http.Path
import studio.bz_soft.mapkittest.root.Constants.BASE
import studio.bz_soft.mapkittest.root.Constants.REVERSE_API

interface ApiInterface {
    @GET("$REVERSE_API&lat={latitude}&lon={longitude}")
    suspend fun getLocationDefinition(
        @Path("latitude") latitude: Double,
        @Path("longitude") longitude: Double
    ): String

    @GET("$BASE")
    suspend fun getTest(): String
}