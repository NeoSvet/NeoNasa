package ru.neosvet.neonasa.repository.room

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class GroupEntity(
    @PrimaryKey
    val date: Long = 0,
    val title: String = ""
)

@Entity
data class AsteroidEntity(
    @PrimaryKey
    val name: String = "",
    val updated: Long = 0,
    val diameter_min: Float? = null,
    val diameter_max: Float? = null,
    val distance: Int? = null,
    val link: String = "",
    var position: Int = 0,
    var note: String? = null,
    var priority: Int = 0,
    var marked: Boolean = false
)