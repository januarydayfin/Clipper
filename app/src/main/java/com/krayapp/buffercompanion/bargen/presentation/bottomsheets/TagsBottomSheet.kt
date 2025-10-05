package com.krayapp.buffercompanion.bargen.presentation.bottomsheets

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.viewmodel.compose.viewModel
import com.krayapp.buffercompanion.bargen.R
import com.krayapp.buffercompanion.bargen.theme.mSize
import com.krayapp.buffercompanion.bargen.presentation.BargenChip
import com.krayapp.buffercompanion.bargen.presentation.dialogs.SetupTagDialog
import com.krayapp.buffercompanion.bargen.presentation.uiModels.TagUiModel
import com.krayapp.buffercompanion.bargen.presentation.viewmodels.TagsViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TagsBottomSheet(onDismiss: () -> Unit) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val viewmodel: TagsViewModel = viewModel()
    val tagsList = remember { mutableStateListOf<TagUiModel>() }
    val scope = rememberCoroutineScope()

    val showEditDialog = remember { mutableStateOf<TagUiModel?>(null) }

    LaunchedEffect(true) {
        viewmodel.getTags {
            tagsList.addAll(it)
        }
    }

    if (showEditDialog.value != null) {
        val dialogModel = showEditDialog.value ?: return
        SetupTagDialog(dialogModel, onDismiss = {
            showEditDialog.value = null
        }) { changed ->
            val position = tagsList.toList().indexOfFirst { it.id == dialogModel.id }
            tagsList[position] = changed
            viewmodel.saveTags(tagsList)
        }
    }


    ModalBottomSheet(sheetState = sheetState, onDismissRequest = {
        onDismiss()
    }) {
        Column(modifier = Modifier.padding(horizontal = mSize)) {
            Text(
                text = stringResource(R.string.tag_longtap_hint),
                style = MaterialTheme.typography.labelMedium
            )
            FlowRow {
                tagsList.forEachIndexed { index, _ ->
                    BargenChip(
                        model = tagsList[index],
                        onLongClick = {
                            showEditDialog.value = tagsList[index]
                        },
                        onClick = {
                            tagsList[index] =
                                tagsList[index].copy(checked = !tagsList[index].checked)
                        }
                    )
                }
            }

            FilledTonalButton(modifier = Modifier.fillMaxWidth(), onClick = {
                scope.launch {
                    sheetState.hide()
                }
            }) {
                Text(text = stringResource(R.string.close))
            }
        }

    }

}