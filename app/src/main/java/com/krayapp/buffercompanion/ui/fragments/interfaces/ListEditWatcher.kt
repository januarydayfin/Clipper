package com.krayapp.buffercompanion.ui.fragments.interfaces

interface ListEditWatcher {
    fun onMenusOpened()

    fun onMenusClosed() {}
    fun onMenusAllClosed()
}