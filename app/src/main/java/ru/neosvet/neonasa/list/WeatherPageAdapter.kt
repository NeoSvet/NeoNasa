package ru.neosvet.neonasa.list

import android.content.Context
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import ru.neosvet.neonasa.R
import ru.neosvet.neonasa.model.TypeWeather
import ru.neosvet.neonasa.view.WeatherPageFragment

class WeatherPageAdapter(
    private val fragmentManager: FragmentManager,
    private val context: Context
) : FragmentStatePagerAdapter(fragmentManager) {

    private val types = arrayOf(
        TypeWeather.CME, TypeWeather.GST, TypeWeather.IPS,
        TypeWeather.FLR, TypeWeather.SEP, TypeWeather.MPC,
        TypeWeather.RBE, TypeWeather.HSS, TypeWeather.WSA
    )

    override fun getItem(position: Int): Fragment {
        return WeatherPageFragment.newInstance(types[position])
    }

    override fun getCount(): Int {
        return types.size
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return context.resources.getStringArray(R.array.weather_types)[position]
    }
}
