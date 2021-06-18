package ru.neosvet.neonasa.app

import android.app.Application
import androidx.room.Room
import ru.neosvet.neonasa.repository.room.AsteroidsDataBase

class App : Application() {
    override fun onCreate() {
        super.onCreate()
        appInstance = this
    }

    companion object {
        private var appInstance: App? = null
        private var dbAsteroids: AsteroidsDataBase? = null
        private const val DB_ASTEROIDS = "Asteroids.db"

        fun getBase(): AsteroidsDataBase {
            if (dbAsteroids == null) {
                synchronized(AsteroidsDataBase::class.java) {
                    if (dbAsteroids == null) {
                        if (appInstance == null) throw IllegalStateException("Application is null while creating DataBase")
                        dbAsteroids = Room.databaseBuilder(
                            appInstance!!.applicationContext,
                            AsteroidsDataBase::class.java,
                            DB_ASTEROIDS
                        )
                            .allowMainThreadQueries()
                            .build()
                    }
                }
            }

            return dbAsteroids!!
        }
    }
}