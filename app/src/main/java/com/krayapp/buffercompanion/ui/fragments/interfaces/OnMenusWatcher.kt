package com.krayapp.buffercompanion.ui.fragments.interfaces

interface OnMenusWatcher {
    fun onMenusOpened()

    fun onMenusClosed() {}
    fun onMenusAllClosed()
}