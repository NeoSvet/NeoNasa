package ru.neosvet.neonasa.repository

sealed class PhotoState {
    data class SuccessPhoto(val response: PhotoResponse) : PhotoState()
    data class SuccessVideo(val response: VideoResponse) : PhotoState()
    data class Error(val error: Throwable) : PhotoState()
    data class Loading(val progress: Int?) : PhotoState()
}