package com.singlepointsol.ABZ_Final_Project.Proposal

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface ProposalApiService {
    @GET("api/Proposal")
    suspend fun fetchProposalDetails(): Response<ProposalArray>

    @POST("api/Proposal/{proposalNo}")
    suspend fun addProposalDetails(
        @Path("proposalNo") proposalNo: String,
        @Body proposal: ProposalItem
    ): Response<ProposalItem>


    @PUT("api/Proposal/{proposalNo}")
    suspend fun updateProposalDetails(
        @Path("proposalNo") id: String,
        @Body proposal: ProposalItem
    ): Response<Unit>

    @DELETE("api/Proposal/{proposalNo}")
    suspend fun deleteProposalDetails(@Path("proposalNo") id: String): Response<Unit>
}