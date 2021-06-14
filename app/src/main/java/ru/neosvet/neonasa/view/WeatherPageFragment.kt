package ru.neosvet.neonasa.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import ru.neosvet.neonasa.R
import ru.neosvet.neonasa.list.PairAdapter
import ru.neosvet.neonasa.model.TypeWeather
import ru.neosvet.neonasa.model.WeatherModel
import ru.neosvet.neonasa.repository.WeatherState

class WeatherPageFragment : Fragment(), Observer<WeatherState> {
    companion object {
        private val ARG_TYPE = "type"

        @JvmStatic
        fun newInstance(type: TypeWeather) =
            WeatherPageFragment().apply {
                arguments = Bundle().apply {
                    putInt(ARG_TYPE, type.index)
                }
            }
    }

    private val model: WeatherModel by lazy {
        ViewModelProvider(this).get(WeatherModel::class.java)
    }
    private val weatherAdapter = PairAdapter()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_weather_page, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initList(view)
        arguments?.getInt(ARG_TYPE)?.let {
            requestWeather(it)
        }
    }

    private fun initList(root: View) {
        val rvWeather = root.findViewById(R.id.rvWeather) as RecyclerView
        rvWeather.layoutManager = LinearLayoutManager(requireContext())
        rvWeather.adapter = weatherAdapter
    }

    private fun requestWeather(index: Int) {
        when (index) {
            TypeWeather.GST.index ->
                model.requestWeather(TypeWeather.GST)
            TypeWeather.IPS.index ->
                model.requestWeather(TypeWeather.IPS)
            TypeWeather.FLR.index ->
                model.requestWeather(TypeWeather.FLR)
            TypeWeather.SEP.index ->
                model.requestWeather(TypeWeather.SEP)
            TypeWeather.MPC.index ->
                model.requestWeather(TypeWeather.MPC)
            TypeWeather.RBE.index ->
                model.requestWeather(TypeWeather.RBE)
            TypeWeather.HSS.index ->
                model.requestWeather(TypeWeather.HSS)
            TypeWeather.WSA.index ->
                model.requestWeather(TypeWeather.WSA)
            else ->
                model.requestWeather(TypeWeather.CME)
        }
    }

    override fun onResume() {
        super.onResume()
        model.getState().observe(this, this)
    }

    override fun onPause() {
        super.onPause()
        model.getState().removeObserver(this)
    }

    override fun onChanged(state: WeatherState?) {
        weatherAdapter.clear()
        when (state) {
            is WeatherState.Success -> {
                state.list.forEach {
                    weatherAdapter.addItem(it.first, it.second)
                }
            }
            is WeatherState.Loading -> {
                weatherAdapter.addItem(
                    getString(R.string.loading), ""
                )
            }
            is WeatherState.Error -> {
                state.error.message?.let {
                    weatherAdapter.addItem("error", it)
                }
            }
        }
        weatherAdapter.notifyDataSetChanged()
    }

}