package ru.neosvet.neonasa.list

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ru.neosvet.neonasa.R
import ru.neosvet.neonasa.repository.room.AsteroidEntity

class AsteroidsAdapter(
    private val callbacks: ListCallbacks
) : RecyclerView.Adapter<AsteroidsHolder>(), ItemTouchHelperAdapter {
    companion object {
        val TYPE_TITLE = 0
        val TYPE_ITEM = 1
    }

    private val data = ArrayList<AsteroidsObject>()

    fun addItem(title: String) {
        data.add(AsteroidsObject.Title(title))
    }

    fun addItem(entity: AsteroidEntity) {
        data.add(AsteroidsObject.Item(entity))
    }

    fun getItem(position: Int) = data[position]

    fun getItems() = data

    override fun getItemViewType(position: Int) = when (data[position]) {
        is AsteroidsObject.Title -> TYPE_TITLE
        is AsteroidsObject.Item -> TYPE_ITEM
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AsteroidsHolder {
        return when (viewType) {
            TYPE_ITEM -> AsteroidsHolders.Item(
                LayoutInflater.from(parent.context).inflate(R.layout.asteroid_item, parent, false),
                callbacks
            )
            else -> AsteroidsHolders.Title(
                LayoutInflater.from(parent.context).inflate(R.layout.title_item, parent, false),
                callbacks
            )
        }
    }

    override fun onBindViewHolder(holder: AsteroidsHolder, position: Int) {
        holder.bind(data[position])
    }

    override fun getItemCount() = data.size

    fun clear() {
        data.clear()
    }

    override fun onItemMove(fromPosition: Int, toPosition: Int) {
        val index = findTitleIndex(fromPosition, toPosition)
        if (index != -1)
            return
        val newPosition = if (toPosition > fromPosition)
            toPosition - 1
        else
            toPosition
        data.removeAt(fromPosition).let {
            data.add(newPosition, it)
        }
        notifyItemMoved(fromPosition, newPosition)
    }

    private fun findTitleIndex(fromPosition: Int, toPosition: Int): Int {
        val step = if (fromPosition < toPosition) 1 else -1
        var index = fromPosition
        while (index != toPosition) {
            index += step
            if (data[index] is AsteroidsObject.Title)
                return index
        }
        return -1
    }

    override fun onItemDismiss(position: Int) {
        callbacks.onItemDismissed(position)
        data.removeAt(position)
        notifyItemRemoved(position)
    }
}