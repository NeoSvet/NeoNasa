package ru.neosvet.neonasa.list

import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.textview.MaterialTextView
import ru.neosvet.neonasa.R

class PairAdapter : RecyclerView.Adapter<PairAdapter.Holder>() {
    private val titles = ArrayList<String>()
    private val values = ArrayList<String>()

    fun addItem(title: String, value: String) {
        titles.add(title)
        values.add(value)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        return Holder(
            LayoutInflater.from(parent.context).inflate(R.layout.pair_item, parent, false)
        )
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder.setItem(titles[position], values[position])
    }

    override fun getItemCount() = titles.size

    fun clear() {
        titles.clear()
        values.clear()
    }

//HOLDER:

    inner class Holder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvTitle: MaterialTextView = itemView.findViewById(R.id.tvTitle)
        private val tvValue: MaterialTextView = itemView.findViewById(R.id.tvValue)

        fun setItem(title: String, value: String) {
            tvTitle.text = title
            tvValue.text = value
            if (title.contains("link")) {
                itemView.setOnClickListener {
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(value))
                    it.context.startActivity(intent)
                }
            }
        }
    }
}