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
import com.krayapp.buffercompanion.databinding.TutorialWidgetFragmentBinding

class TutorWidget : Fragment() {
    private var vb: TutorialWidgetFragmentBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        vb = TutorialWidgetFragmentBinding.inflate(inflater)
        return vb!!.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        vb!!.textContainer.title.setText(R.string.tutor_widget_title)
        vb!!.textContainer.description.setText(R.string.tutor_widget_desc)

        with(vb!!.widget.widgetText) {
            text = this@TutorWidget.getText()
            setOnClickListener { copy(text.toString()) }
        }

    }

    private fun copy(text: String) {
        val manager =
            requireContext().getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText("label", text)
        manager.setPrimaryClip(clip)

        Toast.makeText(
            requireContext(),
            R.string.copied,
            Toast.LENGTH_SHORT
        ).show()
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

    override fun onDestroyView() {
        super.onDestroyView()
        vb = null
    }
}