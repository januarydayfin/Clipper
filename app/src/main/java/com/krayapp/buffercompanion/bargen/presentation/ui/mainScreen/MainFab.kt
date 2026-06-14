package com.krayapp.buffercompanion.bargen.presentation.ui.mainScreen

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import com.krayapp.buffercompanion.bargen.R
import com.krayapp.buffercompanion.bargen.presentation.mvi.main.MainIntent
import com.krayapp.buffercompanion.bargen.presentation.ui.composables.SegmentButtonType
import com.krayapp.buffercompanion.bargen.presentation.ui.composables.SegmentFab
import com.krayapp.buffercompanion.bargen.presentation.utils.Space
import com.krayapp.buffercompanion.bargen.theme.defaultAnimationDuration
import com.krayapp.buffercompanion.bargen.theme.lSize
import com.krayapp.buffercompanion.bargen.theme.mSize
import com.krayapp.buffercompanion.bargen.theme.xsSize

@Composable
fun MainFab(
    modifier: Modifier = Modifier,
    hide: Boolean,
    toTopHide: Boolean,
    onImportFromGallery: () -> Unit,
    onScanClicked: () -> Unit = {},
    onCreateClicked: () -> Unit = {},
    onTagsClicked: () -> Unit = {},
    toTopClick: () -> Unit = {},
) {
    Column(horizontalAlignment = Alignment.End) {
        AnimatedVisibility(
            visible = !toTopHide,
            enter = fadeIn(tween(defaultAnimationDuration)) +
                    expandVertically(tween(defaultAnimationDuration), expandFrom = Alignment.Bottom),
            exit = fadeOut(tween(defaultAnimationDuration)) +
                    shrinkVertically(tween(defaultAnimationDuration), shrinkTowards = Alignment.Bottom),
        ) {
            FloatingActionButton(onClick = toTopClick) {
                Icon(
                    painter = painterResource(R.drawable.ic_arrow_up),
                    contentDescription = null,
                )
            }
        }

        AnimatedVisibility(
            visible = !hide,
            enter = fadeIn(tween(defaultAnimationDuration)) +
                    expandVertically(tween(defaultAnimationDuration), expandFrom = Alignment.Top),
            exit = fadeOut(tween(defaultAnimationDuration)) +
                    shrinkVertically(tween(defaultAnimationDuration), shrinkTowards = Alignment.Top),
        ) {
            Column(
                modifier.width(IntrinsicSize.Max),
                horizontalAlignment = Alignment.End
            ) {
                Space(mSize)
                FloatingActionButton(onClick = onTagsClicked) {
                    Row(
                        modifier = Modifier.padding(horizontal = lSize),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            modifier = Modifier.padding(end = xsSize),
                            painter = painterResource(R.drawable.ic_label),
                            contentDescription = stringResource(R.string.tags),
                        )
                        Text(
                            text = stringResource(R.string.tags),
                            style = typography.labelLarge
                        )
                    }
                }
                Space(mSize)
                SegmentFab(
                    items = listOf(
                        SegmentButtonType.FROM_GALLERY, SegmentButtonType.CREATE_RAW,
                        SegmentButtonType.SCAN
                    )
                ) {
                    when (it) {
                        SegmentButtonType.CREATE_RAW -> onCreateClicked()
                        SegmentButtonType.SCAN -> onScanClicked()
                        SegmentButtonType.FROM_GALLERY -> onImportFromGallery()
                    }
                }
            }
        }
    }
}
