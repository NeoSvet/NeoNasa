package ru.neosvet.neonasa.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewpager.widget.ViewPager
import com.google.android.material.tabs.TabLayout
import ru.neosvet.neonasa.R
import ru.neosvet.neonasa.list.WeatherPageAdapter

class WeatherFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_weather, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initTab(view)
    }

    private fun initTab(root: View) {
        val view_pager = root.findViewById(R.id.view_pager) as ViewPager
        val tab_layout = root.findViewById(R.id.tab_layout) as TabLayout
        view_pager.adapter = WeatherPageAdapter(childFragmentManager, requireContext())
        tab_layout.setupWithViewPager(view_pager)
    }
}