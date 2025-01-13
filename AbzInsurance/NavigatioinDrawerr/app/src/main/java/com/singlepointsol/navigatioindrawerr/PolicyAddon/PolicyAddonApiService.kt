package com.singlepointsol.navigatioindrawerr.PolicyAddon

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface PolicyAddonApiService {
    @GET("api/PolicyAddon")
    suspend fun fetchPolicyAddonDetails(): Response<PolicyAddonArray>

    @POST("api/PolicyAddon/{policyNo}")
    suspend fun addPolicyAddonDetails(
        @Path("policyNo") policyNo: String,
        @Body policy: PolicyAddonItem
    ): Response<PolicyAddonItem>

    @PUT("api/PolicyAddon/{policyNo}")
    suspend fun updatePolicyAddonDetails(
        @Path("policyNo") policyNo: String,
        @Body policyAddon: PolicyAddonItem
    ): Response<Unit>


    @DELETE("api/PolicyAddon/{policyNo}/{addonID}")
    suspend fun deletePolicyDetails(
        @Path("policyNo") policyNo: String,
        @Path("addonID") addonID: String
    ): Response<Unit>



}