package ru.neosvet.neonasa.view

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.android.material.bottomappbar.BottomAppBar
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import ru.neosvet.neonasa.R
import ru.neosvet.neonasa.utils.SettingsUtils
import ru.neosvet.neonasa.utils.Theme


class MainActivity : AppCompatActivity() {
    companion object {
        val OPEN_SETTINGS = "open_settings"
    }

    private lateinit var fabSearch: FloatingActionButton
    private lateinit var barPhoto: BottomAppBar
    private lateinit var barMain: BottomNavigationView
    private var isSearch = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val settings = SettingsUtils(this)
        when (settings.getTheme()) {
            Theme.STAR_SKY -> setTheme(R.style.Theme_StarSky)
            Theme.SNOWFLAKES -> setTheme(R.style.Theme_Snowflakes)
        }
        setContentView(R.layout.activity_main)

        setMainBar()
        setPhotoBar()

        if (savedInstanceState == null) {
            openDayPhoto()
            if (intent.getBooleanExtra(OPEN_SETTINGS, false)) {
                openSettings()
                barMain.selectedItemId = R.id.bottom_view_settings
            }
        }
    }

    override fun onBackPressed() {
        if (!isSearch) {
            fabSearch.setImageDrawable(
                ContextCompat.getDrawable(
                    this,
                    R.drawable.ic_search_24
                )
            )
            isSearch = true
        }
        super.onBackPressed()
    }

    private fun setMainBar() {
        barMain = findViewById(R.id.bottom_navigation_view)
        barMain.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.bottom_view_photo -> {
                    returnToPhoto()
                }
                R.id.bottom_view_asteroids -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.container, AsteroidsFragment())
                        .commit()
                }
                R.id.bottom_view_weather -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.container, WeatherFragment())
                        .commit()
                }
                R.id.bottom_view_settings -> {
                    openSettings()
                }
            }
            return@setOnNavigationItemSelectedListener true
        }
    }

    private fun setPhotoBar() {
        fabSearch = findViewById(R.id.fabSearch)
        barPhoto = findViewById(R.id.bottom_app_bar)

        barPhoto.replaceMenu(R.menu.menu_bottom_bar)

        barPhoto.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.app_bar_home ->
                    showMainBar()
                else ->
                    openPhoto(convertIdToType(it.itemId))
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

    private fun convertIdToType(id: Int) = when (id) {
        R.id.app_bar_earth -> TypePhoto.EARTH
        R.id.app_bar_mars -> TypePhoto.MARS
        else -> TypePhoto.DAY
    }

    private fun returnToPhoto() {
        barMain.visibility = View.GONE
        fabSearch.visibility = View.VISIBLE
        setFabSearch()
        openDayPhoto()
    }

    private fun openDayPhoto() {
        supportFragmentManager.beginTransaction()
            .replace(
                R.id.container,
                PhotoFragment.newInstance(TypePhoto.DAY)
            ).commit()
    }

    private fun openPhoto(type: TypePhoto) {
        setFabSearch()

        supportFragmentManager.beginTransaction()
            .replace(
                R.id.container,
                PhotoFragment.newInstance(type)
            ).commit()
    }

    private fun setFabClose() {
        fabSearch.setImageDrawable(
            ContextCompat.getDrawable(
                this,
                R.drawable.ic_close_24
            )
        )
        isSearch = false
    }

    private fun setFabSearch() {
        fabSearch.setImageDrawable(
            ContextCompat.getDrawable(
                this,
                R.drawable.ic_search_24
            )
        )
        isSearch = true
    }

    private fun openSettings() {
        supportFragmentManager.beginTransaction()
            .replace(R.id.container, SettingsFragment())
            .commit()
    }

    private fun showMainBar() {
        barMain.visibility = View.VISIBLE
        fabSearch.visibility = View.GONE
    }

    fun hideMainBar() {
        barMain.visibility = View.GONE
        barPhoto.visibility = View.VISIBLE
        fabSearch.visibility = View.VISIBLE
    }

    fun hideBottomBars() {
        hideMainBar()
        barPhoto.performHide()
    }

    fun showPhotoBar() {
        barPhoto.performShow()
    }

    fun showToast(string: String?) {
        Toast.makeText(this, string, Toast.LENGTH_SHORT).apply {
            //setGravity(Gravity.BOTTOM, 0, 250)
            show()
        }
    }
}