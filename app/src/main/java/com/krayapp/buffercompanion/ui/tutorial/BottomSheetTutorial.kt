package com.krayapp.buffercompanion.ui.tutorial

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.color.MaterialColors
import com.krayapp.buffercompanion.ClipperApp
import com.krayapp.buffercompanion.R
import com.krayapp.buffercompanion.databinding.BottomsheetTutorialBinding
import com.krayapp.buffercompanion.getSimpleFragmentAdapter

class BottomSheetTutorial : BottomSheetDialogFragment() {
    private var vb: BottomsheetTutorialBinding? = null

    private lateinit var adapter: FragmentStateAdapter
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        vb = BottomsheetTutorialBinding.inflate(inflater)
        return vb!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        adapter = getSimpleFragmentAdapter(getFragList(), this)
        colorNavBar()
        initPager()

        vb!!.next.setOnClickListener {
            val current = vb!!.viewpager.currentItem
            val size = vb!!.viewpager.adapter!!.itemCount

            vb!!.viewpager.currentItem = current + 1
            if (current == size - 1)
                dismiss()
        }
    }

    private fun colorNavBar() {
        val color =
            MaterialColors.getColor(requireContext(), R.attr.bottomSheetBottomColor, Color.BLACK)
        dialog?.window?.navigationBarColor = color
    }

    private fun getFragList(): ArrayList<Fragment> {
        return arrayListOf(TutorCreateText(), TutorManageText(), TutorCopyText(), TutorWidget())
    }

    private fun initPager() {
        vb!!.viewpager.isUserInputEnabled = false
        vb!!.viewpager.adapter = adapter
        vb!!.dots.boundWithPager(vb!!.viewpager)
    }


    override fun onDestroyView() {
        super.onDestroyView()
        vb = null
        ClipperApp.getPrefs().onTutorFinished()
    }

}