package com.singlepointsol.navigatioindrawerr

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface CustomerQueryApiService {
    @GET("api/CustomerQuery")
    suspend fun fetchCustomerQuery(): Response<CustomerQueryArray>

    @POST("api/CustomerQuery")
    suspend fun addCustomerQueryDetails(
        @Body query: CustomerQueryItem
    ): Response<CustomerQueryItem>




    @PUT("api/CustomerQuery/{queryID}")
    suspend fun updateCustomerQueryDetails(
        @Path("queryID") id: String,
        @Body customerQuery: CustomerQueryItem
    ): Response<CustomerQueryItem>
    @DELETE("api/CustomerQuery/{queryID}")
    suspend fun deleteCustomerQueryDetails(@Path("queryID") id: String): Response<Unit>
}