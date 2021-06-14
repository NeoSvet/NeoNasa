package ru.neosvet.neonasa.view

import android.os.Bundle
import android.util.Log
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
import ru.neosvet.neonasa.model.AsteroidsModel
import ru.neosvet.neonasa.repository.AsteroidsData
import ru.neosvet.neonasa.repository.AsteroidsState

class AsteroidsFragment : Fragment(), Observer<AsteroidsState> {
    private val model: AsteroidsModel by lazy {
        ViewModelProvider(this).get(AsteroidsModel::class.java)
    }
    private val dataAdapter = PairAdapter()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_asteroids, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initList(view)
        model.requestAsteroids()
    }

    private fun initList(root: View) {
        val rvAsteroids = root.findViewById(R.id.rvAsteroids) as RecyclerView
        rvAsteroids.layoutManager = LinearLayoutManager(requireContext())
        rvAsteroids.adapter = dataAdapter
    }

    override fun onResume() {
        super.onResume()
        model.getState().observe(this, this)
    }

    override fun onPause() {
        super.onPause()
        model.getState().removeObserver(this)
    }

    override fun onChanged(state: AsteroidsState?) {
        dataAdapter.clear()
        when (state) {
            is AsteroidsState.Success -> {
                responseToAdapter(state.response)
            }
            is AsteroidsState.Loading -> {
                dataAdapter.addItem(
                    getString(R.string.loading), ""
                )
            }
            is AsteroidsState.Error -> {
                state.error.message?.let {
                    dataAdapter.addItem("error", it)
                }
            }
        }
        dataAdapter.notifyDataSetChanged()
    }

    private fun responseToAdapter(response: AsteroidsData) {
        response.elementCount?.let {
            dataAdapter.addItem("asteroids count: ", it.toString())
        }
        val s = StringBuilder()
        for (day in response.days) {
            day.asteroids.forEach {
                s.append("asteroid: ")
                s.appendLine(it.name)
                //Log.d("mylog", "link " + it.nasaJplUrl)
                s.append("diameter (m): ")
                s.append(rounding(it.estimatedDiameter.meters.estimatedDiameterMin))
                s.append("-")
                s.appendLine(rounding(it.estimatedDiameter.meters.estimatedDiameterMax))
                it.closeApproachData.forEach {
                    s.append("distance to ${it.orbitingBody} (km):  ")
                    s.appendLine(rounding(it.missDistance.kilometers))
                }
                s.appendLine()
            }

            if (s.isNotEmpty()) {
                s.delete(s.length - 1, s.length)
                dataAdapter.addItem(day.date, s.toString())
                s.clear()
            } else {
                dataAdapter.addItem(day.date, "No asteroids")
            }
        }
    }

    private fun rounding(value: Double) = rounding(value.toString())

    private fun rounding(value: String): String {
        val i = value.indexOf(".")
        if (i > 0 && i + 4 < value.length)
            return value.substring(0, i + 4)
        else
            return value
    }
}