package ru.neosvet.neonasa.list

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.MotionEvent
import android.view.View
import android.widget.ProgressBar
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.view.MotionEventCompat
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.textview.MaterialTextView
import ru.neosvet.neonasa.R
import ru.neosvet.neonasa.repository.room.AsteroidEntity

sealed class AsteroidsObject {
    data class Title(val title: String) : AsteroidsObject()
    data class Item(val entity: AsteroidEntity) : AsteroidsObject()
}

abstract class AsteroidsHolder(itemView: View, val callbacks: ListCallbacks) :
    RecyclerView.ViewHolder(itemView) {
    abstract fun bind(dataItem: AsteroidsObject)
}

sealed class AsteroidsHolders {
    class Title(itemView: View, callbacks: ListCallbacks) : AsteroidsHolder(itemView, callbacks) {
        private val tvTitle = itemView.findViewById(R.id.tvTitle) as MaterialTextView

        override fun bind(dataItem: AsteroidsObject) {
            val data = dataItem as AsteroidsObject.Title
            tvTitle.text = data.title
        }
    }

    class Item(itemView: View, callbacks: ListCallbacks) : AsteroidsHolder(itemView, callbacks),
        ItemTouchHelperViewHolder {
        private val tvName = itemView.findViewById(R.id.tvName) as MaterialTextView
        private val tvLink = itemView.findViewById(R.id.tvLink) as MaterialTextView
        private val tvDiameter = itemView.findViewById(R.id.tvDiameter) as MaterialTextView
        private val tvDistance = itemView.findViewById(R.id.tvDistance) as MaterialTextView
        private val barPriority = itemView.findViewById(R.id.barPriority) as ProgressBar
        private val ivDragHandle = itemView.findViewById(R.id.ivDragHandle) as AppCompatImageView
        private lateinit var context: Context
        private var startPosition = -1

        override fun bind(dataItem: AsteroidsObject) {
            val data = (dataItem as AsteroidsObject.Item).entity
            context = itemView.context

            tvName.text = data.name
            barPriority.progress = data.priority
            tvDiameter.text = String.format(
                context.getString(R.string.diameter),
                data.diameter_min, data.diameter_max
            )
            tvDistance.text = String.format(
                context.getString(R.string.distance),
                data.distance
            )

            tvLink.setOnClickListener {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(data.link))
                it.context.startActivity(intent)
            }

            itemView.setOnClickListener { callbacks.onItemClick(adapterPosition) }
            ivDragHandle.setOnTouchListener { _, event ->
                if (MotionEventCompat.getActionMasked(event) == MotionEvent.ACTION_DOWN) {
                    callbacks.onStartDrag(this)
                }
                false
            }

        }

        override fun onItemSelected() {
            itemView.background = context.getDrawable(R.drawable.secondary_field)
            startPosition = adapterPosition
        }

        override fun onItemClear() {
            itemView.background = context.getDrawable(R.drawable.primary_field)
            if (adapterPosition == -1)
                return //item is removed
            if (startPosition != adapterPosition)
                callbacks.onItemMoved(adapterPosition)
        }
    }
}