package ru.neosvet.neonasa.model

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import ru.neosvet.neonasa.BuildConfig
import ru.neosvet.neonasa.repository.AsteroidsRepository
import ru.neosvet.neonasa.repository.AsteroidsResponse
import ru.neosvet.neonasa.repository.AsteroidsState
import ru.neosvet.neonasa.repository.NasaRetrofit
import ru.neosvet.neonasa.repository.room.AsteroidEntity

class AsteroidsModel : ViewModel() {
    private val state: MutableLiveData<AsteroidsState> = MutableLiveData()
    private val retrofitImpl = NasaRetrofit()
    private val repository = AsteroidsRepository()

    fun getState() = state

    fun removeAsterod(asteroid: AsteroidEntity) {
        repository.removeAsterod(asteroid)
    }

    fun requestAsteroids() {
        state.value = AsteroidsState.Loading
        val apiKey: String = BuildConfig.NASA_API_KEY
        if (apiKey.isBlank()) {
            AsteroidsState.Error(Throwable("You need API key"))
        } else {
            retrofitImpl.getRetrofit().getAsteroids(apiKey).enqueue(object :
                Callback<AsteroidsResponse> {
                override fun onResponse(
                    call: Call<AsteroidsResponse>,
                    rawResponse: Response<AsteroidsResponse>
                ) {
                    if (rawResponse.isSuccessful) {
                        val response = rawResponse.body()
                        if (response == null || response.objectsPerDay == null) {
                            state.value = AsteroidsState.Error(Throwable("Response is empty"))
                        } else {
                            responseToRepository(response)
                            state.value = AsteroidsState.Success
                        }
                    } else {
                        val message = rawResponse.message()
                        if (message.isNullOrEmpty()) {
                            state.value = AsteroidsState.Error(Throwable("Unidentified error"))
                        } else {
                            state.value = AsteroidsState.Error(Throwable(message))
                        }
                    }
                }

                override fun onFailure(call: Call<AsteroidsResponse>, error: Throwable) {
                    state.value = AsteroidsState.Error(error)
                }
            })
        }
    }

    private fun responseToRepository(response: AsteroidsResponse) {
        response.objectsPerDay?.forEach {
            repository.addGroup(it.key)
            it.value.forEach {
                repository.addAsteroid(it)
            }
        }
    }

    fun update(asteroid: AsteroidEntity) {
        repository.update(asteroid)
    }
}