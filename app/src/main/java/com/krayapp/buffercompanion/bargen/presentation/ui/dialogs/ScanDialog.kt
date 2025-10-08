package com.krayapp.buffercompanion.bargen.presentation.ui.dialogs

import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.cardview.widget.CardView
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.lifecycleScope
import com.journeyapps.barcodescanner.BarcodeResult
import com.journeyapps.barcodescanner.DecoratedBarcodeView
import com.journeyapps.barcodescanner.Size
import com.krayapp.buffercompanion.bargen.ClipperApp
import com.krayapp.buffercompanion.bargen.R
import com.krayapp.buffercompanion.bargen.domain.bargenCore.BarReader
import com.krayapp.buffercompanion.bargen.presentation.models.TagUiModel
import com.krayapp.buffercompanion.bargen.presentation.models.setChecked
import com.krayapp.buffercompanion.bargen.presentation.mvi.MainIntent
import com.krayapp.buffercompanion.bargen.presentation.utils.BargenChip
import com.krayapp.buffercompanion.bargen.presentation.utils.Space
import com.krayapp.buffercompanion.bargen.presentation.viewmodels.BargenViewModel
import com.krayapp.buffercompanion.bargen.presentation.viewmodels.TagsViewModel
import com.krayapp.buffercompanion.bargen.theme.AppTheme
import com.krayapp.buffercompanion.bargen.theme.mSize
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class ScanDialog(
    private val viewModel: BargenViewModel,
    private val tagsViewModel: TagsViewModel,
    private val onScanned: (BarcodeResult?, List<String>) -> Unit
) : DialogFragment(), KoinComponent {
    private val reader: BarReader by inject()
    private val dialogWidth = (ClipperApp.displayWidth * 0.9).toInt()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = LinearLayout(context).apply { orientation = LinearLayout.VERTICAL }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        reader.pauseScan()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val linear = view as LinearLayout
        val scanner = DecoratedBarcodeView(linear.context).apply {
            barcodeView.framingRectSize = Size(dialogWidth, dialogWidth)
        }
        val card = CardView(linear.context).apply {
            layoutParams = LinearLayout.LayoutParams(dialogWidth, dialogWidth)
            addView(scanner)
        }

        val composeView = ComposeView(linear.context)
        linear.addView(card)
        linear.addView(composeView)

        dialog?.setTransparent()

        reader.setView(scanner)
        reader.startScan()

        lifecycleScope.launch {
            reader.readerFlow().collectLatest {
                if (it?.text != null) {
                    onScanned(it, tagsViewModel.tagSelector.tagsFilterFlow.value)
                    dismiss()
                }
            }
        }

        composeView.setContent {
            AutoTagSection(viewmodel = tagsViewModel, mainViewModel = viewModel, barReader = reader)
        }
    }
}

private fun Dialog?.setTransparent() {
    this?.window?.setBackgroundDrawableResource(R.drawable.dialog_background)
}

@Composable
private fun AutoTagSection(
    mainViewModel: BargenViewModel,
    viewmodel: TagsViewModel,
    barReader: BarReader
) {
    AppTheme {
        val tagsState = viewmodel.tagSelector.tagsFilterFlow.collectAsState()
        val scope = rememberCoroutineScope()
        val tagsUiState = remember { mutableStateListOf<TagUiModel>() }
        val torchState = remember { mutableStateOf(false) }
        LaunchedEffect(Unit) {
            viewmodel.loadTagsUiModelsByIds(tagsState.value) { input ->
                tagsUiState.clear()
                tagsUiState.addAll(input)
            }
        }

        viewmodel.loadTagsUiModelsByIds(tagsState.value) { input ->
            tagsUiState.clear()
            tagsUiState.addAll(input)
        }

        Surface {
            Column(
                Modifier
                    .fillMaxWidth()
                    .padding(vertical = mSize),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Space(height = mSize)
                Text(
                    text = stringResource(R.string.label_to_scans),
                    style = MaterialTheme.typography.labelMedium
                )

                FlowRow(
                    Modifier
                        .fillMaxWidth()
                        .padding(horizontal = mSize)
                ) {
                    tagsUiState.forEach {
                        BargenChip(model = it.setChecked(), onClick = {
                            scope.launch {
                                viewmodel.tagSelector.checkTag(it.id)
                            }
                        })
                    }
                }
                Space(height = mSize)
                Row {
                    Button(onClick = {
                        mainViewModel.onIntent(MainIntent.ShowTagsMenu)
                    }) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                modifier = Modifier.padding(end = 4.dp),
                                imageVector = ImageVector.vectorResource(R.drawable.ic_label),
                                contentDescription = stringResource(R.string.tags),
                            )
                            Text(
                                text = stringResource(R.string.tags),
                            )
                        }
                    }
                    Space(width = mSize)
                    Button(onClick = {
                        if (torchState.value)
                            barReader.torchOff()
                        else
                            barReader.torchOn()

                        torchState.value = !torchState.value
                    })
                    {
                        if (torchState.value)
                            Icon(
                                imageVector = ImageVector.vectorResource(R.drawable.baseline_flashlight_off_24),
                                contentDescription = null
                            )
                        else
                            Icon(
                                imageVector = ImageVector.vectorResource(R.drawable.baseline_flashlight_on_24),
                                contentDescription = null
                            )
                    }
                }

            }
        }
    }
}