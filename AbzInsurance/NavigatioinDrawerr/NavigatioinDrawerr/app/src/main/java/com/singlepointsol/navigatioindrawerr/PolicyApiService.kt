package com.singlepointsol.navigatioindrawerr

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface PolicyApiService {


    @GET("api/Policy")
    suspend fun fetchPolicyDetails(): Response<PolicyArray>

    @POST("api/Policy/{policyNo}")
    suspend fun addPolicyDetails(
        @Path("policyNo") policyNo: String,
        @Body policy: PolicyItem
    ): Response<PolicyItem>



    @PUT("api/Policy/{policyNo}")
    suspend fun updatePolicyDetails(
        @Path("policyNo") id: String,
        @Body policy: PolicyItem
    ): Response<PolicyItem>

    @DELETE("api/Policy/{policyNo}")
    suspend fun deletePolicyDetails(@Path("policyNo") policyNo: String): Response<Unit>


}