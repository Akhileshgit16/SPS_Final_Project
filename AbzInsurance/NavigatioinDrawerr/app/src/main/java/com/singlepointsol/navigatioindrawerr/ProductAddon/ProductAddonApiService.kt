package com.singlepointsol.navigatioindrawerr.ProductAddon

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface ProductAddonApiService {
    @GET("api/ProductAddon")
    suspend fun fetchProductaddOnDetails(): Response<ProductAddonArray>


    @POST("api/ProductAddon/{productID}")
    suspend fun addProductAddonDetails(
        @Path("productID") productID: String,
        @Body productAddon: ProductAddonItem
    ): Response<ProductAddonItem>



    @PUT("api/ProductAddon/{agentID}")
    suspend fun updateProductAddonDetails(
        @Path("productID") id: String,
        @Body product: ProductAddonItem
    ): Response<ProductAddonItem>


    @DELETE("api/ProductAddon/{productID}/{addonID}")
    suspend fun deleteProductAddonDetails(
        @Path("productID") productID: String,
        @Path("addonID") addonID: String
    ): Response<Unit>
}