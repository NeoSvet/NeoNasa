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
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        itemSearch = menu.add(R.string.search)
        itemSearch.icon = ContextCompat.getDrawable(this, R.drawable.ic_baseline_search_24)
        itemSearch.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS)

        itemClose = menu.add(R.string.close)
        itemClose.icon = ContextCompat.getDrawable(this, R.drawable.ic_baseline_close_24)
        itemClose.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS)
        itemClose.isVisible = false

        return super.onCreateOptionsMenu(menu)
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