package com.krayapp.buffercompanion.ui.tutorial

import android.annotation.SuppressLint
import android.content.ClipboardManager
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.krayapp.buffercompanion.R
import com.krayapp.buffercompanion.data.room.StringEntity
import com.krayapp.buffercompanion.databinding.TutorialManageFragmentBinding
import com.krayapp.buffercompanion.ui.RecyclerTouchControl
import com.krayapp.buffercompanion.ui.RecyclerViewSpacer
import com.krayapp.buffercompanion.ui.WordsAdapter
import com.krayapp.buffercompanion.ui.fragments.interfaces.ListEditWatcher
import java.util.Collections

class TutorManageText : Fragment() {
    private var vb: TutorialManageFragmentBinding? = null
    private lateinit var wordsAdapter: WordsAdapter
    private lateinit var touchHelper: RecyclerTouchControl


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        vb = TutorialManageFragmentBinding.inflate(inflater)
        return vb!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        vb!!.textContainer.title.setText(R.string.tutor_manage_title)
        vb!!.textContainer.description.setText(R.string.tutor_manage_desc)

        initRecycler()
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun initRecycler() {
        wordsAdapter = WordsAdapter(getListWatcher(), tutorMode = true)
        touchHelper = RecyclerTouchControl(wordsAdapter)
        val helper = ItemTouchHelper(touchHelper).apply { attachToRecyclerView(vb!!.recycler) }

        with(vb!!.recycler) {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = wordsAdapter
            addItemDecoration(RecyclerViewSpacer(12, RecyclerView.VERTICAL))

            setOnTouchListener { _, event ->
                if (event.actionMasked == MotionEvent.ACTION_UP)
                    touchHelper.onActionUp()
                return@setOnTouchListener false
            }

        }

        wordsAdapter.initData(ArrayList<StringEntity>().apply { add(StringEntity(getText(), 0)) })
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

    private fun getListWatcher(): ListEditWatcher {
        return object : ListEditWatcher {
            override fun onEditionStart() {

            }

            override fun onEditionReset() {

            }

            override fun onCopyClicked(entity: StringEntity) {

            }

            override fun onRemoveClicked(entity: StringEntity) {

            }

            override fun onEditClicked(entity: StringEntity) {

            }

            override fun onCheckRemoveStart() {

            }

            override fun onAdapterHasData(hasData: Boolean) {

            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        vb = null
    }
}