package ru.neosvet.neonasa.view

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import ru.neosvet.neonasa.R

class MainActivity : AppCompatActivity() {
    private lateinit var itemSearch: MenuItem
    private lateinit var itemClose: MenuItem

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        supportFragmentManager.beginTransaction()
            .replace(R.id.container, DayPhotoFragment())
            .commit()

        setBottomAppBar()
    }

    private fun setBottomAppBar() {
        fabSearch = findViewById(R.id.fabSearch)
        bottom_app_bar = findViewById(R.id.bottom_app_bar)

        bottom_app_bar.navigationIcon =
            ContextCompat.getDrawable(this, R.drawable.ic_menu_bottom_bar)
        bottom_app_bar.fabAlignmentMode = BottomAppBar.FAB_ALIGNMENT_MODE_CENTER
        bottom_app_bar.replaceMenu(R.menu.menu_bottom_bar)

        bottom_app_bar.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.app_bar_home ->
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.container, DayPhotoFragment())
                        .addToBackStack("")
                        .commit()
                R.id.app_bar_settings -> {
                }
            }
            return@setOnMenuItemClickListener true
        }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.equals(itemSearch)) {
            supportFragmentManager.beginTransaction()
                .add(R.id.container, WikiFragment())
                .addToBackStack("")
                .commit()
            itemSearch.isVisible = false
            itemClose.isVisible = true
        } else { // itemClose
            supportFragmentManager.popBackStack()
            itemSearch.isVisible = true
            itemClose.isVisible = false
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onBackPressed() {
        if (itemClose.isVisible) {
            itemSearch.isVisible = true
            itemClose.isVisible = false
        }
        super.onBackPressed()
    }
}