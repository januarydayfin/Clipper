package com.krayapp.buffercompanion.ui.fragments

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.transition.TransitionInflater
import com.krayapp.buffercompanion.R

abstract class AbsFragment: Fragment(){
    override fun onCreate(savedInstanceState: Bundle?) {
        val inflater = TransitionInflater.from(requireContext())
        enterTransition = inflater.inflateTransition(R.transition.fade)
        super.onCreate(savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        view.setBackgroundColor(requireContext().getColor(R.color.md_theme_surface))
        super.onViewCreated(view, savedInstanceState)
    }
}