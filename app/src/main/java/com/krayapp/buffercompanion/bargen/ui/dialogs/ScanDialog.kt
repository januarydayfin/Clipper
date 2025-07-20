package com.krayapp.buffercompanion.bargen.ui.dialogs

import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.appcompat.content.res.AppCompatResources.getDrawable
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.lifecycleScope
import com.journeyapps.barcodescanner.BarcodeResult
import com.journeyapps.barcodescanner.Size
import com.krayapp.buffercompanion.bargen.R
import com.krayapp.buffercompanion.bargen.bargenCore.BarReader
import com.krayapp.buffercompanion.bargen.bargenCore.reader.BargenReaderImpl
import com.krayapp.buffercompanion.bargen.databinding.DialogScannerLayoutBinding
import com.krayapp.buffercompanion.bargen.utils.dialogWidth
import com.krayapp.buffercompanion.bargen.utils.setCustomBackground
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class ScanDialog(private val onScanned: (BarcodeResult?) -> Unit) : DialogFragment() {
    private var binding: DialogScannerLayoutBinding? = null
    private lateinit var reader: BarReader


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DialogScannerLayoutBinding.inflate(inflater)
        return binding?.root
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        reader.pauseScan()
        binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        dialog.setCustomBackground(R.drawable.dialog_background)
        binding?.run {
            root.layoutParams = FrameLayout.LayoutParams(dialogWidth, dialogWidth)
            scanner.barcodeView.framingRectSize = Size(dialogWidth, dialogWidth)

            reader = BargenReaderImpl(scanner)
            reader.startScan()

            lifecycleScope.launch {
                reader.readerFlow().collectLatest {

                    if (it?.text != null) {
                        onScanned(it)
                        dismiss()
                    }
                }
            }
        }
    }
}