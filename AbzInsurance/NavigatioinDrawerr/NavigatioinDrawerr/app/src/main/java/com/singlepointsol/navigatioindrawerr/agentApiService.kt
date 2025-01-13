package com.singlepointsol.navigatioindrawerr

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface agentApiService {
    @GET("api/Agent")
    suspend fun fetchAgent(): Response<AgentArray>

    @POST("api/Agent/{agentID}")
    suspend fun addAgent(
        @Path("agentID") agentID: String,
        @Body agent: AgentItem
    ): Response<AgentItem>



    @PUT("api/Agent/{agentID}")
    suspend fun updateAgent(
        @Path("agentID") id: String,
        @Body agent: AgentItem
    ): Response<AgentItem>

    @DELETE("api/Agent/{agentID}")
    suspend fun deleteAgent(@Path("agentID") id: String): Response<Unit>
}

