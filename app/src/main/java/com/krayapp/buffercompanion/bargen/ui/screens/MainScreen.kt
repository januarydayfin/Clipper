package com.krayapp.buffercompanion.bargen.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.krayapp.buffercompanion.bargen.R
import com.krayapp.buffercompanion.bargen.theme.lPadding
import com.krayapp.buffercompanion.bargen.theme.mPadding
import com.krayapp.buffercompanion.bargen.theme.sPadding
import com.krayapp.buffercompanion.bargen.ui.screens.BottomButton.CREATE
import com.krayapp.buffercompanion.bargen.ui.screens.BottomButton.SCAN
import com.krayapp.buffercompanion.bargen.ui.screens.BottomButton.TAGS

@Preview(showBackground = true)
@Composable
fun MainScreen(onSearchTextChanged: (String) -> Unit = { }) {
    Scaffold {
        Column(Modifier.padding(it)) {
            MainTopBar(onTextChanged = onSearchTextChanged)
            Box(
                contentAlignment = Alignment.BottomCenter
            ) {
                LazyColumn(modifier = Modifier.fillMaxSize()) {

                }
                BottomButtonGroup(modifier = Modifier.align(Alignment.BottomCenter))
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
                .padding(horizontal = sPadding)
                .size(25.dp),
            imageVector = ImageVector.vectorResource(R.drawable.ic_settings),
            contentDescription = "settings_icon",
            colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onSurfaceVariant)
        )

        SearchBar(modifier = Modifier.weight(1f), onTextChanged = onTextChanged)

        Image(
            modifier = Modifier
                .padding(horizontal = sPadding)
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
            modifier = Modifier.padding(horizontal = lPadding)
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
                    .padding(start = mPadding),
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
    onScanClicked: () -> Unit = {},
    onCreateClicked: () -> Unit = {},
    onTagsClicked: () -> Unit = {},
) {
    Row(
        horizontalArrangement = Arrangement.Center,
        modifier = modifier
            .wrapContentWidth()
            .padding(bottom = mPadding)
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