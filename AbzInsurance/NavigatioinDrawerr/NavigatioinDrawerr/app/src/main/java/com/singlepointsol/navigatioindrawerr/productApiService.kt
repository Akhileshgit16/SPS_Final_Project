package com.singlepointsol.navigatioindrawerr

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface productApiService{
    @GET("api/Product")
    suspend fun fetchProductDetails(): Response<ProductArray>

    @POST("api/Product/{productID}")
    suspend fun addProductDetails(
        @Path("productID") productID: String,
        @Body product: ProductItem
    ): Response<ProductItem>



    @PUT("api/Product/{productID}")
    suspend fun updateProductDetails(
        @Path("productID") id: String,
        @Body product: ProductItem
    ): Response<Unit>


    @DELETE("api/Product/{productID}")
    suspend fun deleteProductDetails(@Path("productID") id: String): Response<Unit>

}