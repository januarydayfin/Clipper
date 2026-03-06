package com.krayapp.buffercompanion.bargen.presentation.ui.mainScreen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemKey
import com.krayapp.buffercompanion.bargen.presentation.mvi.main.MainIntent
import com.krayapp.buffercompanion.bargen.presentation.utils.BarcodeCard
import com.krayapp.buffercompanion.bargen.presentation.utils.Space
import com.krayapp.buffercompanion.bargen.presentation.viewmodels.BargenViewModel
import com.krayapp.buffercompanion.bargen.theme.mSize
import com.krayapp.buffercompanion.bargen.theme.sSize

@Composable
fun MainScreen(
    onScanClicked: () -> Unit
) {
    val viewmodel: BargenViewModel = viewModel()
    val uiState by viewmodel.state.collectAsState()

    val focus = LocalFocusManager.current
    val lazyItems = viewmodel.barcodePagingData.collectAsLazyPagingItems()
    val lazyListState = rememberLazyListState()

//    val reorderableLazyListState = rememberReorderableLazyListState(lazyListState) { from, to ->
//        list = list.toMutableList().apply {
//            add(to.index, removeAt(from.index))
//        }
//
//        hapticFeedback.performHapticFeedback(HapticFeedbackType.SegmentFrequentTick)
//    }


    Surface {
        Column(
            Modifier
                .fillMaxSize()
        ) {
            MainTopBar(
                modifier = Modifier.windowInsetsPadding(WindowInsets.statusBars).padding(top = sSize),
                inSelectionMode = uiState.inSelectionMode,
                onTextChanged = { text ->
                    viewmodel.updateNameFilter(text)
                },
                viewModel = viewmodel
            )
            SelectedFilterTags()
            Box(
                modifier = Modifier.fillMaxSize(),
            ) {
                LazyColumn(
                    state = lazyListState,
                    modifier = Modifier
                        .wrapContentHeight()
                        .padding(top = mSize, start = mSize, end = mSize)
                ) {
                    items(
                        count = lazyItems.itemCount,
                        key = lazyItems.itemKey { item -> item.hashCode() }
                    ) { index ->
                        val item = lazyItems[index]
                        if (item != null) {
                            BarcodeCard(
                                inSelectionMode = uiState.inSelectionMode,
                                isCheckedForDeletion = item.id in uiState.selectedBarcodesIds,
                                uiModel = item,
                                onSelectClick = {
                                    viewmodel.onIntent(MainIntent.CheckBarcodeForSelection(item.id))
                                },
                                onCardClick = {
                                    focus.clearFocus(true)
                                    if (uiState.inSelectionMode)
                                        viewmodel.onIntent(MainIntent.CheckBarcodeForSelection(item.id))
                                    else
                                        viewmodel.onIntent(MainIntent.ShowExistCodeBottomsheet(item))
                                },
                                onDeleteClicked = { id ->
                                    viewmodel.deleteBarcodes(id)
                                })
                            Space(height = mSize)
                        }
                    }
                }
                BottomButtonGroup(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .windowInsetsPadding(WindowInsets.navigationBars),
                    hideState = lazyListState.lastScrolledForward,
                    onCreateClicked = {
                        viewmodel.onIntent(MainIntent.ShowEmptyMainBottomSheet)
                    },
                    onTagsClicked = { viewmodel.onIntent(MainIntent.ShowTagsMenu) },
                    onScanClicked = onScanClicked
                )
            }
        }
    }
}




