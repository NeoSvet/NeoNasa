package ru.neosvet.neonasa.repository

sealed class PhotoState {
    data class SuccessPhoto(val response: PhotoResponse) : PhotoState()
    data class SuccessVideo(val response: VideoResponse) : PhotoState()
    data class Error(val error: Throwable) : PhotoState()
    object Loading : PhotoState()
}

sealed class WeatherState {
    data class Success(val list: List<Pair<String, String>>) : WeatherState()
    data class Error(val error: Throwable) : WeatherState()
    object Loading : WeatherState()
}

sealed class AsteroidsState {
    data class Success(val response: AsteroidsData) : AsteroidsState()
    data class Error(val error: Throwable) : AsteroidsState()
    object Loading : AsteroidsState()
}