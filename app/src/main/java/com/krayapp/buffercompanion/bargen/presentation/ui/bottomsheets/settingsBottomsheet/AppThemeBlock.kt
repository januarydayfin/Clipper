package com.krayapp.buffercompanion.bargen.presentation.ui.bottomsheets.settingsBottomsheet

import androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
import androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_NO
import androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_YES
import androidx.appcompat.app.AppCompatDelegate.setDefaultNightMode
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.krayapp.buffercompanion.bargen.R
import com.krayapp.buffercompanion.bargen.presentation.models.SettingsUiModel
import com.krayapp.buffercompanion.bargen.presentation.utils.Space
import com.krayapp.buffercompanion.bargen.presentation.viewmodels.SettingsViewModel
import com.krayapp.buffercompanion.bargen.theme.mSize
import com.krayapp.buffercompanion.bargen.theme.sSize

@Composable
fun AppThemeBlock(state: State<SettingsUiModel>, viewModel: SettingsViewModel) {
    val buttonModel = state.value.theme
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(all = mSize),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = stringResource(R.string.app_theme),
            style = MaterialTheme.typography.labelLarge
        )
        Space(height = sSize)
        SingleChoiceSegmentedButtonRow {
            AppTheme.entries.forEachIndexed { index, item ->
                SegmentedButton(
                    selected =
                        item.value == buttonModel,
                    shape = SegmentedButtonDefaults.itemShape(
                        index = index,
                        count = AppTheme.entries.size
                    ),
                    onClick = {
                        viewModel.updateTheme(item.value)
                        setDefaultNightMode(item.value)
                    },
                    label = {
                        Text(text = stringResource(item.displayName))
                    }
                )
            }
        }
    }
}
private enum class AppTheme(val value: Int, val displayName: Int) {
    DARK(MODE_NIGHT_YES, R.string.dark_theme),
    SYSTEM(
        MODE_NIGHT_FOLLOW_SYSTEM,
        R.string.system_theme
    ),
    LIGHT(MODE_NIGHT_NO, R.string.light_theme)
}