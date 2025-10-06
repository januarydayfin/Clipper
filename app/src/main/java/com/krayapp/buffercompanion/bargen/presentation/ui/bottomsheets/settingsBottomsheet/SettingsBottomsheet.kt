package com.krayapp.buffercompanion.bargen.presentation.ui.bottomsheets.settingsBottomsheet

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.krayapp.buffercompanion.bargen.presentation.utils.Space
import com.krayapp.buffercompanion.bargen.presentation.viewmodels.SettingsViewModel
import com.krayapp.buffercompanion.bargen.theme.mSize

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsBottomSheet(
    onDismiss: () -> Unit,
) {
    val viewmodel: SettingsViewModel = viewModel()

    val state = viewmodel.settingsState.collectAsState()

    ModalBottomSheet(
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        onDismissRequest = {
            onDismiss()
        }) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(all = mSize),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            AppThemeBlock(state, viewmodel)
            Space(height = mSize)
            CheckboxSection(state, viewmodel)
        }
    }
}