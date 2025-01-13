package com.singlepointsol.navigatioindrawerr

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface QueryResponseApiService {
    @GET("api/QueryResponse")
    suspend fun fetchQueryResponseDetails(): Response<QueryResponseArray>

    @POST("api/QueryResponse")
    suspend fun addQueryResponseDetails(
        @Body queryResponse: QueryResponseItem
    ): Response<QueryResponseItem>

    @PUT("api/QueryResponse")
    suspend fun updateQueryResponseDetails(
        @Body queryResponse: QueryResponseItem
    ): Response<QueryResponseItem>


    @DELETE("api/CustomerQuery/{queryID}")
    suspend fun deleteQueryResponseDetails(
        @Path("queryID") queryID: String
    ): Response<Unit>

}