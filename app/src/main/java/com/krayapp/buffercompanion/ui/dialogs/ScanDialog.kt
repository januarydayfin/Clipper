package com.krayapp.buffercompanion.ui.dialogs

import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.lifecycleScope
import com.journeyapps.barcodescanner.Size
import com.krayapp.buffercompanion.bargenCore.BarReader
import com.krayapp.buffercompanion.bargenCore.reader.BargenReaderImpl
import com.krayapp.buffercompanion.databinding.DialogScannerLayoutBinding
import com.krayapp.buffercompanion.utils.displayWidth
import com.krayapp.buffercompanion.utils.rectangleParams
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class ScanDialog(private val onScanned: (String) -> Unit) : DialogFragment() {
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
        binding?.let { root ->
            with(root.scanner) {
                layoutParams = rectangleParams(context.displayWidth())
                barcodeView.framingRectSize = Size(context.displayWidth(), context.displayWidth())
            }
            reader = BargenReaderImpl(root.scanner)
            reader.startScan()


            lifecycleScope.launch {
                reader.readerFlow().collectLatest {
                    onScanned(it)
                }
            }
        }
    }
}