package com.krayapp.buffercompanion.bargen.presentation.screens.mainScreen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemKey
import com.krayapp.buffercompanion.bargen.presentation.utils.BarcodeCard
import com.krayapp.buffercompanion.bargen.presentation.mvi.MainIntent
import com.krayapp.buffercompanion.bargen.presentation.viewmodels.BargenViewModel
import com.krayapp.buffercompanion.bargen.theme.mSize
import com.krayapp.buffercompanion.bargen.presentation.utils.Space
import com.krayapp.buffercompanion.bargen.utils.io

@Composable
fun MainScreen(
    onScanClicked: () -> Unit
) {
    val viewmodel: BargenViewModel = viewModel()
    val cardSelector = viewmodel.cardSelector
    val selectedIdsState = cardSelector.selectedBarcodes.collectAsState()

    val selectionMode = remember {
        derivedStateOf {
            selectedIdsState.value.isNotEmpty()
        }
    }
    val lazyItems = viewmodel.barcodePagingData.collectAsLazyPagingItems()
    val lazyListState = rememberLazyListState()
    val scope = rememberCoroutineScope()
    Scaffold {
        Column(
            Modifier
                .padding(it)
                .fillMaxSize()
        ) {
            MainTopBar(
                inSelectionMode = selectionMode,
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
                        key = lazyItems.itemKey { item -> item.toString() }
                    ) { index ->
                        val item = lazyItems[index]
                        if (item != null) {
                            BarcodeCard(
                                inSelectionMode = selectionMode.value,
                                isCheckedForDeletion = item.id in selectedIdsState.value,
                                uiModel = item,
                                onSelectClick = {
                                    scope.io {
                                        cardSelector.checkBarcodeForSelection(item.id)
                                    }
                                },
                                onCardClick = {
                                    if (selectionMode.value)
                                        scope.io {
                                            cardSelector.checkBarcodeForSelection(item.id)
                                        }
                                    else
                                        viewmodel.onIntent(MainIntent.ShowBottomsheet(item))
                                },
                                onDeleteClicked = { id ->
                                    viewmodel.deleteBarcodes(id)
                                })
                            Space(height = mSize)
                        }
                    }
                }
                BottomButtonGroup(
                    modifier = Modifier.align(Alignment.BottomCenter),
                    hideState = lazyListState.lastScrolledForward,
                    onCreateClicked = {
                        viewmodel.onIntent(MainIntent.CreateNewBarcode)
                    },
                    onTagsClicked = { viewmodel.onIntent(MainIntent.ShowTagsMenu) },
                    onScanClicked = onScanClicked
                )
            }
        }
    }
}




