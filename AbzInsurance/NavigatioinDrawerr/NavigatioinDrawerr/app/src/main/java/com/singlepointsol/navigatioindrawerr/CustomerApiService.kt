package com.singlepointsol.navigatioindrawerr

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface CustomerApiService {

    @GET("api/Customer")
    suspend fun fetchCustomerDetails(): Response<CustomerArray>


    @POST("api/Customer/{customerID}")
    suspend fun addCustomerDetails(
        @Path("customerID") customerID: String,  // customerId in URL
        @Body customer: CustomerItem  // Customer data in body
    ): Response<CustomerItem>




    @PUT("api/Customer/{customerID}")
    suspend fun updateCustomerDetails(
        @Path("customerID") id: String,
        @Body customer: CustomerItem
    ): Response<CustomerItem>

    @DELETE("api/Customer/{customerID}")
    suspend fun deleteCustomerDetails(@Path("customerID") id: String): Response<Unit>
}