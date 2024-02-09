package com.krayapp.buffercompanion.ui.fragments

import android.annotation.SuppressLint
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.krayapp.buffercompanion.R
import com.krayapp.buffercompanion.data.RememberedRepo
import com.krayapp.buffercompanion.databinding.FragmentMainBinding
import com.krayapp.buffercompanion.hideKeyboard
import com.krayapp.buffercompanion.ui.MainActivity
import com.krayapp.buffercompanion.ui.RecyclerTouchControl
import com.krayapp.buffercompanion.ui.RecyclerViewSpacer
import com.krayapp.buffercompanion.ui.WordsAdapter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

class MainFragment : Fragment() {
	private lateinit var vb : FragmentMainBinding

	private lateinit var repo: RememberedRepo
	private lateinit var adapter: WordsAdapter
	private lateinit var touchHelper: RecyclerTouchControl

	private var dragStarted = false

	override fun onCreateView(
		inflater: LayoutInflater,
		container: ViewGroup?,
		savedInstanceState: Bundle?
	): View {
		vb = FragmentMainBinding.inflate(inflater)
		return vb.root
	}

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)
		repo = RememberedRepo(requireContext())
		initClick()
		initAdapter()
	}

	private fun activity() : MainActivity {
		return activity as MainActivity
	}
	@SuppressLint("ClickableViewAccessibility")
	private fun initAdapter() {
		adapter = WordsAdapter(onClicked = { copy(it) })
		touchHelper = RecyclerTouchControl(adapter) { removeString(it) }
		val helper = ItemTouchHelper(touchHelper).apply { attachToRecyclerView(vb.recycler) }

		adapter.setDrag(
			onStartDrag = {
				dragStarted = true
				helper.startDrag(it)
			}
		)

		vb.recycler.layoutManager = LinearLayoutManager(requireContext())
		vb.recycler.adapter = adapter

		vb.recycler.addItemDecoration(RecyclerViewSpacer(12, RecyclerView.VERTICAL))
		vb.recycler.setOnTouchListener { _, event ->
			if (event.actionMasked == MotionEvent.ACTION_UP && dragStarted) {
				repo.updateBaseIndexes(adapter.getUpdatedIndexData())
				dragStarted = false
			}
			return@setOnTouchListener false
		}

		repo.loadList { MainScope().launch { adapter.initData(ArrayList(it)) } }
	}


	private fun initClick() {
		vb.paste.setOnClickListener { pasteFromClip() }
		vb.toolbar.refreshWidget.setOnClickListener { activity().updateWidget() }
		vb.edit.setOnEditorActionListener(object : TextView.OnEditorActionListener {
			override fun onEditorAction(v: TextView?, actionId: Int, event: KeyEvent?): Boolean {
				if (actionId == EditorInfo.IME_ACTION_DONE) {
					val text = vb.edit.text.toString().trim()
					if (text.isNotEmpty()) {
						repo.addText(text)
						addStringToAdapter(text)
						vb.edit.text?.clear()
						requireContext().hideKeyboard(vb.edit)
					}
					return true
				}
				return false
			}
		})

		vb.toolbar.showSettings.setOnClickListener {
			activity().navigateTo(R.id.toSettings)
		}
	}
	private fun copy(text: String) {
		val manager = requireContext().getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
		val clip = ClipData.newPlainText("label", text)
		manager.setPrimaryClip(clip)

		if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.S_V2)
			Toast.makeText(
				requireContext(),
				"Скопировано",
				Toast.LENGTH_SHORT
			).show()
	}

	private fun removeString(text: String) {
		CoroutineScope(Dispatchers.IO).launch {
			repo.remove(text)
		}

		MainScope().launch {
			adapter.deleteWord(text)
		}
	}

	private fun addStringToAdapter(text: String) {
		adapter.addWord(text)
	}

	override fun onStop() {
		vb.edit.clearFocus()
		super.onStop()
	}


	private fun pasteFromClip() {
		val manager = requireContext().getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager

		val textFromClip = manager.primaryClip?.getItemAt(0)?.text.toString()

		if (textFromClip.isNotEmpty() && textFromClip != "null") {
			repo.addText(textFromClip)
			addStringToAdapter(textFromClip)
		}
	}
}