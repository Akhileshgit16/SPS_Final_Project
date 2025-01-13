package com.singlepointsol.navigatioindrawerr.Queryresponse

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
        @Body updatedQuery: QueryResponseItem  // Updated data to be sent
    ): Response<Unit>

    @DELETE("api/QueryResponse/{queryID}/{srNo}")
    suspend fun deleteQueryResponseDetails(
        @Path("queryID") queryID: String,
        @Path("srNo") srNo: String
    ): Response<Unit>



}