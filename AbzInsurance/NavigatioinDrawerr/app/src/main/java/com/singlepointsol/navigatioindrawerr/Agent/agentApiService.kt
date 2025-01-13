package com.singlepointsol.navigatioindrawerr.Agent

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface agentApiService {

    @GET("api/Agent")
    suspend fun fetchAgent(): Response<List<AgentItem>>

    @GET("api/Agent/{agentID}")
    suspend fun fetchAgentById(@Path("agentID") agentID: String): Response<AgentItem>

    @POST("api/Agent/{agentID}")
    suspend fun addAgent(@Path("agentID") agentID: String, @Body agent: AgentItem): Response<Void>

    @PUT("api/Agent/{agentID}")
    suspend fun updateAgent(@Path("agentID") agentID: String, @Body agent: AgentItem): Response<Void>

    @DELETE("api/Agent/{agentID}")
    suspend fun deleteAgent(@Path("agentID") agentID: String): Response<Void>

}

