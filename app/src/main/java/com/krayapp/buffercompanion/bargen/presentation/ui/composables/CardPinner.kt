package com.krayapp.buffercompanion.bargen.presentation.ui.composables

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.krayapp.buffercompanion.bargen.R
import com.krayapp.buffercompanion.bargen.theme.AppTheme
import com.krayapp.buffercompanion.bargen.theme.xxsSize


@Composable
fun CardPinner(modifier: Modifier = Modifier, pinned: Boolean, onClick: () -> Unit = {}) {
    Box(
        modifier = modifier
            .background(
                color = if (pinned) MaterialTheme.colorScheme.primary else Color.Transparent,
                shape = CircleShape
            )
            .padding(if (pinned) xxsSize else 0.dp)
            .clickable {
                onClick()
            }
    ) {
        Icon(
            painter = painterResource(if (pinned) R.drawable.keep_off else R.drawable.keep),
            contentDescription = null,
            tint = if (pinned) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.primary
        )
    }
}

@Composable
@Preview
private fun PinPreview() {
    AppTheme {
        Row {
            CardPinner(modifier = Modifier.size(25.dp), pinned = true)
            CardPinner(modifier = Modifier.size(25.dp), pinned = false)
        }
    }
}