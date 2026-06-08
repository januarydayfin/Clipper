package com.krayapp.buffercompanion.bargen.presentation.ui.bottomsheets.mainBottomSheet

import android.graphics.Bitmap
import androidx.compose.foundation.background
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.isImeVisible
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.krayapp.buffercompanion.bargen.R
import com.krayapp.buffercompanion.bargen.presentation.models.BarcodeUiModel
import com.krayapp.buffercompanion.bargen.presentation.ui.bottomsheets.mainBottomSheet.components.BottomButtons
import com.krayapp.buffercompanion.bargen.presentation.ui.bottomsheets.mainBottomSheet.components.ContentRow
import com.krayapp.buffercompanion.bargen.presentation.ui.bottomsheets.mainBottomSheet.components.TagRow
import com.krayapp.buffercompanion.bargen.presentation.ui.bottomsheets.mainBottomSheet.dialog.InfoEditDialog
import com.krayapp.buffercompanion.bargen.presentation.ui.bottomsheets.mainBottomSheet.dialog.TagEditDialog
import com.krayapp.buffercompanion.bargen.presentation.ui.dialogs.BarcodeFormatDialog
import com.krayapp.buffercompanion.bargen.presentation.ui.dialogs.BarcodeInfoDialog
import com.krayapp.buffercompanion.bargen.presentation.utils.Space
import com.krayapp.buffercompanion.bargen.presentation.utils.colorizeBottomsheetNavBar
import com.krayapp.buffercompanion.bargen.theme.mSize
import com.krayapp.buffercompanion.bargen.theme.sSize
import kotlinx.coroutines.launch


@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun MainBottomSheet(
    model: BarcodeUiModel,
    onDismiss: () -> Unit = {},
    onSharePicture: (String, Bitmap?) -> Unit = { _, _ -> },
    onSaveStoragePicture: (String, Bitmap?) -> Unit = { _, _ -> { } },
    onApplyBarcode: (BarcodeUiModel) -> Unit = {},
    autoOpenLandscape: Boolean = false,
    hideBsAfterLandscapeClose: Boolean = false,
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var modelState by remember { mutableStateOf(model) }
    var bitmap by remember { mutableStateOf<Bitmap?>(null) }
    var formatDialogOpened by remember { mutableStateOf(false) }
    var fullscreenDialogOpened by remember { mutableStateOf(false) }
    var tagDialogOpened by remember { mutableStateOf(false) }
    var infoEditDialogOpened by remember { mutableStateOf(false) }
    val isKeyboardVisible by rememberUpdatedState(WindowInsets.isImeVisible)
    val scope = rememberCoroutineScope()
    LaunchedEffect(Unit) {
        if (autoOpenLandscape) fullscreenDialogOpened = true
    }

    LaunchedEffect(isKeyboardVisible) {
        if (isKeyboardVisible)
            sheetState.expand()
    }

    fun dismiss() {
        scope.launch {
            sheetState.hide()
            onDismiss()
        }
    }
    if (fullscreenDialogOpened)
        BarcodeInfoDialog(
            model = modelState,
            onDismiss = {
                fullscreenDialogOpened = false
                if (hideBsAfterLandscapeClose) onDismiss()
            }
        )

    if (tagDialogOpened)
        TagEditDialog(uiModel = modelState, onApply = {
            modelState = it
        }, onDismiss = {
            tagDialogOpened = false
        })

    if (infoEditDialogOpened)
        InfoEditDialog(
            uiModel = modelState, onApply =
                { modelState = it }, onDismiss = {
                infoEditDialogOpened = false
            })

    if (formatDialogOpened)
        BarcodeFormatDialog(
            initialState = modelState.barcodeType,
            onPicked = {
                modelState = modelState.copy(barcodeType = it)
            }, onDismiss = {
                formatDialogOpened = false
            })

    ModalBottomSheet(
        dragHandle = {
            Box(Modifier.fillMaxWidth()) {
                Text(
                    modifier = Modifier
                        .align(Alignment.Center)
                        .width(250.dp)
                        .basicMarquee(),
                    text = modelState.name.takeIf { !it.isBlank() } ?: modelState.content,
                    maxLines = 1,
                    textAlign = TextAlign.Center,
                    style = typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = colorScheme.onSurface
                )

                Box(
                    modifier = Modifier
                        .padding(sSize)
                        .align(Alignment.CenterEnd)
                ) {
                    IconButton(
                        modifier = Modifier
                            .clip(CircleShape)
                            .background(color = colorScheme.surfaceContainerHighest)
                            .padding(sSize)
                            .size(30.dp),
                        onClick = { onApplyBarcode(modelState); dismiss() }) {
                        Icon(
                            painter = painterResource(R.drawable.outline_check_24),
                            tint = colorScheme.onSurface,
                            contentDescription = null
                        )
                    }
                }

            }
        },
        sheetState = sheetState,
        onDismissRequest = {
            onDismiss()
        }) {
        colorizeBottomsheetNavBar()

        BottomSheetContent(
            modifier = Modifier.padding(horizontal = mSize),
            uiModel = modelState,
            onSharePicture = { onSharePicture(modelState.filename, bitmap) },
            onSaveStoragePicture = { onSaveStoragePicture(modelState.filename, bitmap) },
            onModelUpdated = {
                modelState = it
            },
            onFullScreen = {
                fullscreenDialogOpened = true
            },
            onInfoEditClick = {
                infoEditDialogOpened = true
            },
            onBitmapGenerated = {
                bitmap = it
            },
            onTagsSettingsClicked = {
                tagDialogOpened = true
            },
            onOpenFormatDialog = {
                formatDialogOpened = true
            }
        )
    }
}

@Composable
private fun BottomSheetContent(
    modifier: Modifier = Modifier,
    uiModel: BarcodeUiModel,
    onModelUpdated: (BarcodeUiModel) -> Unit,
    onBitmapGenerated: (Bitmap) -> Unit,
    onSharePicture: () -> Unit,
    onSaveStoragePicture: () -> Unit,
    onTagsSettingsClicked: () -> Unit,
    onFullScreen: () -> Unit,
    onInfoEditClick: () -> Unit,
    onOpenFormatDialog: () -> Unit,
) {
    Column(modifier = modifier, horizontalAlignment = Alignment.CenterHorizontally) {

        LaunchedEffect(uiModel) {
            onModelUpdated(uiModel)
        }

        TagRow(uiModel.tags)



        Column(
            modifier = Modifier
                .weight(1f, false)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            ImageBlock(
                modifier = Modifier
                    .clickable {
                        onFullScreen()
                    },
                model = uiModel,
                onBitmapGenerated = onBitmapGenerated,
            )

            Space(sSize * 2)

            ContentRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight(),
                uiModel = uiModel
            ) {
                onModelUpdated(uiModel.copy(content = it))
            }


            Space(sSize * 2)

            BarcodeFormatBlock(uiModel) {
                onOpenFormatDialog()
            }

            Space(sSize)


            BottomButtons(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(IntrinsicSize.Min),
                onTagsClick = onTagsSettingsClicked,
                onEditClicked = onInfoEditClick,
                onSaveClicked = onSaveStoragePicture,
                onShareClicked = onSharePicture
            )
        }

    }
}



