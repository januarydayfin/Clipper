package com.krayapp.buffercompanion.ui

import android.graphics.Rect
import android.view.View
import androidx.annotation.IntRange
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.krayapp.buffercompanion.dp

class RecyclerViewSpacer(@IntRange(from = 0, to = 50) private val spaceInDp: Int, @RecyclerView.Orientation private val orientation: Int) :
	RecyclerView.ItemDecoration() {
	override fun getItemOffsets(
		outRect: Rect,
		view: View,
		parent: RecyclerView,
		state: RecyclerView.State
	) {
		if (orientation == RecyclerView.VERTICAL) {
			outRect.bottom = view.context.dp(spaceInDp)
		} else if (orientation == RecyclerView.HORIZONTAL) {
			val pos = parent.getChildAdapterPosition(view)
			if (parent.layoutManager is GridLayoutManager) {
				val spanCount = (parent.layoutManager as GridLayoutManager).spanCount
				if (pos % spanCount != 0) {
					outRect.left = view.context.dp(spaceInDp)
				}
			}
		}
	}
}