package com.krayapp.buffercompanion.bargen.presentation.screens.mainScreen

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.input.clearText
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.krayapp.buffercompanion.bargen.R
import com.krayapp.buffercompanion.bargen.presentation.dialogs.ConfirmationDialog
import com.krayapp.buffercompanion.bargen.presentation.menus.SortDropdownMenu
import com.krayapp.buffercompanion.bargen.presentation.mvi.MainIntent
import com.krayapp.buffercompanion.bargen.presentation.viewmodels.BargenViewModel
import com.krayapp.buffercompanion.bargen.theme.sSize
import com.krayapp.buffercompanion.bargen.utils.io
import com.krayapp.buffercompanion.bargen.utils.modifiers.onCombinedTapScreenOffset


@Composable
fun MainTopBar(
    inSelectionMode: State<Boolean>,
    viewModel: BargenViewModel,
    onTextChanged: (String) -> Unit = {}
) {
    val cardSelector = viewModel.cardSelector
    val scope = rememberCoroutineScope()
    if (inSelectionMode.value)
        SelectionTopBar(
            undoSelectionMode = {
                scope.io {
                    cardSelector.cleanSelection()
                }
            },
            delete = {
                scope.io {
                    cardSelector.deleteAllSelected {
                        viewModel.updatePager()
                    }
                }
            }
        )
    else
        BasicTopBar(viewModel = viewModel, onTextChanged = onTextChanged)
}

@Composable
private fun SelectionTopBar(
    undoSelectionMode: () -> Unit,
    delete: () -> Unit
) {

    val confirmationDialogShowState = remember { mutableStateOf(false) }

    if (confirmationDialogShowState.value)
        ConfirmationDialog(onDismiss = {
            confirmationDialogShowState.value = false
        }) {
            delete()
        }

    Row(modifier = Modifier.fillMaxWidth()) {

        TextButton(onClick = {
            undoSelectionMode()
        }) {
            Text(text = stringResource(R.string.cancel))
        }
        Spacer(modifier = Modifier.weight(1f))

        TextButton(onClick = {
            confirmationDialogShowState.value = true
        }) {
            Text(
                text = stringResource(R.string.delete),
                color = MaterialTheme.colorScheme.error
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
            imageVector = ImageVector.vectorResource(R.drawable.ic_settings),
            contentDescription = "settings_icon",
            colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onSurfaceVariant),
        )

        SearchBar(modifier = Modifier.weight(1f), onTextChanged = onTextChanged)

        Image(
            modifier = Modifier
                .padding(horizontal = sSize)
                .size(25.dp)
                .clip(CircleShape)
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
