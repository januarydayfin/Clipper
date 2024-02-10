package com.krayapp.buffercompanion.ui.fragments.interfaces

import com.krayapp.buffercompanion.data.room.StringEntity

interface ListEditWatcher {
    fun onEditionStart()
    fun onEditionReset()
    fun onCopyClicked(entity: StringEntity)
    fun onRemoveClicked(entity: StringEntity)
    fun onEditClicked(entity: StringEntity)
    fun onCheckRemoveStart()
    fun onAdapterHasData(hasData: Boolean)
}