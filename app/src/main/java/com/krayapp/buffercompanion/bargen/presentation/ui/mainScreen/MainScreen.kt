package com.krayapp.buffercompanion.bargen.presentation.ui.mainScreen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.zIndex
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemKey
import com.krayapp.buffercompanion.bargen.data.room.MAX_PINNED_COUNT
import com.krayapp.buffercompanion.bargen.data.room.TOP_BADGE_HIDE_BORDER
import com.krayapp.buffercompanion.bargen.presentation.models.BarcodeUiModel
import com.krayapp.buffercompanion.bargen.presentation.mvi.main.MainIntent
import com.krayapp.buffercompanion.bargen.presentation.ui.composables.HorizontalDivider
import com.krayapp.buffercompanion.bargen.presentation.ui.composables.ToTopBadge
import com.krayapp.buffercompanion.bargen.presentation.utils.BarcodeCard
import com.krayapp.buffercompanion.bargen.presentation.utils.Space
import com.krayapp.buffercompanion.bargen.presentation.viewmodels.BargenViewModel
import com.krayapp.buffercompanion.bargen.theme.mSize
import com.krayapp.buffercompanion.bargen.theme.sSize
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel
import sh.calvin.reorderable.ReorderableItem
import sh.calvin.reorderable.rememberReorderableLazyListState

@Composable
fun MainScreen(
    onScanClicked: () -> Unit
) {
    val viewmodel: BargenViewModel = koinViewModel()
    val uiState by viewmodel.state.collectAsState()

    val scope = rememberCoroutineScope()
    val focus = LocalFocusManager.current
    val lazyItems = viewmodel.barcodePagingData.collectAsLazyPagingItems()
    val lazyListState = rememberLazyListState()
    val hideToTopBadge by remember {
        derivedStateOf {
            lazyListState.firstVisibleItemIndex < TOP_BADGE_HIDE_BORDER
        }
    }
    val reorderableLazyListState = rememberReorderableLazyListState(lazyListState) { from, to ->
        viewmodel.onIntent(MainIntent.PinIntent.SwapBarcodes(from = from.index, to = to.index))
    }

    @Composable
    fun Card(
        modifier: Modifier = Modifier,
        item: BarcodeUiModel,
        pinAvailable: Boolean,
        reorderModifier: Modifier = Modifier
    ) {
        BarcodeCard(
            modifier = modifier.padding(horizontal = mSize),
            inSelectionMode = uiState.inSelectionMode,
            isCheckedForDeletion = item.id in uiState.selectedBarcodesIds,
            uiModel = item,
            pinAvailable = pinAvailable,
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
            },
            onPin = {
                viewmodel.onIntent(MainIntent.PinIntent.PinBarcode(id = item.id))
            },
            onUnpin = {
                viewmodel.onIntent(MainIntent.PinIntent.UnpinBarcode(id = item.id))
            },
            reorderModifier = reorderModifier
        )
    }
    Surface {
        Column(
            Modifier
                .fillMaxSize()
        ) {
            MainTopBar(
                modifier = Modifier
                    .windowInsetsPadding(WindowInsets.statusBars)
                    .zIndex(2f)
                    .padding(top = sSize),
                inSelectionMode = uiState.inSelectionMode,
                onTextChanged = { text ->
                    viewmodel.updateNameFilter(text)
                },
                viewModel = viewmodel
            )
            SelectedFilterTags(modifier = Modifier.zIndex(2f))
            Box(
                modifier = Modifier.fillMaxSize(),
            ) {
                ToTopBadge(
                    modifier = Modifier
                        .zIndex(1f)
                        .padding(vertical = sSize), hide = hideToTopBadge
                ) {
                    scope.launch {
                        lazyListState.animateScrollToItem(0)
                    }
                }
                LazyColumn(
                    state = lazyListState,
                    modifier = Modifier
                        .wrapContentHeight()
                        .padding(top = mSize)
                ) {
                    items(
                        count = uiState.pinnedBarcodes.size,
                        key = { index -> uiState.pinnedBarcodes[index].id }
                    ) {
                        val uiItem = uiState.pinnedBarcodes[it]
                        ReorderableItem(
                            state = reorderableLazyListState, key = uiItem.id
                        ) {
                            Card(
                                item = uiItem,
                                pinAvailable = true,
                                reorderModifier = Modifier.draggableHandle(
                                    onDragStopped = {
                                        viewmodel.onIntent(MainIntent.PinIntent.SaveOrder)
                                    })
                            )
                        }
                        Space(height = mSize)
                    }

                    if (uiState.hasPinnedBarcodes)
                        item {
                            if (uiState.inSelectionMode)
                                Text(
                                    modifier = Modifier.fillMaxWidth(),
                                    textAlign = TextAlign.Center,
                                    text = "${uiState.pinnedBarcodes.size}/$MAX_PINNED_COUNT",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.surfaceContainerHighest
                                )
                            HorizontalDivider()
                            Space(height = mSize)
                        }
                    items(
                        count = lazyItems.itemCount,
                        key = lazyItems.itemKey { item -> item.hashCode() }
                    ) { index ->
                        val item = lazyItems[index]
                        if (item != null) {
                            Card(
                                item = item,
                                pinAvailable = uiState.pinnedBarcodes.size < MAX_PINNED_COUNT
                            )
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
