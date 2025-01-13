package com.singlepointsol.navigatioindrawerr.ProductAddon

import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class ProductAddonInstance {

    companion object {
        private const val MAIN_URL = "https://abzproductwebapi-akshitha.azurewebsites.net/"

        private const val TOKEN = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJodHRwOi8vc2NoZW1hcy54bWxzb2FwLm9yZy93cy8yMDA1LzA1L2lkZW50aXR5L2NsYWltcy9uYW1lIjoiYWRoaWxha3NobWkiLCJodHRwOi8vc2NoZW1hcy5taWNyb3NvZnQuY29tL3dzLzIwMDgvMDYvaWRlbnRpdHkvY2xhaW1zL3JvbGUiOiJkZXZlbG9wZXIiLCJleHAiOjE3MzYyNzA0NzcsImlzcyI6Imh0dHBzOi8vd3d3LnRlYW0yLmNvbSIsImF1ZCI6Imh0dHBzOi8vd3d3LnRlYW0yLmNvbSJ9.rfmOUIvj_86A3qFS2iNZKwzDe7qbdcraa4U_hMqFbO0"

        fun getProductInstance(): Retrofit {
            val client = OkHttpClient.Builder()
                .addInterceptor { chain ->
                    val request = chain.request().newBuilder()
                        .addHeader("Authorization", "Bearer $TOKEN")
                        .build()
                    chain.proceed(request)
                }
                .build()

            return Retrofit.Builder()
                .baseUrl(MAIN_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create(GsonBuilder().create()))
                .build()
        }
    }
}
