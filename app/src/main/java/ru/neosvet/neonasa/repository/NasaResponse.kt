package ru.neosvet.neonasa.repository

import com.google.gson.annotations.SerializedName

data class PhotoResponse(
    val title: String?,
    val explanation: String?,
    val url: String?,
    val hdurl: String?
)

data class VideoResponse(
    val title: String?,
    val explanation: String?,
    val url: String?
)

data class DayPhotoResponse(
    @field:SerializedName("copyright") val copyright: String?,
    @field:SerializedName("date") val date: String?,
    @field:SerializedName("explanation") val explanation: String?,
    @field:SerializedName("media_type") val mediaType: String?,
    @field:SerializedName("title") val title: String?,
    @field:SerializedName("url") val url: String?,
    @field:SerializedName("hdurl") val hdurl: String?
)

data class EarthPhotoResponse(
    @field:SerializedName("identifier") val identifier: String,
    @field:SerializedName("caption") val caption: String,
    @field:SerializedName("image") val image: String,
    @field:SerializedName("version") val version: String,
    @field:SerializedName("centroid_coordinates") val centroid_coordinates: Coord,
    @field:SerializedName("date") val date: String,
)

data class Coord(
    @field:SerializedName("lat") val lat: Double,
    @field:SerializedName("lon") val lon: Double
)

data class MarsPhotoResponse(
    @field:SerializedName("photos") var photos: List<Photos>
)

data class Photos(
    @field:SerializedName("id") var id: Int,
    @field:SerializedName("sol") var sol: Int,
    @field:SerializedName("camera") var camera: Camera,
    @field:SerializedName("img_src") var imgSrc: String,
    @field:SerializedName("earth_date") var earthDate: String,
    @field:SerializedName("rover") var rover: Rover
)

data class Camera(
    @field:SerializedName("id") var id: Int,
    @field:SerializedName("name") var name: String,
    @field:SerializedName("rover_id") var roverId: Int,
    @field:SerializedName("full_name") var fullName: String
)

data class Rover(
    @field:SerializedName("id") var id: Int,
    @field:SerializedName("name") var name: String,
    @field:SerializedName("landing_date") var landingDate: String,
    @field:SerializedName("launch_date") var launchDate: String,
    @field:SerializedName("status") var status: String
)