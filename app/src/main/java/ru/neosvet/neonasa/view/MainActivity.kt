package ru.neosvet.neonasa.view

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.android.material.bottomappbar.BottomAppBar
import com.google.android.material.floatingactionbutton.FloatingActionButton
import ru.neosvet.neonasa.R
import ru.neosvet.neonasa.utils.SettingsUtils
import ru.neosvet.neonasa.utils.Theme

class MainActivity : AppCompatActivity() {
    companion object {
        val OPEN_SETTINGS = "open_settings"
    }

    private lateinit var fabSearch: FloatingActionButton
    private lateinit var bottom_app_bar: BottomAppBar
    private var isSearch = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val settings = SettingsUtils(this)
        when (settings.getTheme()) {
            Theme.STAR_SKY -> setTheme(R.style.Theme_StarSky)
            Theme.SNOWFLAKES -> setTheme(R.style.Theme_Snowflakes)
        }
        setContentView(R.layout.activity_main)

        openDayPhoto()
        setBottomAppBar()

        if (intent.getBooleanExtra(OPEN_SETTINGS, false))
            openSettings()
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
                R.id.app_bar_home -> {
                    setFabSearch()
                    while (supportFragmentManager.popBackStackImmediate()) {
                        //Log.d("mylog", "pop back")
                    }
                }
                R.id.app_bar_settings ->
                    openSettings()
            }
            return@setOnMenuItemClickListener true
        }

        fabSearch.setOnClickListener {
            if (isSearch) {
                supportFragmentManager.beginTransaction()
                    .add(R.id.container, WikiFragment())
                    .addToBackStack("")
                    .commit()
                setFabClose()
            } else {
                supportFragmentManager.popBackStack()
                setFabSearch()
            }
        }
    }

    private fun setFabClose() {
        fabSearch.setImageDrawable(
            ContextCompat.getDrawable(
                this,
                R.drawable.ic_baseline_close_24
            )
        )
        isSearch = false
    }

    private fun setFabSearch() {
        fabSearch.setImageDrawable(
            ContextCompat.getDrawable(
                this,
                R.drawable.ic_baseline_search_24
            )
        )
        isSearch = true
    }

    private fun openDayPhoto() {
        supportFragmentManager.beginTransaction()
            .replace(R.id.container, DayPhotoFragment())
            .commit()
    }

    private fun openSettings() {
        supportFragmentManager.beginTransaction()
            .replace(R.id.container, SettingsFragment())
            .addToBackStack("")
            .commit()
    }

    fun hideBar() {
        bottom_app_bar.performHide()
    }

    fun showBar() {
        bottom_app_bar.performShow()
    }

    override fun onBackPressed() {
        if (!isSearch) {
            fabSearch.setImageDrawable(
                ContextCompat.getDrawable(
                    this,
                    R.drawable.ic_baseline_search_24
                )
            )
            isSearch = true
        }
        super.onBackPressed()
    }
}