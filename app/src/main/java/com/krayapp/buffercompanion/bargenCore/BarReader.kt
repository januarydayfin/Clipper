package com.krayapp.buffercompanion.bargenCore

import kotlinx.coroutines.flow.StateFlow

interface BarReader {
    fun readerFlow(): StateFlow<String>

    fun startScan()
    fun pauseScan()
}