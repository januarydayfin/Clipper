package com.krayapp.buffercompanion.bargen.presentation.ui.mainScreen

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemKey
import com.google.zxing.BarcodeFormat
import com.krayapp.buffercompanion.bargen.R
import com.krayapp.buffercompanion.bargen.data.room.MAX_PINNED_COUNT
import com.krayapp.buffercompanion.bargen.data.room.TOP_BADGE_HIDE_BORDER
import com.krayapp.buffercompanion.bargen.presentation.models.BarcodeUiModel
import com.krayapp.buffercompanion.bargen.presentation.mvi.main.MainIntent
import com.krayapp.buffercompanion.bargen.presentation.ui.barcodeCard.BarcodeCard
import com.krayapp.buffercompanion.bargen.presentation.ui.composables.HorizontalDivider
import com.krayapp.buffercompanion.bargen.presentation.ui.composables.ToTopBadge
import com.krayapp.buffercompanion.bargen.presentation.utils.ContentFromUriImage
import com.krayapp.buffercompanion.bargen.presentation.utils.rememberImagePicker
import com.krayapp.buffercompanion.bargen.presentation.viewmodels.BargenViewModel
import com.krayapp.buffercompanion.bargen.theme.lSize
import com.krayapp.buffercompanion.bargen.theme.mSize
import com.krayapp.buffercompanion.bargen.theme.sSize
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel
import sh.calvin.reorderable.ReorderableItem
import sh.calvin.reorderable.rememberReorderableLazyListState

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun MainScreen(
    onScanClicked: () -> Unit,
) {
    val viewmodel: BargenViewModel = koinViewModel()
    val uiState by viewmodel.state.collectAsState()

    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val focus = LocalFocusManager.current
    val lazyItems = viewmodel.barcodePagingData.collectAsLazyPagingItems()
    //используется для адекватного перетаскивания
    var localPinned by remember { mutableStateOf(emptyList<BarcodeUiModel>()) }
    val lazyListState = rememberLazyListState()

    val botButtonsHideState by remember(
        lazyListState.canScrollForward,
        lazyListState.lastScrolledForward
    ) {
        mutableStateOf(!lazyListState.canScrollForward || lazyListState.lastScrolledForward)
    }
    val haptic = LocalHapticFeedback.current
    val reorderableLazyListState = rememberReorderableLazyListState(lazyListState) { from, to ->
        localPinned = localPinned.toMutableList().apply {
            add(to.index, removeAt(from.index))
        }
        haptic.performHapticFeedback(HapticFeedbackType.ContextClick)
    }

    var snackbarHost by remember { mutableStateOf(SnackbarHostState()) }

    val imageLauncher = rememberImagePicker {
        it?.run {
            scope.launch {
                val result = ContentFromUriImage(it)
                if (result == null) {
                    snackbarHost.showSnackbar(context.getString(R.string.failed_scan))
                    return@launch
                }
                viewmodel.onIntent(
                    MainIntent.CreateNewRecord(
                        text = result.first,
                        format = BarcodeFormat.valueOf(result.second),
                        tagIds = emptyList(),
                        showAfterCreate = true
                    )
                )
            }
        }
    }

    LaunchedEffect(uiState.pinnedBarcodes) {
        localPinned = uiState.pinnedBarcodes
    }

    LaunchedEffect(uiState.inSelectionMode) {
        if (!uiState.inSelectionMode) {
            viewmodel.onIntent(MainIntent.PinIntent.SaveOrder(localPinned))
        }
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
            swipeToDeleteAvailable = uiState.swipeToDelete,
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
                viewmodel.onIntent(MainIntent.DeleteBarcode(id))
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
    Scaffold(snackbarHost = {
        SnackbarHost(modifier = Modifier.padding(bottom = lSize * 2), hostState = snackbarHost)
    }) {
        it
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
                }
            )
            SelectedFilterTags(modifier = Modifier.zIndex(2f))
            Box(
                modifier = Modifier.fillMaxSize(),
            ) {
                val hideToTopBadge by remember {
                    derivedStateOf {
                        lazyListState.firstVisibleItemIndex < TOP_BADGE_HIDE_BORDER
                    }
                }

                ToTopBadge(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .zIndex(1f)
                        .padding(bottom = 120.dp, end = mSize), hide = hideToTopBadge
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
                    if (uiState.searchMode.not()) {
                        items(
                            count = localPinned.size,
                            key = { index -> localPinned[index].id }
                        ) {
                            val uiItem = localPinned[it]
                            ReorderableItem(
                                state = reorderableLazyListState, key = uiItem.id
                            ) {
                                val interactionSource = remember { MutableInteractionSource() }

                                Card(
                                    item = uiItem,
                                    pinAvailable = true,
                                    reorderModifier = Modifier.draggableHandle(interactionSource = interactionSource),
                                )
                            }
                            Spacer(modifier = Modifier.height(mSize))
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
                                Spacer(modifier = Modifier.height(mSize))
                            }
                    }
                    items(
                        count = lazyItems.itemCount,
                        key = lazyItems.itemKey { item -> item.hashCode() }
                    ) { index ->
                        val item = lazyItems[index]
                        if (item != null) {
                            Card(
                                item = item,
                                pinAvailable = uiState.pinnedBarcodes.size < MAX_PINNED_COUNT,
                            )
                            Spacer(modifier = Modifier.height(mSize))
                        }
                    }

                    item {
                        Spacer(modifier = Modifier.padding(WindowInsets.navigationBars.asPaddingValues()))
                    }
                }
                if (!uiState.inSelectionMode) {
                    BottomButtonGroup(
                        modifier = Modifier
                            .align(Alignment.BottomCenter),
                        hide = botButtonsHideState,
                        onCreateClicked = {
                            viewmodel.onIntent(MainIntent.ShowEmptyMainBottomSheet)
                        },
                        onTagsClicked = { viewmodel.onIntent(MainIntent.ShowTagsMenu) },
                        onScanClicked = onScanClicked,
                        onImportFromGallery = {
                            imageLauncher()
                        }
                    )
                }
            }
        }
    }
}
