package ru.neosvet.neonasa.utils

import android.view.View

//copied and adapted from https://stackoverflow.com/a/30775598/2956830

abstract class DoubleClickListener : View.OnClickListener {
    private val DOUBLE_CLICK_TIME_DELTA = 300 //milliseconds
    private var lastClickTime: Long = 0

    override fun onClick(v: View) {
        val clickTime = System.currentTimeMillis()
        if (clickTime - lastClickTime < DOUBLE_CLICK_TIME_DELTA) {
            onDoubleClick(v)
            lastClickTime = 0
        } else {
            onSingleClick(v)
            lastClickTime = clickTime
        }
    }

    abstract fun onSingleClick(v: View)
    abstract fun onDoubleClick(v: View)
}