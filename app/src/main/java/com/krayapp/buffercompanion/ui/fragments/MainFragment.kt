package com.krayapp.buffercompanion.ui.fragments

import android.annotation.SuppressLint
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.util.Log
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
import androidx.transition.TransitionInflater
import com.krayapp.buffercompanion.R
import com.krayapp.buffercompanion.activity
import com.krayapp.buffercompanion.addTextWatcher
import com.krayapp.buffercompanion.data.MainRepo
import com.krayapp.buffercompanion.data.room.StringEntity
import com.krayapp.buffercompanion.databinding.FragmentMainBinding
import com.krayapp.buffercompanion.setGone
import com.krayapp.buffercompanion.setVisible
import com.krayapp.buffercompanion.ui.RecyclerTouchControl
import com.krayapp.buffercompanion.ui.RecyclerViewSpacer
import com.krayapp.buffercompanion.ui.WordsAdapter
import com.krayapp.buffercompanion.ui.botsheets.EditTextBottomSheet
import com.krayapp.buffercompanion.ui.fragments.interfaces.ListEditWatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MainFragment : Fragment() {
	private lateinit var vb: FragmentMainBinding

	private lateinit var repo: MainRepo
	private lateinit var wordsAdapter: WordsAdapter
	private lateinit var touchHelper: RecyclerTouchControl

	private var dragStarted = false

	private val dataSet = ArrayList<StringEntity>()

	private val backDispatcher by lazy {
		object : OnBackPressedCallback(true) {
			override fun handleOnBackPressed() {
				wordsAdapter.resetEdition()
			}
		}
	}

	override fun onCreate(savedInstanceState: Bundle?) {
		val inflater = TransitionInflater.from(requireContext())
		enterTransition = inflater.inflateTransition(R.transition.slide_right)
		super.onCreate(savedInstanceState)
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
		view.setBackgroundColor(requireContext().getColor(R.color.md_theme_surface))
		repo = MainRepo(requireContext())
		initClick()
		initAdapter()
		initTextWatcher()
	}

	private fun initTextWatcher() {
		vb.edit.addTextWatcher {
			for (item in dataSet) {
				if (it == item.text) {
					vb.editLayout.isErrorEnabled = true
					vb.editLayout.error = context?.getString(R.string.already_exist)
					break
				} else {
					vb.editLayout.isErrorEnabled = false

					vb.editLayout.error = null
				}
			}
		}
	}

	@SuppressLint("ClickableViewAccessibility")
	private fun initAdapter() {
		wordsAdapter = WordsAdapter(getListWatcher())
		touchHelper = RecyclerTouchControl(wordsAdapter)
		val helper = ItemTouchHelper(touchHelper).apply { attachToRecyclerView(vb.recycler) }

		with(wordsAdapter) {
			setDrag(
				onStartDrag = {
					dragStarted = true
					helper.startDrag(it)
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

		reloadRepo {
			if (dataSet.isNotEmpty())
				vb.emptyHint.setGone()
			MainScope().launch {
				wordsAdapter.initData(dataSet)
			}
		}

	}


	private fun getListWatcher(): ListEditWatcher {
		return object : ListEditWatcher {
			override fun onEditionStart() {
				activity().onBackPressedDispatcher.addCallback(backDispatcher)
			}

			override fun onEditionReset() {
				backDispatcher.remove()
				activity().toolbarAssistant().onMainScreen()
			}

			override fun onCopyClicked(entity: StringEntity) {
				copy(entity.text)
			}

			override fun onRemoveClicked(entity: StringEntity) {
				removeString(entity)
			}

			override fun onEditClicked(entity: StringEntity) {
				EditTextBottomSheet(entity) { oldKey, newEntity ->
					wordsAdapter.updateWord(oldKey, newEntity)
					repo.replace(oldKey, newEntity)
					reloadRepo()
				}.show(childFragmentManager, "")
			}

			override fun onCheckRemoveStart() {
				activity().toolbarAssistant()
					.onCheckRemoveStarted({ wordsAdapter.checkAllRemove() }, onDelete = {
						repo.removeList(wordsAdapter.getCheckedList())
						reloadRepo()
						wordsAdapter.removeChecked()
					})
			}

			override fun onAdapterHasData(hasData: Boolean) {
				if (hasData) {
					vb.recycler.setVisible()
					vb.emptyHint.setGone()
				} else {
					vb.recycler.setGone()
					vb.emptyHint.setVisible()
				}
			}
		}
	}

	private fun reloadRepo(onLoaded: ((List<StringEntity>) -> Unit)? = null) {
		CoroutineScope(Dispatchers.IO).launch {
			if (onLoaded == null) //Нужно моментально получить данные, если есть колбек, т.к это инициализация
				delay(1000)
			repo.loadList {
				dataSet.apply {
					clear()
					addAll(it)
				}
				if (onLoaded != null)
					onLoaded(dataSet)
			}
		}
	}

	private fun initClick() {
		vb.editLayout.setEndIconOnClickListener {
			val text = vb.edit.text.toString()
			if (text.isNotEmpty() && vb.editLayout.error == null) {
				createNewText(text)
			} else
				pasteFromClip()
		}

		vb.edit.setOnEditorActionListener(object : TextView.OnEditorActionListener {
			override fun onEditorAction(v: TextView?, actionId: Int, event: KeyEvent?): Boolean {
				if (actionId == EditorInfo.IME_ACTION_DONE) {
					val text = vb.edit.text.toString().trim()
					if (text.isNotEmpty() && vb.editLayout.error == null) {
						createNewText(text)
					}
					return true
				}
				return false
			}
		})
	}

	private fun createNewText(text: String) {
		repo.addText(text)
		reloadRepo()
		addStringToAdapter(text)
		vb.edit.text?.clear()
		vb.recycler.scrollToPosition(wordsAdapter.itemCount - 1)
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

	private fun removeString(text: StringEntity) {
		CoroutineScope(Dispatchers.IO).launch {
			repo.remove(text)
			reloadRepo()
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
		val manager =
			requireContext().getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager

		val textFromClip = manager.primaryClip?.getItemAt(0)?.text.toString()

		if (textFromClip.isNotEmpty() && textFromClip != "null") {
			repo.addText(textFromClip)
			addStringToAdapter(textFromClip)
			reloadRepo()
		}
	}
}