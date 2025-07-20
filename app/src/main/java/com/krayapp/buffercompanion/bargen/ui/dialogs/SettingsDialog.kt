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
import com.google.android.material.button.MaterialButtonToggleGroup
import com.krayapp.buffercompanion.bargen.ClipperApp
import com.krayapp.buffercompanion.bargen.data.SearchType
import com.krayapp.buffercompanion.bargen.databinding.DialogSettingsBinding
import com.krayapp.buffercompanion.bargen.utils.decodedSize

class SettingsDialog : DialogFragment() {
    private var binding: DialogSettingsBinding? = null
    private var onDismiss: () -> Unit = { }
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

            val themeCurrentCheckId = findCurrentThemeButtonId()
            val currentSearchCheckId = findCurrentSearchTypeId()

            if (currentSearchCheckId != null)
                searchToggleGroup.check(currentSearchCheckId)

            if (themeCurrentCheckId != null)
                themeToggleGroup.check(themeCurrentCheckId)

            binding?.run {
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

                byName.setOnClickListener { v ->
                    uncheckExcept(v, searchToggleGroup)
                    setSearchType(SearchType.valueOf(v.tag.toString()))
                }
                byValue.setOnClickListener { v ->
                    uncheckExcept(v, searchToggleGroup)
                    setSearchType(SearchType.valueOf(v.tag.toString()))
                }
            }

            close.setOnClickListener {
                dismiss()
            }
        }
    }

    fun setOnDismiss(onDismiss: () -> Unit) {
        this.onDismiss = onDismiss
    }

    private fun uncheckExcept(except: View, group: MaterialButtonToggleGroup) {
        group.run {
            val toUncheck = children.filter { it.id != except.id }
            toUncheck.forEach {
                uncheck(it.id)
            }
            check(except.id)
        }
    }


    private fun findCurrentThemeButtonId(): Int? {
        var id: Int? = null

        binding?.run {
            themeToggleGroup.children.forEach {
                val parsed = AppTheme.valueOf(it.tag.toString())
                if (parsed.value == ClipperApp.getPrefs().theme)
                    id = it.id
            }
        }
        return id
    }

    private fun findCurrentSearchTypeId(): Int? {
        var id: Int? = null

        binding?.run {
            searchToggleGroup.children.forEach {
                val parsed = SearchType.valueOf(it.tag.toString()).toString()
                if (parsed == ClipperApp.getPrefs().searchType)
                    id = it.id
            }
        }
        return id
    }

    private fun setMode(theme: AppTheme) {
        setDefaultNightMode(theme.value)
        ClipperApp.getPrefs().theme = theme.value
    }

    private fun setSearchType(type: SearchType) {
        ClipperApp.getPrefs().searchType = type.toString()
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        onDismiss()
        binding = null
    }

    private enum class AppTheme(val value: Int) {
        SYSTEM(MODE_NIGHT_FOLLOW_SYSTEM), LIGHT(MODE_NIGHT_NO), DARK(MODE_NIGHT_YES)
    }
}