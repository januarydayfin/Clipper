package com.krayapp.buffercompanion.bargen.utils

import android.animation.Animator
import android.view.View
import android.view.ViewPropertyAnimator
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

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

fun RecyclerView?.disableAnimation() {
    this?.itemAnimator = null
}

fun RecyclerView?.enableAnimation(scope: CoroutineScope) {
    scope.launch {
        delay(500)
        this@enableAnimation?.itemAnimator = DefaultItemAnimator()
    }
}

fun View.attachHidingWithRecycler(
    recyclerView: RecyclerView,
    inverted: Boolean,
    translationToHide: Dp = 16.dp,
) {
    var isAnimating = false
    val startAnimator: () -> ViewPropertyAnimator? = {
        if (isAnimating)
            null
        else
            animate()
    }


    animate().setListener(object : Animator.AnimatorListener {
        override fun onAnimationStart(animation: Animator) {
            isAnimating = true
        }

        override fun onAnimationEnd(animation: Animator) {
            isAnimating = false
        }

        override fun onAnimationCancel(animation: Animator) {
            isAnimating = false
        }

        override fun onAnimationRepeat(animation: Animator) {
        }
    })


    recyclerView.addOnScrollListener(BasicScrollWatcher(true) {
        val show =
            it == (if (inverted) ScrollDirection.DOWN else ScrollDirection.UP)

        val translation = if (show) 0f else translationToHide.value


        if (show) isVisible = true
        startAnimator()
            ?.translationY(translation)
            ?.setDuration(100)
            ?.alpha(if (show) 1f else 0f)
            ?.setDuration(200)
            ?.withEndAction { if (!show) isVisible = false }

        isClickable = show
    })
}
