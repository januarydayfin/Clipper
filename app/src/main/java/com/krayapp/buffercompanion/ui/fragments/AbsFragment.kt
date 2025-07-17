package com.krayapp.buffercompanion.ui.fragments

import android.os.Bundle
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.transition.TransitionInflater
import com.krayapp.buffercompanion.R
import com.krayapp.buffercompanion.activity

abstract class AbsFragment : Fragment() {
    override fun onCreate(savedInstanceState: Bundle?) {
        val inflater = TransitionInflater.from(requireContext())
        enterTransition = inflater.inflateTransition(R.transition.slide_right)
        super.onCreate(savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.setBackgroundColor(requireContext().getColor(R.color.md_theme_surface))
    }

    override fun onStart() {
        super.onStart()
        setupToolbar()
    }


    private fun setupToolbar() {
        with(activity().toolbarAssistant()) {
            setTitle(getTitleRes())
            setBackClick { backAction() }
        }
    }

    /**
     * Можем переопределить в дочернем фрагменте, чтобы поменять поведение
     */
    private fun backAction() {
        activity().popBackStack()
    }
    abstract fun getTitleRes() : Int
}