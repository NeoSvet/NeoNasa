package ru.neosvet.neonasa.view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import ru.neosvet.neonasa.R

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        supportFragmentManager.beginTransaction()
            .replace(R.id.container, DayPhotoFragment())
            .commit()
    }
}