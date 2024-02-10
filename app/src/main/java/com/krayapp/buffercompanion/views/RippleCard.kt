package com.krayapp.buffercompanion.views

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import com.krayapp.buffercompanion.databinding.LayoutRippleCardBinding

class RippleCard(context: Context, attributeSet: AttributeSet? = null) :
	FrameLayout(context, attributeSet) {

	private val vb: LayoutRippleCardBinding = LayoutRippleCardBinding.inflate(
		LayoutInflater.from(context), this, true
	)
	override fun setOnClickListener(l: OnClickListener?) {
		vb.clickable.setOnClickListener(l)
	}

	override fun addView(child: View?, params: ViewGroup.LayoutParams?) {
		vb.clickable.addView(child, params)
	}

}