package ru.neosvet.neonasa.model

import android.widget.ImageView
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.squareup.picasso.Picasso
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import ru.neosvet.neonasa.BuildConfig
import ru.neosvet.neonasa.R
import ru.neosvet.neonasa.repository.*

class PhotoModel : ViewModel() {
    private val state: MutableLiveData<PhotoState> = MutableLiveData()
    private val retrofitImpl = NasaRetrofit()

    fun getState() = state

    fun requestDayPhoto() {
        state.value = PhotoState.Loading
        val apiKey: String = BuildConfig.NASA_API_KEY
        if (apiKey.isBlank()) {
            PhotoState.Error(Throwable("You need API key"))
        } else {
            retrofitImpl.getRetrofit().getDayPhoto(apiKey).enqueue(object :
                Callback<DayPhotoResponse> {
                override fun onResponse(
                    call: Call<DayPhotoResponse>,
                    rawResponse: Response<DayPhotoResponse>
                ) {
                    if (rawResponse.isSuccessful) {
                        val response = rawResponse.body()
                        if (response == null || response.url.isNullOrEmpty()) {
                            state.value = PhotoState.Error(Throwable("Response is empty"))
                        } else if (response.mediaType.equals("image")) {
                            state.value = PhotoState.SuccessPhoto(responseToPhoto(response))
                        } else {
                            state.value = PhotoState.SuccessVideo(responseToVideo(response))
                        }
                    } else {
                        val message = rawResponse.message()
                        if (message.isNullOrEmpty()) {
                            state.value = PhotoState.Error(Throwable("Unidentified error"))
                        } else {
                            state.value = PhotoState.Error(Throwable(message))
                        }
                    }
                }

                override fun onFailure(call: Call<DayPhotoResponse>, error: Throwable) {
                    state.value = PhotoState.Error(error)
                }
            })
        }
    }

    private fun responseToPhoto(response: DayPhotoResponse) = PhotoResponse(
        response.title,
        response.explanation,
        response.url,
        response.hdurl
    )

    private fun responseToVideo(response: DayPhotoResponse) = VideoResponse(
        response.title,
        response.explanation,
        response.url
    )

    fun requestEarthPhoto() {
        state.value = PhotoState.Loading
        val apiKey: String = BuildConfig.NASA_API_KEY
        if (apiKey.isBlank()) {
            PhotoState.Error(Throwable("You need API key"))
        } else {
            retrofitImpl.getRetrofit().getEarthPhoto(apiKey).enqueue(object :
                Callback<List<EarthPhotoResponse>> {
                override fun onResponse(
                    call: Call<List<EarthPhotoResponse>>,
                    rawResponse: Response<List<EarthPhotoResponse>>
                ) {
                    if (rawResponse.isSuccessful) {
                        val response = rawResponse.body()
                        if (response == null || response.size == 0) {
                            state.value =
                                PhotoState.Error(Throwable("Response is empty"))
                        } else {
                            state.value =
                                PhotoState.SuccessPhoto(responseToPhoto(response[0]))
                        }
                    } else {
                        val message = rawResponse.message()
                        if (message.isNullOrEmpty()) {
                            state.value =
                                PhotoState.Error(Throwable("Unidentified error"))
                        } else {
                            state.value =
                                PhotoState.Error(Throwable(message))
                        }
                    }
                }

                override fun onFailure(call: Call<List<EarthPhotoResponse>>, error: Throwable) {
                    state.value = PhotoState.Error(error)
                }
            })
        }
    }

    private fun responseToPhoto(response: EarthPhotoResponse): PhotoResponse {
        var date = response.date.replace("-", "/")
        date = date.substring(0, date.indexOf(" "))

        return PhotoResponse(
            "Earth ${response.date}",
            response.caption,
            "https://epic.gsfc.nasa.gov/archive/natural/${date}/png/${response.image}.png",
            null
        )
    }

    fun requestMarsPhoto() {
        state.value = PhotoState.Loading
        val apiKey: String = BuildConfig.NASA_API_KEY
        if (apiKey.isBlank()) {
            PhotoState.Error(Throwable("You need API key"))
        } else {
            retrofitImpl.getRetrofit().getMarsPhoto(10, apiKey).enqueue(object :
                Callback<MarsPhotoResponse> {
                override fun onResponse(
                    call: Call<MarsPhotoResponse>,
                    rawResponse: Response<MarsPhotoResponse>
                ) {
                    if (rawResponse.isSuccessful) {
                        val response = rawResponse.body()
                        if (response == null || response.photos.size == 0) {
                            state.value =
                                PhotoState.Error(Throwable("Response is empty"))
                        } else {
                            state.value =
                                PhotoState.SuccessPhoto(responseToPhoto(response))
                        }
                    } else {
                        val message = rawResponse.message()
                        if (message.isNullOrEmpty()) {
                            state.value =
                                PhotoState.Error(Throwable("Unidentified error"))
                        } else {
                            state.value =
                                PhotoState.Error(Throwable(message))
                        }
                    }
                }

                override fun onFailure(call: Call<MarsPhotoResponse>, error: Throwable) {
                    state.value = PhotoState.Error(error)
                }
            })
        }
    }

    private fun responseToPhoto(response: MarsPhotoResponse): PhotoResponse {
        val photo = response.photos[0]

        return PhotoResponse(
            "Mars ${photo.earthDate}",
            photo.rover.name + ", " + photo.camera.fullName,
            photo.imgSrc,
            null
        )
    }

    fun loadImage(view: ImageView, url: String) {
        Picasso.get()
            .load(url)
            .placeholder(R.drawable.ic_no_photo_vector)
            .error(R.drawable.ic_load_error_vector)
            .into(view)
    }
}