package com.singlepointsol.navigatioindrawerr.Customerquery

import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class CustomerQueryInstance {
    companion object {
        private const val MAIN_URL = "https://abzcustomerquerywebapi-akshitha.azurewebsites.net/"
        private const val TOKEN ="eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJodHRwOi8vc2NoZW1hcy54bWxzb2FwLm9yZy93cy8yMDA1LzA1L2lkZW50aXR5L2NsYWltcy9uYW1lIjoiYWRoaWxha3NobWkiLCJodHRwOi8vc2NoZW1hcy5taWNyb3NvZnQuY29tL3dzLzIwMDgvMDYvaWRlbnRpdHkvY2xhaW1zL3JvbGUiOiJkZXZlbG9wZXIiLCJleHAiOjE3MzYyNzA0NzcsImlzcyI6Imh0dHBzOi8vd3d3LnRlYW0yLmNvbSIsImF1ZCI6Imh0dHBzOi8vd3d3LnRlYW0yLmNvbSJ9.rfmOUIvj_86A3qFS2iNZKwzDe7qbdcraa4U_hMqFbO0"
        fun getInstance(): Retrofit {
            // Configure OkHttpClient with an interceptor for the Authorization header
            val client = OkHttpClient.Builder()
                .addInterceptor { chain ->
                    val request = chain.request().newBuilder()
                        .addHeader("Authorization", "Bearer $TOKEN") // Add token to the header
                        .build()
                    chain.proceed(request)
                }
                .build()

            // Build Retrofit instance with OkHttpClient and Gson converter
            return Retrofit.Builder()
                .baseUrl(MAIN_URL)
                .client(client) // Attach the OkHttpClient with the interceptor
                .addConverterFactory(GsonConverterFactory.create(GsonBuilder().create()))
                .build()
        }
    }
}