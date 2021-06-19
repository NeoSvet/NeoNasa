package ru.neosvet.neonasa.list

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ru.neosvet.neonasa.R
import ru.neosvet.neonasa.repository.room.AsteroidEntity

class AsteroidsAdapter : RecyclerView.Adapter<AsteroidsHolder>() {
    companion object {
        val TYPE_TITLE = 0
        val TYPE_ITEM = 1
    }

    private val list = ArrayList<AsteroidsObject>()

    fun addItem(title: String) {
        list.add(AsteroidsObject.Title(title))
    }

    fun addItem(entity: AsteroidEntity) {
        list.add(AsteroidsObject.Item(entity))
    }

    override fun getItemViewType(position: Int) = when (list[position]) {
        is AsteroidsObject.Title -> TYPE_TITLE
        is AsteroidsObject.Item -> TYPE_ITEM
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AsteroidsHolder {
        return when (viewType) {
            TYPE_ITEM -> AsteroidsHolders.Item(
                LayoutInflater.from(parent.context).inflate(R.layout.asteroid_item, parent, false)
            )
            else -> AsteroidsHolders.Title(
                LayoutInflater.from(parent.context).inflate(R.layout.title_item, parent, false)
            )
        }
    }

    override fun onBindViewHolder(holder: AsteroidsHolder, position: Int) {
        holder.bind(list[position])
    }

    override fun getItemCount() = list.size

    fun clear() {
        list.clear()
    }
}