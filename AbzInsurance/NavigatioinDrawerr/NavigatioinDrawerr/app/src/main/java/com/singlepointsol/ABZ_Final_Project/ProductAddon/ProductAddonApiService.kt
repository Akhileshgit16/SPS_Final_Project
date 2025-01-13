package com.singlepointsol.ABZ_Final_Project.ProductAddon

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface ProductAddonApiService {

    @GET("api/Product")
    suspend fun fetchProductAddon(): Response<ProductAddonArray>

    @POST("api/Product/{productID}")
    suspend fun addProductAddon(
        @Path("productID") productID: String,
        @Body productaddon: ProductAddonItem
    ): Response<ProductAddonItem>



    @PUT("api/Product/{productID}")
    suspend fun updateProductAddon(
        @Path("productID") id: String,
        @Body productaddon: ProductAddonItem
    ): Response<ProductAddonItem>

    @DELETE("api/Product/{productID}")
    suspend fun deleteProductAddon(@Path("productID") id: String): Response<Unit>
}