package com.krayapp.buffercompanion.bargen.presentation.ui.bottomsheets.tagsBottomsheet

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.krayapp.buffercompanion.bargen.R
import com.krayapp.buffercompanion.bargen.presentation.models.TagUiModel
import com.krayapp.buffercompanion.bargen.presentation.mvi.tags.TagIntent
import com.krayapp.buffercompanion.bargen.presentation.ui.composables.SheetDragger
import com.krayapp.buffercompanion.bargen.presentation.ui.dialogs.setupTagDialog.SetupTagDialog
import com.krayapp.buffercompanion.bargen.presentation.ui.mainScreen.SearchBar
import com.krayapp.buffercompanion.bargen.presentation.ui.composables.BargenChip
import com.krayapp.buffercompanion.bargen.presentation.utils.colorizeBottomsheetNavBar
import com.krayapp.buffercompanion.bargen.presentation.viewmodels.TagsViewModel
import com.krayapp.buffercompanion.bargen.theme.mSize
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TagsBottomSheet(onDismiss: () -> Unit) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val viewmodel: TagsViewModel = koinViewModel()
    val showEditDialog = remember { mutableStateOf<TagUiModel?>(null) }
    val tagsState by viewmodel.state.collectAsState()

    if (showEditDialog.value != null) {
        val dialogModel = showEditDialog.value ?: return
        SetupTagDialog(dialogModel, onDismiss = {
            showEditDialog.value = null
        }, onComplete = { changed ->
            viewmodel.onIntent(TagIntent.SaveTag(listOf(changed)))
        }, onDeleteTag = {
            viewmodel.onIntent(TagIntent.UncheckTag(it.id))
            viewmodel.onIntent(TagIntent.DeleteTag(it.id))
            showEditDialog.value = null
        })
    }

    LaunchedEffect(Unit) {
        viewmodel.onIntent(TagIntent.Init)
    }

    ModalBottomSheet(dragHandle = {
        SheetDragger()
    }, sheetState = sheetState, onDismissRequest = {
        onDismiss()
    }) {
        colorizeBottomsheetNavBar()
        Column(
            modifier = Modifier
                .padding(horizontal = mSize)
                .weight(1f, fill = false)
                .verticalScroll(state = rememberScrollState())
        ) {
            Text(
                text = stringResource(R.string.tag_longtap_hint),
                style = MaterialTheme.typography.labelMedium
            )
            FlowRow(Modifier.wrapContentHeight()) {
                tagsState.list.forEach { tag ->
                    BargenChip(
                        model = tag,
                        onLongClick = {
                            showEditDialog.value = tag
                        },
                        onClick = {
                            viewmodel.onIntent(TagIntent.CheckTag(tag.id))
                        }
                    )
                }
            }


        }
        SearchBar(Modifier.padding(all = mSize)) {
            viewmodel.onIntent(TagIntent.FilterTags(it))
        }
    }

}