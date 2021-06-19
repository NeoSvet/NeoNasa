package ru.neosvet.neonasa.list

import android.content.Intent
import android.net.Uri
import android.view.View
import android.widget.ProgressBar
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.textview.MaterialTextView
import ru.neosvet.neonasa.R
import ru.neosvet.neonasa.repository.room.AsteroidEntity

sealed class AsteroidsObject {
    data class Title(val title: String) : AsteroidsObject()
    data class Item(val entity: AsteroidEntity) : AsteroidsObject()
}

abstract class AsteroidsHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    abstract fun bind(dataItem: AsteroidsObject)
}

sealed class AsteroidsHolders {
    class Title(itemView: View) : AsteroidsHolder(itemView) {
        private val tvTitle = itemView.findViewById(R.id.tvTitle) as MaterialTextView

        override fun bind(dataItem: AsteroidsObject) {
            val data = dataItem as AsteroidsObject.Title
            tvTitle.text = data.title
        }
    }

    class Item(itemView: View) : AsteroidsHolder(itemView) {
        private val tvName = itemView.findViewById(R.id.tvName) as MaterialTextView
        private val tvLink = itemView.findViewById(R.id.tvLink) as MaterialTextView
        private val tvDiameter = itemView.findViewById(R.id.tvDiameter) as MaterialTextView
        private val tvDistance = itemView.findViewById(R.id.tvDistance) as MaterialTextView
        private val barPriority = itemView.findViewById(R.id.barPriority) as ProgressBar

        override fun bind(dataItem: AsteroidsObject) {
            val data = (dataItem as AsteroidsObject.Item).entity
            data.priority
            tvName.text = data.name
            val context = itemView.context
            tvDiameter.text = String.format(
                context.getString(R.string.diameter),
                data.diameter_min, data.diameter_max
            )
            tvDistance.text = String.format(
                context.getString(R.string.distance),
                data.distance
            )
            barPriority.progress = data.priority

            tvLink.setOnClickListener {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(data.link))
                it.context.startActivity(intent)
            }

        }
    }
}