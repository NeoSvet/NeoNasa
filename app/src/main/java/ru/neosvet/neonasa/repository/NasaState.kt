package ru.neosvet.neonasa.repository

sealed class DayPhotoState {
    data class SuccessPhoto(val response: DayPhotoResponse) : DayPhotoState()
    data class SuccessVideo(val response: DayPhotoResponse) : DayPhotoState()
    data class Error(val error: Throwable) : DayPhotoState()
    data class Loading(val progress: Int?) : DayPhotoState()
}