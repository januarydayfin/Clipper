package com.krayapp.buffercompanion.ui.tutorial

import android.content.ClipboardManager
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.krayapp.buffercompanion.R
import com.krayapp.buffercompanion.databinding.TutorialCopyFragmentBinding

class TutorCopyText : Fragment() {
    private var vb : TutorialCopyFragmentBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        vb = TutorialCopyFragmentBinding.inflate(inflater)
        return vb!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        vb!!.textContainer.title.setText(R.string.tutor_copy_title)
        vb!!.textContainer.description.setText(R.string.tutor_copy_desc)

        with(vb!!.item) {
            text.text = this@TutorCopyText.getText()
            root.setOnClickListener { copy() }
        }
    }

    private fun getText(): String {
        val manager =
            requireContext().getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager

        val fromClip = manager.primaryClip?.getItemAt(0)?.text.toString()

        if (fromClip.isNotEmpty() && fromClip != "null")
            return fromClip
        else
            return "Any text"
    }

    private fun copy() {
        Toast.makeText(
            requireContext(),
            R.string.copied,
            Toast.LENGTH_SHORT
        ).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        vb = null
    }
}
