package com.krayapp.buffercompanion.bargen.utils

import android.view.View
import android.view.View.OnScrollChangeListener
import androidx.recyclerview.widget.RecyclerView

class BasicScrollWatcher(
    absoluteAccuracy: Boolean = false,
    private val onScrolled: (ScrollDirection) -> Unit
) : RecyclerView.OnScrollListener(), OnScrollChangeListener {
    private val upAccuracy = if (absoluteAccuracy) 0 else -10
    private val downAccuracy = if (absoluteAccuracy) 0 else 10
    override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
        super.onScrolled(recyclerView, dx, dy)

        if (dy > downAccuracy)
            onScrolled(ScrollDirection.DOWN)
        else if (dy < upAccuracy)
            onScrolled(ScrollDirection.UP)
    }

    override fun onScrollChange(
        v: View?,
        scrollX: Int,
        scrollY: Int,
        oldScrollX: Int,
        oldScrollY: Int
    ) {
        val dy = oldScrollY - scrollY

        if (dy > downAccuracy)
            onScrolled(ScrollDirection.DOWN)
        else if (dy < upAccuracy)
            onScrolled(ScrollDirection.UP)
    }
}


enum class ScrollDirection {
    UP, DOWN
}

