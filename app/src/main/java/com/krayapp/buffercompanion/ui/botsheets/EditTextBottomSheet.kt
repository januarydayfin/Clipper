package com.krayapp.buffercompanion.ui.botsheets

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.color.MaterialColors
import com.krayapp.buffercompanion.R
import com.krayapp.buffercompanion.addTextWatcher
import com.krayapp.buffercompanion.data.MainRepo
import com.krayapp.buffercompanion.data.room.StringEntity
import com.krayapp.buffercompanion.databinding.BottomsheetEditTextBinding


class EditTextBottomSheet(
	private val origin: StringEntity,
	private val onEditionComplete: (String, StringEntity) -> Unit
) :
	BottomSheetDialogFragment() {
	private val key = origin.text
	private lateinit var vb: BottomsheetEditTextBinding

	private val dataSet = ArrayList<StringEntity>()

	override fun onCreateView(
		inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
	): View {
		vb = BottomsheetEditTextBinding.inflate(inflater)
		return vb.root
	}

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)

		MainRepo(vb.root.context).loadList { dataSet.addAll(it) }
		colorNavBar()
		initEditText()
		initClick()
	}

	private fun initEditText() {
		vb.edit.setText(origin.text)

		vb.edit.addTextWatcher {
			for (item in dataSet) {
				if (it == item.text && it.trim().isNotEmpty()) {
					if (it == origin.text)
						continue
					vb.editLayout.isErrorEnabled = true
					vb.editLayout.error = context?.getString(R.string.already_exist)
					vb.save.isEnabled = false
					break
				} else {
					vb.editLayout.isErrorEnabled = false
					vb.editLayout.error = null
					vb.save.isEnabled = true
				}
			}
		}
	}
	private fun colorNavBar() {
		val color = MaterialColors.getColor(requireContext(), R.attr.bottomSheetBottomColor, Color.BLACK)
		dialog?.window?.navigationBarColor = color
	}


	private fun initClick() {
		vb.save.setOnClickListener {
			val text = vb.edit.text.toString()
			if (text.trim().isNotEmpty() && vb.editLayout.error == null) {
				onEditionComplete(
					key,
					StringEntity(text, origin.position)
				)
				dismiss()
			}
		}

		vb.cancel.setOnClickListener { dismiss() }
	}
}