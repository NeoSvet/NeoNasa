package ru.neosvet.neonasa.list

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.util.TypedValue
import androidx.annotation.AttrRes
import androidx.annotation.ColorInt
import androidx.core.graphics.blue
import androidx.core.graphics.green
import androidx.core.graphics.red
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import ru.neosvet.neonasa.R
import ru.neosvet.neonasa.repository.room.AsteroidEntity
import kotlin.math.abs

interface ListCallbacks {
    fun onItemClick(position: Int)
    fun onItemTouched(position: Int)
    fun onStartDrag(viewHolder: RecyclerView.ViewHolder)
    fun onItemMoved(position: Int)
    fun onItemDismissed(position: Int)
}

interface AsteroidEditor {
    fun startEdit(position: Int)
    fun cancelEdit(position: Int)
    fun saveEdit(position: Int, asteroid: AsteroidEntity)
}

interface ItemTouchHelperAdapter {
    fun onItemMove(fromPosition: Int, toPosition: Int)
    fun onItemDismiss(position: Int)
}

interface ItemTouchHelperViewHolder {
    fun onItemSelected()
    fun onItemClear()
}

class ItemTouchHelperCallback(private val adapter: AsteroidsAdapter) :
    ItemTouchHelper.Callback() {

    override fun isLongPressDragEnabled(): Boolean {
        return false
    }

    override fun isItemViewSwipeEnabled(): Boolean {
        return adapter.isSwipable
    }

    override fun getMovementFlags(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder
    ): Int {
        val dragFlags = ItemTouchHelper.UP or ItemTouchHelper.DOWN
        val swipeFlags = ItemTouchHelper.START or ItemTouchHelper.END
        return makeMovementFlags(
            dragFlags,
            swipeFlags
        )
    }

    override fun onMove(
        recyclerView: RecyclerView,
        source: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder
    ): Boolean {
        adapter.onItemMove(source.adapterPosition, target.adapterPosition)
        return true
    }

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, i: Int) {
        adapter.onItemDismiss(viewHolder.adapterPosition)
    }

    override fun onSelectedChanged(viewHolder: RecyclerView.ViewHolder?, actionState: Int) {
        if (actionState != ItemTouchHelper.ACTION_STATE_IDLE && viewHolder is ItemTouchHelperViewHolder) {
            val itemViewHolder = viewHolder as ItemTouchHelperViewHolder
            itemViewHolder.onItemSelected()
        }
        super.onSelectedChanged(viewHolder, actionState)
    }

    override fun clearView(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder) {
        super.clearView(recyclerView, viewHolder)
        if (viewHolder is ItemTouchHelperViewHolder) {
            val itemViewHolder = viewHolder as ItemTouchHelperViewHolder
            itemViewHolder.onItemClear()
        }
    }

    override fun onChildDraw(
        c: Canvas,
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        dX: Float,
        dY: Float,
        actionState: Int,
        isCurrentlyActive: Boolean
    ) {
        if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {
            val width = viewHolder.itemView.width.toFloat()
            val context = viewHolder.itemView.context
            if (abs(dX) > 0) {
                val primary = context.getColorFromAttr(R.attr.colorPrimary)
                val m = (255 * (abs(dX) / width)).toInt()
                val red = if (primary.red + m > 255) 255 else m + primary.red
                val green = if (primary.green - m < 0) 0 else primary.green - m
                val blue = if (primary.blue - m < 0) 0 else primary.blue - m
                viewHolder.itemView.setBackgroundColor(Color.argb(255, red, green, blue))
            } else {
                viewHolder.itemView.background = context.getDrawable(R.drawable.primary_field)
            }
            viewHolder.itemView.translationX = dX
        } else {
            super.onChildDraw(
                c, recyclerView, viewHolder, dX, dY,
                actionState, isCurrentlyActive
            )
        }
    }

    @ColorInt
    fun Context.getColorFromAttr(
        @AttrRes attrColor: Int,
        typedValue: TypedValue = TypedValue(),
        resolveRefs: Boolean = true
    ): Int {
        theme.resolveAttribute(attrColor, typedValue, resolveRefs)
        return typedValue.data
    }
}
