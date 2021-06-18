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
import ru.neosvet.neonasa.model.AsteroidsModel
import ru.neosvet.neonasa.repository.AsteroidsRepository
import ru.neosvet.neonasa.repository.AsteroidsState
import java.text.DecimalFormat

class AsteroidsFragment : Fragment(), Observer<AsteroidsState> {
    private val model: AsteroidsModel by lazy {
        ViewModelProvider(this).get(AsteroidsModel::class.java)
    }
    private val mainAct: MainActivity by lazy {
        activity as MainActivity
    }
    private val formatDiameter = DecimalFormat("#.0")
    private val formatDistance = DecimalFormat("#,###")
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
        when (state) {
            is AsteroidsState.Success -> {
                mainAct.finishLoad(false)
                fillList()
            }
            is AsteroidsState.Loading -> {
                mainAct.startLoad()
            }
            is AsteroidsState.Error -> {
                mainAct.finishLoad(false)
                dataAdapter.clear()
                state.error.message?.let {
                    dataAdapter.addItem("error", it)
                }
                dataAdapter.notifyDataSetChanged()
            }
        }
    }

    private fun fillList() {
        dataAdapter.clear()
        val repository = AsteroidsRepository()
        repository.getGroups()?.forEach {
            val s = StringBuilder()
            repository.getGroupList(it.date)?.forEach {
                s.append("asteroid: ")
                s.appendLine(it.name)
                s.append("diameter (m): ")
                s.append(formatDiameter.format(it.diameter_min))
                s.append("-")
                s.appendLine(formatDiameter.format(it.diameter_max))
                s.append("distance to Earth (km):  ")
                s.appendLine(formatDistance.format(it.distance))
                s.append("priority:  ")
                s.appendLine(it.priority)
                it.note?.let {
                    s.append("note:  ")
                    s.appendLine(it)
                }
                s.appendLine()
            }

            if (s.isNotEmpty()) {
                s.delete(s.length - 1, s.length)
                dataAdapter.addItem(it.title, s.toString())
                s.clear()
            } else {
                dataAdapter.addItem(it.title, "No asteroids")
            }
        }
        dataAdapter.notifyDataSetChanged()
    }
}