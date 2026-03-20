package com.krayapp.buffercompanion.bargen.presentation.ui.mainScreen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.krayapp.buffercompanion.bargen.R
import com.krayapp.buffercompanion.bargen.domain.selector.barcodeSelector.CardSelector
import com.krayapp.buffercompanion.bargen.presentation.mvi.main.MainIntent
import com.krayapp.buffercompanion.bargen.presentation.ui.dialogs.ConfirmationDialog
import com.krayapp.buffercompanion.bargen.presentation.ui.menus.SortDropdownMenu
import com.krayapp.buffercompanion.bargen.presentation.viewmodels.BargenViewModel
import com.krayapp.buffercompanion.bargen.theme.maxRoundedCornerShape
import com.krayapp.buffercompanion.bargen.theme.sSize
import com.krayapp.buffercompanion.bargen.theme.xsSize
import org.koin.androidx.compose.koinViewModel
import org.koin.compose.koinInject


@Composable
fun MainTopBar(
    modifier: Modifier = Modifier,
    inSelectionMode: Boolean,
    onTextChanged: (String) -> Unit = {}
) {
    val viewModel: BargenViewModel = koinViewModel()
    Box(modifier = modifier) {
        if (inSelectionMode)
            SelectionTopBar(
                undoSelectionMode = {
                    viewModel.onIntent(MainIntent.CleanCardSelection)
                },
                delete = {
                    viewModel.onIntent(MainIntent.DeleteAllSelectedCards)
                }
            )
        else
            BasicTopBar(viewModel = viewModel, onTextChanged = onTextChanged)
    }
}

@Composable
private fun SelectionTopBar(
    undoSelectionMode: () -> Unit,
    delete: () -> Unit
) {
    val selector: CardSelector = koinInject()
    val confirmationDialogShowState = remember { mutableStateOf(false) }

    val selectedBarcodeSize by selector.selectedBarcodes.collectAsState()
    if (confirmationDialogShowState.value)
        ConfirmationDialog(onDismiss = {
            confirmationDialogShowState.value = false
        }) {
            delete()
        }

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {

        TextButton(onClick = {
            undoSelectionMode()
        }) {
            Text(text = stringResource(R.string.cancel))
        }

        Box(
            modifier = Modifier.background(
                color = colorScheme.surfaceContainer,
                shape = maxRoundedCornerShape
            ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                modifier = Modifier.padding(horizontal = sSize, vertical = xsSize),
                text = "${stringResource(R.string.selected)}: ${selectedBarcodeSize.size}",
                style = typography.bodyMedium,
                color = colorScheme.onSurface
            )
        }


        TextButton(onClick = {
            confirmationDialogShowState.value = true
        }) {
            Text(
                text = stringResource(R.string.delete),
                color = colorScheme.error
            )
        }
    }
}

@Composable
private fun BasicTopBar(
    viewModel: BargenViewModel,
    onTextChanged: (String) -> Unit = {}
) {
    val showSortMenu = remember { mutableStateOf<Offset?>(null) }

    val context = LocalContext.current
    val displayMetrics = context.resources.displayMetrics
    val screenWidthPx = displayMetrics.widthPixels
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
                .clip(CircleShape)
                .clickable {
                    viewModel.onIntent(MainIntent.ShowSettingsBottomsheet)
                },
            painter = painterResource(R.drawable.ic_settings),
            contentDescription = "settings_icon",
            colorFilter = ColorFilter.tint(colorScheme.onSurfaceVariant),
        )

        SearchBar(
            modifier = Modifier.weight(1f),
            viewModel.currentFilterValue.searchFilter,
            onTextChanged = onTextChanged
        )

        Image(
            modifier = Modifier
                .padding(horizontal = sSize)
                .size(25.dp)
                .clip(CircleShape)
                .clickable {
                    showSortMenu.value = Offset(screenWidthPx.toFloat(), 0f)
                },
            painter = painterResource(R.drawable.ic_sort),
            contentDescription = "sort_icon",
            colorFilter = ColorFilter.tint(colorScheme.onSurfaceVariant)
        )
    }
}

@Composable
fun SearchBar(
    modifier: Modifier = Modifier,
    initialValue: String = "",
    onTextChanged: (String) -> Unit = {}
) {
    val focus = LocalFocusManager.current
    Card(
        shape = RoundedCornerShape(size = 100.dp),
        modifier = modifier
    ) {
        var text by remember { mutableStateOf(initialValue) }
        TextField(
            value = text,
            placeholder = { Text(stringResource(R.string.search)) },
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
            onValueChange = {
                text = it
                onTextChanged(text)
            },
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
                    painter = painterResource(R.drawable.ic_search),
                    contentDescription = "icon_search",
                    tint = colorScheme.onSurfaceVariant
                )
            },
            trailingIcon = {
                if (text.isNotEmpty())
                    Icon(
                        modifier = Modifier.clickable {
                            text = ""
                            focus.clearFocus()
                            onTextChanged("")
                        },
                        painter = painterResource(R.drawable.outline_cancel_24),
                        tint = colorScheme.onSurfaceVariant,
                        contentDescription = null,
                    )
            }
        )
    }
}
