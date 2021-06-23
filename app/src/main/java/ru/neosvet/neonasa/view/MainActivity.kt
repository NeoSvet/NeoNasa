package ru.neosvet.neonasa.view

import android.animation.ObjectAnimator
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.RotateAnimation
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.transition.*
import com.google.android.material.bottomappbar.BottomAppBar
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.imageview.ShapeableImageView
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
    private lateinit var barStatus: LinearLayout
    private lateinit var ivStatus: ShapeableImageView
    private var isSearch = true
    private var mainBarIsHide = false
    private val rotateAnim: RotateAnimation by lazy {
        RotateAnimation(
            0f, 360f, Animation.RELATIVE_TO_SELF,
            0.5f, Animation.RELATIVE_TO_SELF, 0.5f
        ).apply {
            repeatCount = Animation.INFINITE
            duration = 1500
        }
    }
    private val animShowMainBar: ObjectAnimator by lazy {
        ObjectAnimator.ofFloat(barMain, "translationY", 0f)
            .apply {
                duration = 500
            }
    }
    private val animHideMainBar: ObjectAnimator by lazy {
        val y = 56f * resources.displayMetrics.density
        ObjectAnimator.ofFloat(barMain, "translationY", y)
            .apply {
                duration = 500
            }
    }

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
        setStatusBar()

        if (savedInstanceState == null) {
            openDayPhoto()
            if (intent.getBooleanExtra(OPEN_SETTINGS, false)) {
                openSettings()
                barMain.selectedItemId = R.id.bottom_view_settings
            }
        }
    }

    private fun setStatusBar() {
        barStatus = findViewById(R.id.barStatus)
        ivStatus = findViewById(R.id.ivStatus)
        barStatus.setOnClickListener {
            finishLoad(false)
        }
    }

    override fun onBackPressed() {
        if (!isSearch) {
            fabSearch.setImageDrawable(
                ContextCompat.getDrawable(
                    this,
                    R.drawable.ic_search
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
                    backToPhoto()
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

        fabSearch.post({
            fabSearch.visibility = View.GONE
            barPhoto.visibility = View.GONE
        })
    }

    private fun convertIdToType(id: Int) = when (id) {
        R.id.app_bar_earth -> TypePhoto.EARTH
        R.id.app_bar_mars -> TypePhoto.MARS
        else -> TypePhoto.DAY
    }

    private fun backToPhoto() {
        hideMainBar()
        barPhoto.visibility = View.VISIBLE
        fabSearch.visibility = View.VISIBLE
        setFabSearch()
        openDayPhoto()
    }

    private fun openDayPhoto() {
        supportFragmentManager.beginTransaction()
            .replace(
                R.id.container,
                DayPhotoFragment()
            ).commit()
    }

    fun openPhoto(type: TypePhoto) {
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
                R.drawable.ic_close
            )
        )
        isSearch = false
    }

    private fun setFabSearch() {
        fabSearch.setImageDrawable(
            ContextCompat.getDrawable(
                this,
                R.drawable.ic_search
            )
        )
        isSearch = true
    }

    private fun openSettings() {
        supportFragmentManager.beginTransaction()
            .replace(R.id.container, SettingsFragment())
            .commit()
    }

    fun startBottomAnim(target: ViewGroup, dimenId: Int) {
        TransitionManager.beginDelayedTransition(target)
        val params = target.getLayoutParams() as ViewGroup.MarginLayoutParams
        params.bottomMargin = resources.getDimension(dimenId).toInt()
        target.setLayoutParams(params)
    }

    fun startLoad() {
        hideMainBar()
        barPhoto.visibility = View.GONE
        fabSearch.visibility = View.GONE

        ivStatus.startAnimation(rotateAnim)
        startBottomAnim(barStatus, R.dimen.status_show)
    }

    fun finishLoad(isPhoto: Boolean) {
        ivStatus.clearAnimation()
        startBottomAnim(barStatus, R.dimen.status_hide)

        if (isPhoto)
            showPhotoBar()
        else
            showMainBar()
    }

    private fun showMainBar() {
        barPhoto.visibility = View.GONE
        fabSearch.visibility = View.GONE
        if (!mainBarIsHide)
            return
        mainBarIsHide = false
        barMain.clearAnimation()
        animShowMainBar.start()
    }

    fun hideMainBar() {
        if (mainBarIsHide)
            return
        mainBarIsHide = true
        barMain.clearAnimation()
        animHideMainBar.start()
    }

    fun hideBottomBars() {
        hideMainBar()
        barPhoto.performHide()
    }

    fun hideBottomBarsWithFab() {
        hideBottomBars()
        barPhoto.performHide()
        fabSearch.visibility = View.GONE
    }

    fun showPhotoBar() {
        barPhoto.visibility = View.VISIBLE
        fabSearch.visibility = View.VISIBLE
        barPhoto.performShow()
    }

    fun showToast(string: String?) {
        Toast.makeText(this, string, Toast.LENGTH_SHORT).apply {
            //setGravity(Gravity.BOTTOM, 0, 250)
            show()
        }
    }
}