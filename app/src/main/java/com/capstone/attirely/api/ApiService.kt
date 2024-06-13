package com.capstone.attirely.api

import com.capstone.attirely.data.Outfit
import retrofit2.http.GET

interface ApiService {
    @GET("cowok")
    suspend fun getCowokOutfits(): List<Outfit>

    @GET("cewek")
    suspend fun getCewekOutfits(): List<Outfit>
}