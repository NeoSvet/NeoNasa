package ru.neosvet.neonasa.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import ru.neosvet.neonasa.R
import ru.neosvet.neonasa.list.*
import ru.neosvet.neonasa.model.AsteroidsModel
import ru.neosvet.neonasa.repository.AsteroidsRepository
import ru.neosvet.neonasa.repository.AsteroidsState
import ru.neosvet.neonasa.repository.room.AsteroidEntity

class AsteroidsFragment : Fragment(), Observer<AsteroidsState> {
    private val model: AsteroidsModel by lazy {
        ViewModelProvider(this).get(AsteroidsModel::class.java)
    }
    private val mainAct: MainActivity by lazy {
        activity as MainActivity
    }
    private lateinit var itemTouchHelper: ItemTouchHelper
    private lateinit var dataAdapter: AsteroidsAdapter
    private lateinit var rvAsteroids: RecyclerView
    private var snackbar: Snackbar? = null

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

    override fun onDestroyView() {
        snackbar?.dismiss()
        dataAdapter.deleteFromBase()
        super.onDestroyView()
    }

    private fun initList(root: View) {
        dataAdapter = AsteroidsAdapter(object : ListCallbacks {
            override fun onItemClick(position: Int) {
            }

            override fun onItemTouched(position: Int) {
                dataAdapter.switchSwipe(position)
            }

            override fun onStartDrag(viewHolder: RecyclerView.ViewHolder) {
                itemTouchHelper.startDrag(viewHolder)
            }

            override fun onItemMoved(position: Int) {
                val item = dataAdapter.getItem(position) as AsteroidsObject.Item
                updatePositionsInGroup(item.entity.updated)
            }

            override fun notifDeleteItem(position: Int) {
                snackbar?.dismiss()
                snackbar = Snackbar.make(
                    rvAsteroids,
                    R.string.item_deleted,
                    AsteroidsAdapter.TIME_TO_DELETE.toInt()
                )
                snackbar?.setAction(R.string.restore, View.OnClickListener {
                    val pos = dataAdapter.restoreDeletedItem()
                    if (pos > -1)
                        rvAsteroids.scrollToPosition(pos)
                    snackbar?.dismiss()
                })
                snackbar?.show()
            }
        },
            object : AsteroidEditor {
                override fun deleteItem(item: AsteroidEntity) {
                    model.removeAsterod(item)
                }

                override fun startEdit(position: Int) {
                    dataAdapter.editItem(position)
                }

                override fun cancelEdit(position: Int) {
                    dataAdapter.editItem(position)
                }

                override fun saveEdit(position: Int, asteroid: AsteroidEntity) {
                    model.update(asteroid)
                    dataAdapter.updateItem(position, asteroid)
                }
            })

        rvAsteroids = root.findViewById(R.id.rvAsteroids) as RecyclerView
        rvAsteroids.layoutManager = LinearLayoutManager(requireContext())
        rvAsteroids.adapter = dataAdapter

        itemTouchHelper = ItemTouchHelper(ItemTouchHelperCallback(dataAdapter))
        itemTouchHelper.attachToRecyclerView(rvAsteroids)
    }

    private fun updatePositionsInGroup(date: Long) {
        Thread({
            var i = 0
            var started = false
            val repository = AsteroidsRepository()
            dataAdapter.getItems().forEach {
                if (it is AsteroidsObject.Item) {
                    if (it.entity.updated == date) {
                        started = true
                        repository.updatePosition(it.entity.name, i)
                        i++
                    } else if (started)
                        return@forEach
                }
            }
        }).start()
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
                    dataAdapter.addItem("Error: " + it)
                }
                dataAdapter.notifyDataSetChanged()
            }
        }
    }

    private fun fillList() {
        dataAdapter.clear()
        val repository = AsteroidsRepository()
        repository.getGroups()?.forEach {
            dataAdapter.addItem(it.title)
            repository.getGroupList(it.date)?.forEach {
                dataAdapter.addItem(it)
            }
            return@forEach
        }
        dataAdapter.notifyDataSetChanged()
    }
}