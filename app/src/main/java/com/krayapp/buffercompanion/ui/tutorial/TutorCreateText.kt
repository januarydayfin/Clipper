package com.krayapp.buffercompanion.ui.tutorial

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.krayapp.buffercompanion.R
import com.krayapp.buffercompanion.databinding.TutorialCreateFragmentBinding
import com.krayapp.buffercompanion.hideKeyboard
import com.krayapp.buffercompanion.onImeDone

class TutorCreateText : Fragment() {
    private var vb: TutorialCreateFragmentBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        vb = TutorialCreateFragmentBinding.inflate(inflater)
        return vb!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        vb!!.textContainer.title.setText(R.string.tutor_create_title)
        vb!!.textContainer.description.setText(R.string.tutor_create_desc)

        vb!!.editLayout.setEndIconOnClickListener {
            createText()
        }

        vb!!.edit.onImeDone {
            createText()
        }
    }


    private fun createText() {
        val text = vb!!.edit.text.toString()

        if (text.isNotEmpty())
            Toast.makeText(requireContext(), text, Toast.LENGTH_SHORT).show()
        else if (getFromClip().isNotEmpty() && getFromClip() != "null")
            Toast.makeText(requireContext(), getFromClip(), Toast.LENGTH_SHORT).show()

        copy(text)
        vb!!.edit.hideKeyboard()
    }

    private fun copy(text: String) {
        val manager =
            requireContext().getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText("label", text)
        manager.setPrimaryClip(clip)
    }

    private fun getFromClip(): String {
        val manager =
            requireContext().getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager

        return manager.primaryClip?.getItemAt(0)?.text.toString()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        vb = null
    }
}