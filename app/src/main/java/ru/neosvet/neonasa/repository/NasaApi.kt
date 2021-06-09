package ru.neosvet.neonasa.repository

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface NasaApi {
    @GET("planetary/apod")
    fun getDayPhoto(
        //@Query("date") date: String
        @Query("api_key") apiKey: String
    ): Call<DayPhotoResponse>

    @GET("EPIC/api/natural/images")
    fun getEarthPhoto(
        //@Path("natural/date") date: String
        @Query("api_key") apiKey: String
    ): Call<List<EarthPhotoResponse>>

    @GET("mars-photos/api/v1/rovers/curiosity/photos")
    fun getMarsPhoto(
        @Query("sol") sol: Int,
        //@Query("camera") camera: String,
        //@Query("page") page: Int,
        @Query("api_key") apiKey: String
    ): Call<MarsPhotoResponse>
}