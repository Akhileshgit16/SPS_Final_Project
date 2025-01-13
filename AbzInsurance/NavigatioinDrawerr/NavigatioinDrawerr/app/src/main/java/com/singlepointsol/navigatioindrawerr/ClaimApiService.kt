package com.singlepointsol.navigatioindrawerr

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface ClaimApiService {
    @GET("api/Claim")
    suspend fun fetchClaimNumbers(): Response<ClaimArray>


    @POST("api/Claim/{claimNo}")
    suspend fun addClaimDetails(
        @Path("claimNo") claimNo: String,  // customerId in URL
        @Body claim: ClaimItem  // Customer data in body
    ): Response<ClaimItem>




    @PUT("api/Claim/{claimNo}")
    suspend fun updateClaimDetails(
        @Path("claimNo") id: String,
        @Body claim: ClaimItem
    ): Response<ClaimItem>

    @DELETE("api/Claim/{claimNo}")
    suspend fun deleteClaimDetails(@Path("claimNo") id: String): Response<Unit>
}