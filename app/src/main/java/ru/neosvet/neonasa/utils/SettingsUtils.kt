package ru.neosvet.neonasa.utils

import android.content.Context
import android.content.SharedPreferences

class SettingsUtils(val context: Context) {
    private val NAME = "settings"
    private val THEME = "theme"
    private val pref: SharedPreferences by lazy {
        context.getSharedPreferences(NAME, Context.MODE_PRIVATE)
    }
    private val editor: SharedPreferences.Editor by lazy {
        pref.edit()
    }

    fun getTheme(): Theme = when (pref.getInt(THEME, 0)) {
        1 -> Theme.SNOWFLAKES
        else -> Theme.STAR_SKY
    }

    fun setTheme(value: Theme) {
        editor.putInt(THEME, value.index)
        editor.apply()
    }
}

enum class Theme(val index: Int) {
    STAR_SKY(0),
    SNOWFLAKES(1)
}