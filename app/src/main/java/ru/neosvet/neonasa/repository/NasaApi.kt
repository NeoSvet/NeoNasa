package ru.neosvet.neonasa.repository

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface NasaApi {
    @GET("planetary/apod")
    fun getDayPhoto(
        @Query("api_key") apiKey: String,
        //@Query("date") date: String
    ): Call<DayPhotoResponse>
}