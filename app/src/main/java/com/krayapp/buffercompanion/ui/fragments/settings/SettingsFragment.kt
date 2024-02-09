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
	}

	override fun getTitleRes(): Int {
		return R.string.settings_title
	}

	private fun initClick() {
		vb.dynamicColorsSwitch.isChecked = ClipperApp.getPrefs().isDynamicColors()

		vb.dynamicColorsSwitch.setOnCheckedChangeListener { buttonView, isChecked ->
			ClipperApp.getPrefs().setDynamicColors(isChecked)
			activity().restartApp()
		}

		vb.appTheme.setOnClickListener { activity().navigateTo(R.id.toAppTheme) }
	}

}