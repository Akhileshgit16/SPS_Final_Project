package com.singlepointsol.ABZ_Final_Project.CustomerQuery

import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class CustomerQueryInstance {
    companion object {
        private const val MAIN_URL = "https://abzcustomerquerywebapi-akshitha.azurewebsites.net/"
        private const val token ="eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJodHRwOi8vc2NoZW1hcy54bWxzb2FwLm9yZy93cy8yMDA1LzA1L2lkZW50aXR5L2NsYWltcy9uYW1lIjoiQWxla2h5YSIsImh0dHA6Ly9zY2hlbWFzLm1pY3Jvc29mdC5jb20vd3MvMjAwOC8wNi9pZGVudGl0eS9jbGFpbXMvcm9sZSI6IlNvZnR3YXJlIiwiZXhwIjoxNzM1ODk0MTE5LCJpc3MiOiJodHRwczovL3d3dy50ZWFtMi5jb20iLCJhdWQiOiJodHRwczovL3d3dy50ZWFtMi5jb20ifQ.e790S8cXD1VEwLyfE_jye7PyYUHYvfH5PdSs2vo-KMY"
        fun getInstance(): Retrofit {
            // Configure OkHttpClient with an interceptor for the Authorization header
            val client = OkHttpClient.Builder()
                .addInterceptor { chain ->
                    val request = chain.request().newBuilder()
                        .addHeader("Authorization", "Bearer $token") // Add token to the header
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