package com.krayapp.buffercompanion.ui.fragments.settings

import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.krayapp.buffercompanion.ClipperApp
import com.krayapp.buffercompanion.R
import com.krayapp.buffercompanion.activity
import com.krayapp.buffercompanion.databinding.FragmentSettingsBinding
import com.krayapp.buffercompanion.setVisible
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

		vb.version.text = getAppVersion()
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S)
			vb.dynamicColorsSwitch.setVisible()

		initClick()
	}

	private fun getAppVersion(): String {
		return try {
			val packageManager = requireContext().packageManager
			val packageName = requireContext().packageName
			val packageInfo = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
				packageManager.getPackageInfo(packageName, PackageManager.PackageInfoFlags.of(0))
			} else {
				packageManager.getPackageInfo(packageName, 0)
			}
			packageInfo.versionName
		} catch (e: Exception) {
			""
		}
	}
	override fun getTitleRes(): Int {
		return R.string.settings_title
	}

	private fun initClick() {
		vb.dynamicColorsSwitch.isChecked = ClipperApp.getPrefs().isDynamicColors()

		vb.dynamicColorsSwitch.setOnCheckedChangeListener { _, isChecked ->
			ClipperApp.getPrefs().setDynamicColors(isChecked)
			activity().restartApp()
		}

		vb.refreshWidget.setOnClickListener { activity().refreshWidget() }
		vb.appTheme.setOnClickListener { activity().navigateTo(R.id.toAppTheme) }
	}

}