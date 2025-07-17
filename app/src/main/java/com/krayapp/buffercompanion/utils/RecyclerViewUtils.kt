package com.krayman.toolbox

import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

fun RecyclerView.firstVisiblePosition(): Int {
    if (layoutManager is LinearLayoutManager) {
        val manager = layoutManager as LinearLayoutManager
        return manager.findFirstVisibleItemPosition()
    } else {
        return -1
    }
}

fun RecyclerView.lastVisiblePosition(): Int {
    when (layoutManager) {
        is LinearLayoutManager -> {
            val manager = layoutManager as LinearLayoutManager
            return manager.findLastVisibleItemPosition()
        }

        else -> {
            return -1
        }
    }
}

fun RecyclerView.onScrollStopped(block: () -> Unit) {
    addOnScrollListener(object :
        RecyclerView.OnScrollListener() {
        override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
            if (newState == 0) block()
            super.onScrollStateChanged(recyclerView, newState)
        }
    })
}

fun RecyclerView.scrollImmediately(position: Int) {
    val manager = layoutManager as LinearLayoutManager
    manager.scrollToPositionWithOffset(position, 0)
}