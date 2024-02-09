package com.krayapp.buffercompanion.ui.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.transition.TransitionInflater
import com.krayapp.buffercompanion.R

abstract class AbsFragment: Fragment(){
    override fun onCreate(savedInstanceState: Bundle?) {
        val inflater = TransitionInflater.from(requireContext())
        enterTransition = inflater.inflateTransition(R.transition.fade)
        super.onCreate(savedInstanceState)
    }
}