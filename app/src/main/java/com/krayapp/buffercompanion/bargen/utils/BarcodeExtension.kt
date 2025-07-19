package com.krayapp.buffercompanion.bargen.utils

import android.animation.Animator
import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.view.View
import android.view.ViewPropertyAnimator
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipDrawable
import com.google.android.material.color.MaterialColors
import com.krayapp.buffercompanion.bargen.R
import com.krayapp.buffercompanion.bargen.data.room.bargen.BargenDB
import com.krayapp.buffercompanion.bargen.data.room.bargen.entity.BarcodeEntity
import com.krayapp.buffercompanion.bargen.data.room.bargen.entity.TagEntity
import com.krayapp.buffercompanion.bargen.ui.models.BarcodeUiModel
import com.krayapp.buffercompanion.bargen.ui.models.TagUiModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat

suspend fun BarcodeEntity.toBarcodeUiModel(cachedTags: List<TagEntity>? = null): BarcodeUiModel =
    withContext(Dispatchers.IO) {
        val tagsModel = mutableListOf<TagUiModel>()
        val allTags = cachedTags ?: provideDatabase<BargenDB>().tagsDao().getTags()

        allTags
            .filter { this@toBarcodeUiModel.tags.contains(it.id) }
            .forEach { tagsModel.add(it.toTagUiModel()) }

        BarcodeUiModel(
            id = this@toBarcodeUiModel.id,
            name = this@toBarcodeUiModel.name,
            description = this@toBarcodeUiModel.description,
            barcodeType = this@toBarcodeUiModel.type,
            tags = tagsModel,
            content = this@toBarcodeUiModel.content
        )
    }

fun BottomSheetDialogFragment.colorNavBar(
    color: Int = MaterialColors.getColor(
        requireContext(),
        R.attr.bottomSheetBottomColor,
        Color.BLACK
    )
) {
    dialog?.window?.navigationBarColor = color
}

fun TagEntity.toTagUiModel() =
    TagUiModel(
        id = this.id,
        backgroundColor = this.backgroundColor,
        fontColor = fontColor,
        name = this.name
    )

fun TagUiModel.toEntity() =
    TagEntity(
        id = this.id,
        name = this.name,
        backgroundColor = this.backgroundColor,
        fontColor = this.fontColor
    )

fun Long.toReadableTime() =
    SimpleDateFormat("dd MMMM yyyy HH:mm:ss").format(this)


fun Context.filterChip(tagUiModel: TagUiModel, canChecked: Boolean = true): Chip {
    val drawable = ChipDrawable.createFromAttributes(this, null, 0, filterChipStyle)


    return Chip(this).apply {
        val backgroundColor = tagUiModel.backgroundColor.toColorStateList()
        val fontColor = tagUiModel.fontColor.toColorStateList()

        setChipDrawable(drawable)
        text = tagUiModel.name
        chipBackgroundColor = backgroundColor

        if (fontColor != null)
            setTextColor(fontColor)

        checkedIconTint = fontColor
        isCheckable = canChecked
        chipStrokeColor = backgroundColor
        isChecked = tagUiModel.checked
    }
}

private fun Int?.toColorStateList() =
    if (this == null)
        null
    else {
        ColorStateList.valueOf(this)
    }

val filterChipStyle: Int
    get() = com.google.android.material.R.style.Widget_Material3_Chip_Filter

fun View.attachHidingWithRecycler(
    recyclerView: RecyclerView,
    inverted: Boolean,
    translationToHide: Dp = 16.dp,
) {
    var isAnimating = false
    val startAnimator: () -> ViewPropertyAnimator? = {
        if (isAnimating)
            null
        else
            animate()
    }


    animate().setListener(object : Animator.AnimatorListener {
        override fun onAnimationStart(animation: Animator) {
            isAnimating = true
        }

        override fun onAnimationEnd(animation: Animator) {
            isAnimating = false
        }

        override fun onAnimationCancel(animation: Animator) {
            isAnimating = false
        }

        override fun onAnimationRepeat(animation: Animator) {
        }
    })


    recyclerView.addOnScrollListener(BasicScrollWatcher(true) {
        val show =
            it == (if (inverted) ScrollDirection.DOWN else ScrollDirection.UP)

        val translation = if (show) 0f else translationToHide.value


        if (show) isVisible = true
        startAnimator()
            ?.translationY(translation)
            ?.setDuration(100)
            ?.alpha(if (show) 1f else 0f)
            ?.setDuration(200)
            ?.withEndAction { if (!show) isVisible = false }

        isClickable = show
    })
}