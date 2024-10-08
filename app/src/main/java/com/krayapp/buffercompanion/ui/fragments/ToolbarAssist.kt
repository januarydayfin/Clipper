package com.krayapp.buffercompanion.ui.fragments

import android.view.View
import androidx.annotation.StringRes
import androidx.core.view.isVisible
import com.krayapp.buffercompanion.databinding.LayoutToolbarBinding
import com.krayapp.buffercompanion.setGone
import com.krayapp.buffercompanion.setInvisible
import com.krayapp.buffercompanion.setVisible

class ToolbarAssist(
	view: View,
	private val vb: LayoutToolbarBinding = LayoutToolbarBinding.bind(view)
) {

	fun setTitle(@StringRes res: Int) {
		vb.appTitle.setInvisible()
		vb.showSettings.setInvisible()
		vb.fragmentTitle.setVisible()
		vb.fragmentTitle.setText(res)
	}

	fun setBackClick(onBack: (Any) -> Unit) {
		vb.back.setVisible()
		vb.back.setOnClickListener(onBack)
	}

	fun onMainScreen() {
		vb.checkAll.setGone()
		vb.delete.setGone()
		vb.appTitle.setVisible()
		vb.showSettings.setVisible()
		vb.fragmentTitle.setInvisible()
		vb.back.setInvisible()
	}

	fun onCheckRemoveStarted(checkAll: (Any) -> Unit, onDelete: (Any) -> Unit) {
		vb.showSettings.setInvisible()
		vb.checkAll.setVisible()
		vb.delete.setVisible()
		vb.checkAll.setOnClickListener { checkAll("") }
		vb.delete.setOnClickListener { onDelete("") }
	}
}