package com.krayapp.buffercompanion.bargen.presentation.screens

import androidx.compose.animation.core.animateIntAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.input.clearText
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SplitButtonDefaults
import androidx.compose.material3.SplitButtonLayout
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemKey
import com.krayapp.buffercompanion.bargen.R
import com.krayapp.buffercompanion.bargen.presentation.BarcodeCard
import com.krayapp.buffercompanion.bargen.presentation.menus.SortDropdownMenu
import com.krayapp.buffercompanion.bargen.presentation.mvi.MainIntent
import com.krayapp.buffercompanion.bargen.presentation.viewmodels.BargenViewModel
import com.krayapp.buffercompanion.bargen.theme.defaultAnimationDuration
import com.krayapp.buffercompanion.bargen.theme.lSize
import com.krayapp.buffercompanion.bargen.theme.mSize
import com.krayapp.buffercompanion.bargen.theme.sSize
import com.krayapp.buffercompanion.bargen.utils.Space
import com.krayapp.buffercompanion.bargen.utils.modifiers.onCombinedTapScreenOffset

@Composable
fun MainScreen(
    onScanClicked: () -> Unit
) {
    val viewmodel: BargenViewModel = viewModel()
    val lazyItems = viewmodel.barcodePagingData.collectAsLazyPagingItems()
    val lazyListState = rememberLazyListState()

    Scaffold {
        Column(
            Modifier
                .padding(it)
                .fillMaxSize()
        ) {
            MainTopBar(
                onTextChanged = { text ->
                    viewmodel.updateNameFilter(text)
                },
                viewModel = viewmodel
            )
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
                        key = lazyItems.itemKey { item -> item.id }
                    ) { index ->
                        val item = lazyItems[index]
                        if (item != null) {
                            BarcodeCard(
                                uiModel = item,
                                onCardClick = { model ->
                                    viewmodel.onIntent(MainIntent.ShowBottomsheet(model))
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

@Composable
private fun MainTopBar(onTextChanged: (String) -> Unit = {}, viewModel: BargenViewModel) {
    val showSortMenu = remember { mutableStateOf<Offset?>(null) }

    if (showSortMenu.value != null)
        SortDropdownMenu(
            offset = showSortMenu.value!!,
            viewModel = viewModel()
        ) {
            showSortMenu.value = null
        }
    Row(
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Image(
            modifier = Modifier
                .padding(horizontal = sSize)
                .size(25.dp)
                .clickable {
                    viewModel.onIntent(MainIntent.ShowSettingsBottomsheet)
                },
            imageVector = ImageVector.vectorResource(R.drawable.ic_settings),
            contentDescription = "settings_icon",
            colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onSurfaceVariant),
        )

        SearchBar(modifier = Modifier.weight(1f), onTextChanged = onTextChanged)

        Image(
            modifier = Modifier
                .padding(horizontal = sSize)
                .size(25.dp)
                .onCombinedTapScreenOffset(onTap = {
                    showSortMenu.value = it
                }),
            imageVector = ImageVector.vectorResource(R.drawable.ic_sort),
            contentDescription = "sort_icon",
            colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onSurfaceVariant)
        )
    }
}

@Preview
@Composable
private fun SearchBar(modifier: Modifier = Modifier, onTextChanged: (String) -> Unit = {}) {
    val textFieldState = rememberTextFieldState()
    val focus = LocalFocusManager.current
    onTextChanged(textFieldState.text.toString())
    Card(
        shape = RoundedCornerShape(size = 100.dp),
        modifier = modifier
    ) {

        TextField(
            placeholder = { Text(stringResource(R.string.search)) },
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
            state = textFieldState,
            modifier = Modifier
                .fillMaxWidth(),
            colors = TextFieldDefaults.colors().copy(
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                disabledIndicatorColor = Color.Transparent,
                errorIndicatorColor = Color.Transparent
            ),
            leadingIcon = {
                Icon(
                    imageVector = ImageVector.vectorResource(R.drawable.ic_search),
                    contentDescription = "icon_search",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            },
            trailingIcon = {
                if (textFieldState.text.isNotEmpty())
                    Icon(
                        modifier = Modifier.clickable {
                            textFieldState.clearText()
                            focus.clearFocus()
                        },
                        imageVector = ImageVector.vectorResource(R.drawable.outline_cancel_24),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        contentDescription = null,
                    )
            }
        )
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun BottomButtonGroup(
    modifier: Modifier = Modifier,
    hideState: Boolean = false,
    onScanClicked: () -> Unit = {},
    onCreateClicked: () -> Unit = {},
    onTagsClicked: () -> Unit = {},
) {

    val offsetState = animateIntAsState(
        if (hideState) 200 else 0,
        animationSpec = tween(defaultAnimationDuration)
    )
    Row(
        horizontalArrangement = Arrangement.Center,
        modifier = modifier
            .wrapContentWidth()
            .offset {
                IntOffset(y = offsetState.value, x = 0)
            }
            .padding(bottom = mSize)
    ) {

        SplitButtonLayout(leadingButton = {
            SplitButtonDefaults.LeadingButton(onClick = onCreateClicked) {
                Icon(
                    ImageVector.vectorResource(R.drawable.ic_plus),
                    modifier = Modifier.size(SplitButtonDefaults.LeadingIconSize),
                    contentDescription = "Add new",
                )
                Spacer(Modifier.size(ButtonDefaults.IconSpacing))
                Text(stringResource(R.string.create))
            }
        }, trailingButton = {
            SplitButtonDefaults.TrailingButton(onClick = onScanClicked) {
                Icon(
                    imageVector = ImageVector.vectorResource(R.drawable.ic_camera),
                    modifier =
                        Modifier.size(SplitButtonDefaults.TrailingIconSize),
                    contentDescription = "Localized description",
                )
            }
        })

        Space(width = sSize)
        Button(
            modifier = Modifier
                .wrapContentWidth()
                .padding(horizontal = 1.dp),
            onClick = {
                onTagsClicked()
            }

        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    modifier = Modifier.padding(end = 4.dp),
                    imageVector = ImageVector.vectorResource(R.drawable.ic_label),
                    contentDescription = stringResource(R.string.tags),
                )
                Text(
                    text = stringResource(R.string.tags),
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}