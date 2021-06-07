package ru.neosvet.neonasa.model

import android.widget.ImageView
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.squareup.picasso.Picasso
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import ru.neosvet.neonasa.BuildConfig
import ru.neosvet.neonasa.R
import ru.neosvet.neonasa.repository.DayPhotoResponse
import ru.neosvet.neonasa.repository.DayPhotoState
import ru.neosvet.neonasa.repository.NasaRetrofit

class DayPhotoModel : ViewModel() {
    private val liveDataForViewToObserve: MutableLiveData<DayPhotoState> = MutableLiveData()
    private val retrofitImpl = NasaRetrofit()

    fun getState(): LiveData<DayPhotoState> {
        sendServerRequest()
        return liveDataForViewToObserve
    }

    private fun sendServerRequest() {
        liveDataForViewToObserve.value = DayPhotoState.Loading(null)
        val apiKey: String = BuildConfig.NASA_API_KEY
        if (apiKey.isBlank()) {
            DayPhotoState.Error(Throwable("You need API key"))
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
                            liveDataForViewToObserve.value =
                                DayPhotoState.Error(Throwable("Response is empty"))
                        } else if (response.mediaType.equals("image")) {
                            liveDataForViewToObserve.value =
                                DayPhotoState.SuccessPhoto(response)
                        } else {
                            liveDataForViewToObserve.value =
                                DayPhotoState.SuccessVideo(response)
                        }
                    } else {
                        val message = rawResponse.message()
                        if (message.isNullOrEmpty()) {
                            liveDataForViewToObserve.value =
                                DayPhotoState.Error(Throwable("Unidentified error"))
                        } else {
                            liveDataForViewToObserve.value =
                                DayPhotoState.Error(Throwable(message))
                        }
                    }
                }

                override fun onFailure(call: Call<DayPhotoResponse>, error: Throwable) {
                    liveDataForViewToObserve.value = DayPhotoState.Error(error)
                }
            })
        }
    }

    fun loadImage(view: ImageView, url: String) {
        Picasso.get()
            .load(url)
            .placeholder(R.drawable.ic_no_photo_vector)
            .error(R.drawable.ic_load_error_vector)
            .into(view)
    }
}