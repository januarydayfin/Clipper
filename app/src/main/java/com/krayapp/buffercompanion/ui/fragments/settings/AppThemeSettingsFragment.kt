package com.krayapp.buffercompanion.ui.fragments.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
import androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_NO
import androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_YES
import androidx.appcompat.app.AppCompatDelegate.getDefaultNightMode
import androidx.appcompat.app.AppCompatDelegate.setDefaultNightMode
import com.krayapp.buffercompanion.ClipperApp
import com.krayapp.buffercompanion.R
import com.krayapp.buffercompanion.databinding.FragmentAppThemeSettingsBinding
import com.krayapp.buffercompanion.ui.fragments.AbsFragment

class AppThemeSettingsFragment : AbsFragment() {
	private var vb: FragmentAppThemeSettingsBinding? = null
		get() = field!!

	private var currentMode: Int = getDefaultNightMode()
	override fun onCreateView(
		inflater: LayoutInflater,
		container: ViewGroup?,
		savedInstanceState: Bundle?
	): View {
		vb = FragmentAppThemeSettingsBinding.inflate(inflater)
		return vb!!.root
	}

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)
		checkTheme()
		initClick()
	}

	override fun getTitleRes(): Int {
		return R.string.app_theme
	}


	private fun checkTheme() {
		vb!!.systemRadio.isChecked = currentMode == MODE_NIGHT_FOLLOW_SYSTEM
		vb!!.lightRadio.isChecked = currentMode == MODE_NIGHT_NO
		vb!!.darkRadio.isChecked = currentMode == MODE_NIGHT_YES
	}

	private fun initClick() {
		vb!!.lightTheme.setOnClickListener { setMode(MODE_NIGHT_NO) }
		vb!!.darkTheme.setOnClickListener { setMode(MODE_NIGHT_YES) }
		vb!!.systemTheme.setOnClickListener { setMode(MODE_NIGHT_FOLLOW_SYSTEM) }
	}

	private fun setMode(mode: Int) {
		currentMode = mode
		setDefaultNightMode(mode)
		checkTheme()
		ClipperApp.getPrefs().setThemeMode(mode)
	}

	override fun onDestroyView() {
		super.onDestroyView()
		vb = null
	}
}