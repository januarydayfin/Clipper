package com.krayapp.buffercompanion.ui.fragments

import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import com.krayapp.buffercompanion.ui.MainActivity

class SettingsFragment : Fragment() {


	private val backDispatcher by lazy {
		object : OnBackPressedCallback(true) {
			override fun handleOnBackPressed() {
				activity().popBackStack()
			}
		}
	}

	private fun activity() : MainActivity {
		return activity as MainActivity
	}

	override fun onResume() {
		super.onResume()
		activity().onBackPressedDispatcher.addCallback(backDispatcher)
	}
	override fun onStop() {
		backDispatcher.remove()
		super.onStop()
	}
}