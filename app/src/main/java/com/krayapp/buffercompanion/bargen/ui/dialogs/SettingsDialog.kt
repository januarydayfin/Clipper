package com.krayapp.buffercompanion.bargen.ui.dialogs

import android.content.DialogInterface
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
import androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_NO
import androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_YES
import androidx.appcompat.app.AppCompatDelegate.setDefaultNightMode
import androidx.core.view.children
import androidx.fragment.app.DialogFragment
import com.krayapp.buffercompanion.bargen.ClipperApp
import com.krayapp.buffercompanion.bargen.databinding.DialogSettingsBinding
import com.krayapp.buffercompanion.bargen.utils.decodedSize

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
        super.onViewCreated(view, savedInstanceState)
        binding?.run {
            val (width, height) = decodedSize
            root.layoutParams = FrameLayout.LayoutParams(width, -1).apply {
                gravity = Gravity.CENTER
            }

            val needToCheckId = findCurrentThemeButtonId()
            if (needToCheckId != null)
                toggleButton.check(needToCheckId)

            binding?.run {
                darkTheme.setOnClickListener(::uncheckExcept)
                systemTheme.setOnClickListener(::uncheckExcept)
                lightTheme.setOnClickListener(::uncheckExcept)
            }

            close.setOnClickListener {
                dismiss()
            }
        }
    }

    private fun uncheckExcept(except: View) {
        val theme = AppTheme.valueOf(except.tag.toString())
        binding?.toggleButton?.run {
            val toUncheck = children.filter { it.id != except.id }
            toUncheck.forEach {
                uncheck(it.id)
            }
            check(except.id)
        }

        setMode(theme)
    }

    private fun findCurrentThemeButtonId(): Int? {
        var id: Int? = null

        binding?.run {
            toggleButton.children.forEach {
                val parsed = AppTheme.valueOf(it.tag.toString())
                if (parsed.value == ClipperApp.getPrefs().theme)
                    id = it.id
            }
        }
        return id
    }

    private fun setMode(theme: AppTheme) {
        setDefaultNightMode(theme.value)
        ClipperApp.getPrefs().theme = theme.value
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        binding = null
    }

    private enum class AppTheme(val value: Int) {
        SYSTEM(MODE_NIGHT_FOLLOW_SYSTEM), LIGHT(MODE_NIGHT_NO), DARK(MODE_NIGHT_YES)
    }
}