package com.krayapp.buffercompanion.ui.fragments.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.core.view.isVisible
import com.krayapp.buffercompanion.ClipperApp
import com.krayapp.buffercompanion.R
import com.krayapp.buffercompanion.activity
import com.krayapp.buffercompanion.databinding.FragmentSettingsBinding
import com.krayapp.buffercompanion.ui.fragments.AbsFragment

class SettingsFragment : AbsFragment() {
	private lateinit var vb: FragmentSettingsBinding
	private val backDispatcher by lazy {
		object : OnBackPressedCallback(true) {
			override fun handleOnBackPressed() {
				this@SettingsFragment.activity().popBackStack()
			}
		}
	}
	override fun onCreateView(
		inflater: LayoutInflater,
		container: ViewGroup?,
		savedInstanceState: Bundle?
	): View {
		vb = FragmentSettingsBinding.inflate(inflater)
		return vb.root
	}


	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)
		initClick()
		setupToolbar()
	}


	private fun setupToolbar() {
		with(activity().toolbarAssistant()) {
			setTitle(R.string.settings_title)
			setBackClick { activity().popBackStack() }
		}
	}

	private fun initClick() {
		vb.dynamicColorsSwitch.isChecked = ClipperApp.getPrefs().isDynamicColors()

		vb.dynamicColorsSwitch.setOnCheckedChangeListener { buttonView, isChecked ->
			ClipperApp.getPrefs().setDynamicColors(isChecked)
			activity().restartApp()
		}
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