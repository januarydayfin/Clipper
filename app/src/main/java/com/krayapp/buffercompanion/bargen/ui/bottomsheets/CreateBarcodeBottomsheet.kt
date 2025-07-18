package com.krayapp.buffercompanion.bargen.ui.bottomsheets

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout.LayoutParams
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.zxing.BarcodeFormat
import com.krayapp.buffercompanion.bargen.addTextWatcher
import com.krayapp.buffercompanion.bargen.bargenCore.BarGenerator
import com.krayapp.buffercompanion.bargen.bargenCore.generator.BarcodeGenerator
import com.krayapp.buffercompanion.bargen.databinding.BottomsheetCreateCodeBinding
import com.krayapp.buffercompanion.bargen.utils.displayWidth
import com.krayapp.buffercompanion.bargen.utils.runOnUi

class CreateBarcodeBottomsheet : BottomSheetDialogFragment() {
    private var vb: BottomsheetCreateCodeBinding? = null
    private var bitmapGenerator: BarGenerator? = null
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        vb = BottomsheetCreateCodeBinding.inflate(inflater)
        return vb?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        vb?.let { vb ->
            bitmapGenerator = BarcodeGenerator
            vb.edit.addTextWatcher { generatePreview(it, vb.preview) }
            vb.preview.layoutParams =
                LayoutParams(context.displayWidth(), context.displayWidth() / 2)
        }
    }

    private fun generatePreview(text: String, view: ImageView) {
        runCatching {
            runOnUi {
                val preview = bitmapGenerator?.generate(
                    content = text,
                    type = BarcodeFormat.EAN_13,
                    width = view.width,
                    height = view.height

                )
                view.setImageBitmap(preview)
            }
        }
    }
}