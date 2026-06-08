package com.krayapp.buffercompanion.bargen.presentation.ui.bottomsheets.mainBottomSheet.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.krayapp.buffercompanion.bargen.R
import com.krayapp.buffercompanion.bargen.presentation.utils.Space
import com.krayapp.buffercompanion.bargen.theme.lSize
import com.krayapp.buffercompanion.bargen.theme.mRoundedCornerShape

@Composable
fun BottomButtons(
    modifier: Modifier = Modifier,
    onTagsClick: () -> Unit,
    onEditClicked: () -> Unit,
    onShareClicked: () -> Unit,
    onSaveClicked: () -> Unit
) {
    Row(modifier = modifier, horizontalArrangement = Arrangement.Center, verticalAlignment = Alignment.CenterVertically) {
        CircleOutlineButton(res = R.drawable.ic_download, onClick = onSaveClicked)
        Space(lSize)
        CircleOutlineButton(res = R.drawable.ic_share, onClick = onShareClicked)
        Space(lSize)
        Box(modifier = Modifier.height(16.dp).width(1.dp).background(colorScheme.onSurface.copy(alpha = 0.2f), shape = mRoundedCornerShape)) { }
        Space(lSize)
        CircleButton(res = R.drawable.ic_label, onClick = onTagsClick)
        Space(lSize)
        CircleButton(res = R.drawable.ic_edit, onClick = onEditClicked)

    }
}

@Composable
fun CircleOutlineButton(res: Int, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .size(50.dp)
            .clip(CircleShape)
            .border(1.dp, shape = CircleShape, color = colorScheme.onSurface)
            .clickable {
                onClick()
            },
        contentAlignment = Alignment.Center
    ) {
        Icon(
            painter = painterResource(res),
            contentDescription = null
        )
    }
}

@Composable
private fun CircleButton(res: Int, onClick: () -> Unit) {
    IconButton(
        modifier = Modifier.size(50.dp),
        onClick = onClick,
        shape = CircleShape,
        colors = IconButtonDefaults.iconButtonColors().copy(
            containerColor = colorScheme.primary,
            contentColor = colorScheme.onPrimary
        )
    ) {
        Icon(
            painter = painterResource(res),
            contentDescription = null
        )
    }
}