package com.dicoding.githubuser.api

import com.dicoding.githubuser.DetailResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Path

interface ApiService {
    @GET("users/{username}")
    fun getDetailUser(
        @Path("username") username: String,
        @Header("Authorization") token: String
    ): Call<DetailResponse>
}
