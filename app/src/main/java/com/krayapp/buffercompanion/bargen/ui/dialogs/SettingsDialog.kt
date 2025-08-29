package com.krayapp.buffercompanion.bargen.ui.dialogs

import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
import androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_NO
import androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_YES
import androidx.appcompat.app.AppCompatDelegate.setDefaultNightMode
import androidx.core.view.children
import androidx.fragment.app.DialogFragment
import com.google.android.material.button.MaterialButtonToggleGroup
import com.krayapp.buffercompanion.bargen.ClipperApp
import com.krayapp.buffercompanion.bargen.R
import com.krayapp.buffercompanion.bargen.databinding.DialogSettingsBinding
import com.krayapp.buffercompanion.bargen.utils.setCustomBackground
import com.krayapp.buffercompanion.bargen.utils.setupDialogWidth

class SettingsDialog : DialogFragment() {
    private var binding: DialogSettingsBinding? = null
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DialogSettingsBinding.inflate(inflater)
        return binding?.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        dialog.setCustomBackground(R.drawable.dialog_background)
        super.onViewCreated(view, savedInstanceState)
        binding?.run {
            setupDialogWidth()
            setupThemePicked(this)
            setupOpenCardCheckbox(this)
            setupVolumeScannerCheckbox(this)
            close.setOnClickListener {
                dismiss()
            }
        }
    }

    private fun setupThemePicked(binding: DialogSettingsBinding) {
        fun uncheckExcept(except: View, group: MaterialButtonToggleGroup) {
            group.run {
                val toUncheck = children.filter { it.id != except.id }
                toUncheck.forEach {
                    uncheck(it.id)
                }
                check(except.id)
            }
        }


        fun findCurrentThemeButtonId(): Int? {
            var id: Int? = null

            binding.run {
                themeToggleGroup.children.forEach {
                    val parsed = AppTheme.valueOf(it.tag.toString())
                    if (parsed.value == ClipperApp.getPrefs().theme)
                        id = it.id
                }
            }
            return id
        }

        fun setMode(theme: AppTheme) {
            setDefaultNightMode(theme.value)
            ClipperApp.getPrefs().theme = theme.value
        }

        with(binding) {
            val themeCurrentCheckId = findCurrentThemeButtonId()
            if (themeCurrentCheckId != null)
                themeToggleGroup.check(themeCurrentCheckId)

            darkTheme.setOnClickListener { v ->
                uncheckExcept(v, themeToggleGroup)
                setMode(AppTheme.valueOf(v.tag.toString()))
            }
            systemTheme.setOnClickListener { v ->
                uncheckExcept(v, themeToggleGroup)
                setMode(AppTheme.valueOf(v.tag.toString()))

            }
            lightTheme.setOnClickListener { v ->
                uncheckExcept(v, themeToggleGroup)
                setMode(AppTheme.valueOf(v.tag.toString()))
            }
        }
    }

    private fun setupOpenCardCheckbox(binding: DialogSettingsBinding) {
        with(binding) {
            openCardAfterScanBox.isChecked = ClipperApp.getPrefs().openCardAfterScan
            openCardAfterScanBox.setOnCheckedChangeListener { _, isChecked ->
                ClipperApp.getPrefs().openCardAfterScan = isChecked
            }
        }
    }

    private fun setupVolumeScannerCheckbox(binding: DialogSettingsBinding) {
        with(binding) {
            volumeScannerCheckbox.isChecked = ClipperApp.getPrefs().scanOnVolume
            volumeScannerCheckbox.setOnCheckedChangeListener { _, isChecked ->
                ClipperApp.getPrefs().scanOnVolume = isChecked
            }
        }
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        binding = null
    }

    private enum class AppTheme(val value: Int) {
        SYSTEM(MODE_NIGHT_FOLLOW_SYSTEM), LIGHT(MODE_NIGHT_NO), DARK(MODE_NIGHT_YES)
    }
}