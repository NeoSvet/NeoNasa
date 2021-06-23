package ru.neosvet.neonasa.list

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.MotionEvent
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatSeekBar
import androidx.core.view.MotionEventCompat
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textview.MaterialTextView
import ru.neosvet.neonasa.R
import ru.neosvet.neonasa.repository.room.AsteroidEntity

sealed class AsteroidsObject {
    data class Title(val title: String) : AsteroidsObject()
    data class Item(val entity: AsteroidEntity, var edit: Boolean) : AsteroidsObject()
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

            itemView.setOnTouchListener { _, event ->
                if (MotionEventCompat.getActionMasked(event) == MotionEvent.ACTION_DOWN) {
                    callbacks.onItemTouched(adapterPosition)
                }
                false
            }
        }
    }

    class Item(
        itemView: View,
        callbacks: ListCallbacks,
        val editor: AsteroidEditor
    ) : AsteroidsHolder(itemView, callbacks), ItemTouchHelperViewHolder {
        private lateinit var context: Context
        private lateinit var entity: AsteroidEntity
        private var startPosition = -1

        override fun bind(dataItem: AsteroidsObject) {
            val item = dataItem as AsteroidsObject.Item
            entity = item.entity
            context = itemView.context

            val tvName = itemView.findViewById(R.id.tvName) as MaterialTextView
            tvName.text = entity.name
            itemView.setOnTouchListener { _, event ->
                if (MotionEventCompat.getActionMasked(event) == MotionEvent.ACTION_DOWN) {
                    callbacks.onItemTouched(adapterPosition)
                }
                false
            }

            if (item.edit)
                setEditMode()
            else
                setNormalMode()
        }

        private fun setNormalMode() {
            val tvLink = itemView.findViewById(R.id.tvLink) as MaterialTextView
            val tvDiameter = itemView.findViewById(R.id.tvDiameter) as MaterialTextView
            val tvDistance = itemView.findViewById(R.id.tvDistance) as MaterialTextView
            val tvNote = itemView.findViewById(R.id.tvNote) as MaterialTextView
            val barPriority = itemView.findViewById(R.id.barPriority) as ProgressBar
            val ivDragHandle = itemView.findViewById(R.id.ivDragHandle) as AppCompatImageView
            val ivEdit = itemView.findViewById(R.id.ivEdit) as AppCompatImageView

            barPriority.progress = entity.priority
            tvDiameter.text = String.format(
                context.getString(R.string.diameter),
                entity.diameter_min, entity.diameter_max
            )
            tvDistance.text = String.format(
                context.getString(R.string.distance),
                entity.distance
            )
            if (entity.note.isNullOrEmpty())
                tvNote.visibility = View.GONE
            else {
                entity.note?.let {
                    tvNote.visibility = View.VISIBLE
                    if (it.length > 75) {
                        tvNote.text = it.substring(0, 75) +
                                context.getString(R.string.more)
                        tvNote.setOnClickListener {
                            tvNote.text = entity.note
                        }
                    } else
                        tvNote.text = entity.note
                }
            }

            tvLink.setOnClickListener {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(entity.link))
                it.context.startActivity(intent)
            }
            ivEdit.setOnClickListener {
                editor.startEdit(layoutPosition)
            }

            itemView.setOnClickListener { callbacks.onItemClick(adapterPosition) }
            ivDragHandle.setOnTouchListener { _, event ->
                if (MotionEventCompat.getActionMasked(event) == MotionEvent.ACTION_DOWN) {
                    callbacks.onStartDrag(this)
                }
                false
            }
        }

        private fun setEditMode() {
            val sbPriority = itemView.findViewById(R.id.sbPriority) as AppCompatSeekBar
            val etNote = itemView.findViewById(R.id.etNote) as TextInputEditText
            val ivDone = itemView.findViewById(R.id.ivDone) as AppCompatImageView
            val ivClose = itemView.findViewById(R.id.ivClose) as AppCompatImageView

            sbPriority.progress = entity.priority
            etNote.setText(entity.note, TextView.BufferType.NORMAL)
            ivDone.setOnClickListener {
                entity.priority = sbPriority.progress
                entity.note = etNote.text.toString()
                editor.saveEdit(adapterPosition, entity)
            }
            ivClose.setOnClickListener { editor.cancelEdit(adapterPosition) }
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