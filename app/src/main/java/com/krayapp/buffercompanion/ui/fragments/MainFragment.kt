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
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.krayapp.buffercompanion.activity
import com.krayapp.buffercompanion.data.RememberedRepo
import com.krayapp.buffercompanion.databinding.FragmentMainBinding
import com.krayapp.buffercompanion.hideKeyboard
import com.krayapp.buffercompanion.ui.RecyclerTouchControl
import com.krayapp.buffercompanion.ui.RecyclerViewSpacer
import com.krayapp.buffercompanion.ui.WordsAdapter
import com.krayapp.buffercompanion.ui.fragments.interfaces.OnMenusWatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

class MainFragment : AbsFragment() {
	private lateinit var vb : FragmentMainBinding

	private lateinit var repo: RememberedRepo
	private lateinit var wordsAdapter: WordsAdapter
	private lateinit var touchHelper: RecyclerTouchControl

	private var dragStarted = false

	private val backDispatcher by lazy {
		object : OnBackPressedCallback(true) {
			override fun handleOnBackPressed() {
				wordsAdapter.resetMenus()
			}
		}
	}

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

	@SuppressLint("ClickableViewAccessibility")
	private fun initAdapter() {
		wordsAdapter = WordsAdapter(
			onCopyClicked = { copy(it) },
			onRemoveClicked = { removeString(it) },
			onEditClicked = {}
		)
		touchHelper = RecyclerTouchControl(wordsAdapter)
		val helper = ItemTouchHelper(touchHelper).apply { attachToRecyclerView(vb.recycler) }

		with(wordsAdapter) {
			setDrag(
				onStartDrag = {
					dragStarted = true
					helper.startDrag(it)
				})

			attachMenusWatcher(object : OnMenusWatcher {
				override fun onMenusOpened() {
					activity().onBackPressedDispatcher.addCallback(backDispatcher)
				}

				override fun onMenusAllClosed() {
					backDispatcher.remove()
				}
			})
		}


		with(vb.recycler) {
			layoutManager = LinearLayoutManager(requireContext())
			adapter = wordsAdapter
			addItemDecoration(RecyclerViewSpacer(12, RecyclerView.VERTICAL))
			setOnTouchListener { _, event ->
				if (event.actionMasked == MotionEvent.ACTION_UP && dragStarted) {
					repo.updateBaseIndexes(wordsAdapter.getUpdatedIndexData())
					dragStarted = false
				} else if (event.actionMasked == MotionEvent.ACTION_UP)
					touchHelper.onActionUp()
				return@setOnTouchListener false
			}
		}

		repo.loadList { MainScope().launch { wordsAdapter.initData(ArrayList(it)) } }
	}


	private fun initClick() {
		vb.editLayout.setEndIconOnClickListener { pasteFromClip() }
		vb.edit.setOnClickListener {

		}
		vb.edit.setOnEditorActionListener(object : TextView.OnEditorActionListener {
			override fun onEditorAction(v: TextView?, actionId: Int, event: KeyEvent?): Boolean {
				if (actionId == EditorInfo.IME_ACTION_DONE) {
					val text = vb.edit.text.toString().trim()
					if (text.isNotEmpty()) {
						repo.addText(text)
						addStringToAdapter(text)
						vb.edit.text?.clear()
						vb.recycler.scrollToPosition(wordsAdapter.itemCount - 1)
					}
					return true
				}
				return false
			}
		})


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
	}

	private fun addStringToAdapter(text: String) {
		wordsAdapter.addWord(text)
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