package com.krayapp.buffercompanion.bargen.ui.screens

import androidx.compose.animation.core.animateIntAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.krayapp.buffercompanion.bargen.R
import com.krayapp.buffercompanion.bargen.testBarcodeUiModel
import com.krayapp.buffercompanion.bargen.theme.defaultAnimationDuration
import com.krayapp.buffercompanion.bargen.theme.lSize
import com.krayapp.buffercompanion.bargen.theme.mSize
import com.krayapp.buffercompanion.bargen.theme.sSize
import com.krayapp.buffercompanion.bargen.ui.adapter.BarcodeCard
import com.krayapp.buffercompanion.bargen.ui.models.BarcodeUiModel
import com.krayapp.buffercompanion.bargen.ui.mvi.BargenViewModel
import com.krayapp.buffercompanion.bargen.ui.mvi.MainIntent
import com.krayapp.buffercompanion.bargen.ui.screens.BottomButton.CREATE
import com.krayapp.buffercompanion.bargen.ui.screens.BottomButton.SCAN
import com.krayapp.buffercompanion.bargen.ui.screens.BottomButton.TAGS

@Preview(showBackground = true)
@Composable
fun MainScreen(
    data: List<BarcodeUiModel> = testBarcodeUiModel,
    onSearchTextChanged: (String) -> Unit = { }
) {
    val viewmodel: BargenViewModel = viewModel()
    val lazyListState = rememberLazyListState()
    Scaffold {
        Column(
            Modifier
                .padding(it)
                .fillMaxSize()
        ) {
            MainTopBar(onTextChanged = onSearchTextChanged)
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
                        count = data.size
                    ) { index ->
                        BarcodeCard(uiModel = data[index], onEditClick = {
                            viewmodel.onIntent(MainIntent.ShowBottomsheet(it))
                        })
                        Spacer(Modifier.height(mSize))
                    }
                }
                BottomButtonGroup(
                    modifier = Modifier.align(Alignment.BottomCenter),
                    hideState = lazyListState.lastScrolledForward,
                    onCreateClicked = {
                        viewmodel.onIntent(MainIntent.CreateNewBarcode)
                    }
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun MainTopBar(onTextChanged: (String) -> Unit = {}) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Image(
            modifier = Modifier
                .padding(horizontal = sSize)
                .size(25.dp),
            imageVector = ImageVector.vectorResource(R.drawable.ic_settings),
            contentDescription = "settings_icon",
            colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onSurfaceVariant)
        )

        SearchBar(modifier = Modifier.weight(1f), onTextChanged = onTextChanged)

        Image(
            modifier = Modifier
                .padding(horizontal = sSize)
                .size(25.dp),
            imageVector = ImageVector.vectorResource(R.drawable.ic_sort),
            contentDescription = "sort_icon",
            colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onSurfaceVariant)
        )
    }
}

@Composable
private fun SearchBar(modifier: Modifier = Modifier, onTextChanged: (String) -> Unit = {}) {
    val textFieldState = rememberTextFieldState()
    onTextChanged(textFieldState.text.toString())
    Card(
        shape = RoundedCornerShape(size = 100.dp),
        modifier = modifier
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = lSize)
        ) {
            Image(
                modifier = Modifier.size(25.dp),
                imageVector = ImageVector.vectorResource(R.drawable.ic_search),
                contentDescription = "icon_search",
                colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onSurfaceVariant)
            )
            TextField(
                placeholder = { Text(stringResource(R.string.search)) },
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                state = textFieldState,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = mSize),
                colors = TextFieldDefaults.colors().copy(
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    disabledIndicatorColor = Color.Transparent,
                    errorIndicatorColor = Color.Transparent
                )
            )
        }
    }
}

@Preview()
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
        val buttons = BottomButton.entries.toList()

        buttons.forEach { buttonInfo ->
            val click = when (buttonInfo) {
                SCAN -> onScanClicked
                CREATE -> onCreateClicked
                TAGS -> onTagsClicked
            }

            Button(
                modifier = Modifier
                    .wrapContentWidth()
                    .padding(horizontal = 1.dp),
                onClick = {
                    click()
                }

            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        modifier = Modifier.padding(end = 4.dp),
                        imageVector = ImageVector.vectorResource(buttonInfo.drawableRes),
                        contentDescription = stringResource(buttonInfo.labelRes),
                    )
                    Text(
                        text = stringResource(buttonInfo.labelRes),
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        }
    }
}

enum class BottomButton(val labelRes: Int, val drawableRes: Int) {
    SCAN(labelRes = R.string.scan, drawableRes = R.drawable.qr_example),
    CREATE(labelRes = R.string.create, drawableRes = R.drawable.ic_plus),
    TAGS(labelRes = R.string.tags, drawableRes = R.drawable.ic_label)
}