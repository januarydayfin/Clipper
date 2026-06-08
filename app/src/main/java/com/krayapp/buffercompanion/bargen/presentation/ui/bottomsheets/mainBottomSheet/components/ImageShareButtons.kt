package com.krayapp.buffercompanion.bargen.presentation.ui.bottomsheets.mainBottomSheet.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.krayapp.buffercompanion.bargen.R
import com.krayapp.buffercompanion.bargen.theme.mSize

@Composable
fun ImageShareButtons(
    onSharePicture: () -> Unit = {},
    onSaveStoragePicture: () -> Unit = {},
    onFullScreen: () -> Unit = {},
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center
    ) {
        OutlinedIconButton(
            onClick = { onSaveStoragePicture() },
            textRes = R.string.save,
            iconRes = R.drawable.ic_download
        )

        Spacer(Modifier.width(mSize))

        OutlinedIconButton(
            onClick = { onSharePicture() },
            textRes = R.string.share,
            iconRes = R.drawable.ic_share
        )

        Spacer(Modifier.width(mSize))

        FilledTonalIconButton(onClick = onFullScreen) {
            Icon(
                painter = painterResource(R.drawable.ic_landscape),
                contentDescription = stringResource(R.string.landscape_view)
            )
        }
    }
}

@Composable
private fun OutlinedIconButton(onClick: () -> Unit, textRes: Int, iconRes: Int) {
    OutlinedButton(shape = CircleShape, onClick = {
        onClick()
    }) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                modifier = Modifier,
                painter = painterResource(iconRes),
                contentDescription = stringResource(textRes),
            )
        }
    }
}