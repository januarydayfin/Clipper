package com.krayapp.buffercompanion.bargen.presentation.mvi.processing.pins

import android.util.Log
import com.krayapp.buffercompanion.bargen.data.room.entity.BarcodeEntity
import com.krayapp.buffercompanion.bargen.domain.usecase.barcode.PinnerBarcodeUsecase
import com.krayapp.buffercompanion.bargen.domain.usecase.barcode.UpdateBarcodeList
import com.krayapp.buffercompanion.bargen.presentation.mapper.toBarcodeUiModel
import com.krayapp.buffercompanion.bargen.presentation.models.BarcodeUiModel.Companion.AUTO_POSITION
import com.krayapp.buffercompanion.bargen.presentation.mvi.main.MainIntent
import com.krayapp.buffercompanion.bargen.presentation.mvi.main.MviState
import com.krayapp.buffercompanion.bargen.presentation.mvi.main.SideEffect
import org.koin.core.component.KoinComponent
import org.orbitmvi.orbit.ContainerHost

class PinHandler(
    private val host: ContainerHost<MviState, SideEffect>,
) : KoinComponent {

    suspend fun init() {
        refreshPinnedBarcodes()
    }

    suspend fun onIntent(intent: MainIntent.PinIntent) {
        when (intent) {
            is MainIntent.PinIntent.SwapBarcodes -> swapPinned(intent.from, intent.to)
            is MainIntent.PinIntent.PinBarcode -> pinBarcode(intent.id)
            is MainIntent.PinIntent.UnpinBarcode -> unpinBarcode(intent.id)
            MainIntent.PinIntent.SaveOrder -> savePinOrder()
        }
    }

    private suspend fun pinBarcode(id: String) {
        PinnerBarcodeUsecase.pinBarcode(id = id, position = AUTO_POSITION)
        refreshPinnedBarcodes()
    }

    private suspend fun unpinBarcode(id: String) {
        PinnerBarcodeUsecase.unpinBarcode(id = id)
        refreshPinnedBarcodes()
    }

    private fun swapPinned(from: Int, to: Int) {
        host.intent {
            reduce {
                val original = state.pinnedBarcodes
                val temp = original[from]
                val newList = original.toMutableList()
                newList[from] = original[to]
                newList[to] = temp

                state.copy(pinnedBarcodes = newList)
            }
        }
    }

    private suspend fun savePinOrder() {
        val currentPinnedOrder = host.container.stateFlow.value.pinnedBarcodes
        val pinnedOld = PinnerBarcodeUsecase.getPinnedBarcodes().associateBy { it.id }

        val replaced = mutableListOf<BarcodeEntity>()

        for (i in currentPinnedOrder.indices) {
            val newItem = pinnedOld[currentPinnedOrder[i].id]?.copy(pinnedPosition = i) ?: continue
            replaced.add(newItem)
        }
        UpdateBarcodeList(replaced)
    }

    suspend fun getAllPinnedBarcodes() = PinnerBarcodeUsecase.getPinnedBarcodes()

    suspend fun refreshPinnedBarcodes() {
        val pinnedBarcodes = PinnerBarcodeUsecase.getPinnedBarcodes().map { it.toBarcodeUiModel() }
        host.intent {
            reduce {
                state.copy(pinnedBarcodes = pinnedBarcodes)
            }
        }
    }
}