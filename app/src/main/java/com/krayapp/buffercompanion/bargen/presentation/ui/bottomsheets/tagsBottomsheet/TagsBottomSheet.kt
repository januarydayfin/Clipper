package com.krayapp.buffercompanion.bargen.presentation.ui.bottomsheets.tagsBottomsheet

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
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
import com.krayapp.buffercompanion.bargen.presentation.models.TagUiModel
import com.krayapp.buffercompanion.bargen.presentation.screens.mainScreen.SearchBar
import com.krayapp.buffercompanion.bargen.presentation.ui.composables.SheetDragger
import com.krayapp.buffercompanion.bargen.presentation.ui.dialogs.setupTagDialog.SetupTagDialog
import com.krayapp.buffercompanion.bargen.presentation.utils.BargenChip
import com.krayapp.buffercompanion.bargen.presentation.viewmodels.TagsViewModel
import com.krayapp.buffercompanion.bargen.theme.mSize
import com.krayapp.buffercompanion.bargen.utils.io
import com.krayapp.buffercompanion.bargen.utils.launchWithDelay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TagsBottomSheet(onDismiss: () -> Unit) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val viewmodel: TagsViewModel = viewModel()
    val tagsList = remember { mutableStateListOf<TagUiModel>() }
    val scope = rememberCoroutineScope()
    val selector = viewmodel.tagSelector
    val showEditDialog = remember { mutableStateOf<TagUiModel?>(null) }
    val filterQueryState = remember { mutableStateOf("") }

    fun refreshTags() {
        val checkedTags = viewmodel.tagSelector.tagsFilterFlow.value
        viewmodel.getTags(filterQueryState.value) {
            val newList = it.map { item ->
                item.copy(checked = item.id in checkedTags)
            }
            tagsList.clear()
            tagsList.addAll(newList)
        }
    }
    LaunchedEffect(Unit) {
        refreshTags()
    }
    if (showEditDialog.value != null) {
        val dialogModel = showEditDialog.value ?: return
        SetupTagDialog(dialogModel, onDismiss = {
            showEditDialog.value = null
        }, onComplete = { changed ->
            val position = tagsList.toList().indexOfFirst { it.id == dialogModel.id }
            tagsList[position] = changed
            viewmodel.saveTags(tagsList)
        }, onDeleteTag = {
            viewmodel.removeTagById(it.id)
            showEditDialog.value = null

            scope.launchWithDelay {
                refreshTags()
            }
        })
    }


    ModalBottomSheet(dragHandle = {
        SheetDragger()
    }, sheetState = sheetState, onDismissRequest = {
        onDismiss()
    }) {
        Column {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = mSize)
                    .verticalScroll(state = rememberScrollState())
            ) {
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

                                scope.io {
                                    selector.checkTag(tagsList[index].id)
                                }
                            }
                        )
                    }
                }
            }

            SearchBar(Modifier.padding(all = mSize)) {
                filterQueryState.value = it
                refreshTags()
            }
        }

    }
}