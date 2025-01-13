package com.singlepointsol.navigatioindrawerr

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface vehicleApiService{
    @GET("api/Vehicle")
    suspend fun fetchVehicleDetails(): Response<VehicleArray>

    @POST("api/Vehicle/{regNo}")
    suspend fun addVehicleDetails(
        @Path("regNo") regNo: String,
        @Body vehicle: VehicleItem
    ): Response<VehicleItem>



    @PUT("api/Vehicle/{regNo}")
    suspend fun updateVehicleDetails(
        @Path("regNo") id: String,
        @Body vehicle: VehicleItem
    ): Response<VehicleItem>

    @DELETE("api/Vehicle/{regNo}")
    suspend fun deleteVehicleDetails(@Path("regNo") id: String): Response<Unit>
}