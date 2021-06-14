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

    @GET("neo/rest/v1/feed")
    fun getAsteroids(
        //@Query("start_date") startDate: String,
        //@Query("end_date") endDate: String,
        @Query("api_key") apiKey: String
    ): Call<AsteroidsResponse>

    @GET("DONKI/CME")
    fun getWeatherCME(
        //@Query("startDate") startDate: String,
        //@Query("endDate") endDate: String,
        @Query("api_key") apiKey: String
    ): Call<List<WeatherCMEResponse>>

    @GET("DONKI/GST")
    fun getWeatherGST(
        //@Query("startDate") startDate: String,
        //@Query("endDate") endDate: String,
        @Query("api_key") apiKey: String
    ): Call<List<WeatherGSTResponse>>

    @GET("DONKI/IPS")
    fun getWeatherIPS(
        //@Query("startDate") startDate: String,
        //@Query("endDate") endDate: String,
        @Query("api_key") apiKey: String
    ): Call<List<WeatherIPSResponse>>

    @GET("DONKI/FLR")
    fun getWeatherFLR(
        //@Query("startDate") startDate: String,
        //@Query("endDate") endDate: String,
        @Query("api_key") apiKey: String
    ): Call<List<WeatherFLRResponse>>

    @GET("DONKI/SEP")
    fun getWeatherSEP(
        //@Query("startDate") startDate: String,
        //@Query("endDate") endDate: String,
        @Query("api_key") apiKey: String
    ): Call<List<WeatherSEPResponse>>

    @GET("DONKI/MPC")
    fun getWeatherMPC(
        //@Query("startDate") startDate: String,
        //@Query("endDate") endDate: String,
        @Query("api_key") apiKey: String
    ): Call<List<WeatherMPCResponse>>

    @GET("DONKI/RBE")
    fun getWeatherRBE(
        //@Query("startDate") startDate: String,
        //@Query("endDate") endDate: String,
        @Query("api_key") apiKey: String
    ): Call<List<WeatherRBEResponse>>

    @GET("DONKI/HSS")
    fun getWeatherHSS(
        //@Query("startDate") startDate: String,
        //@Query("endDate") endDate: String,
        @Query("api_key") apiKey: String
    ): Call<List<WeatherHSSResponse>>

    @GET("DONKI/WSAEnlilSimulations")
    fun getWeatherWSA(
        //@Query("startDate") startDate: String,
        //@Query("endDate") endDate: String,
        @Query("api_key") apiKey: String
    ): Call<List<WeatherWSAResponse>>
}