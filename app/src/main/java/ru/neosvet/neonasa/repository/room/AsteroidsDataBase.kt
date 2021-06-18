package ru.neosvet.neonasa.repository.room

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = arrayOf(
        GroupEntity::class,
        AsteroidEntity::class
    ), version = 1, exportSchema = false
)
abstract class AsteroidsDataBase : RoomDatabase() {
    abstract fun groupDao(): GroupDao
    abstract fun asteroidDao(): AsteroidDao
}