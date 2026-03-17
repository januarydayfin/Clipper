package com.krayapp.buffercompanion.bargen.presentation.utils

import com.gun0912.tedpermission.PermissionBuilder
import com.gun0912.tedpermission.PermissionListener
import com.gun0912.tedpermission.normal.TedPermission.Builder

fun PermissionBuilder<Builder>.addPermissionListener(
    onGranted: () -> Unit,
    onDenied: (denied: List<String>) -> Unit
): PermissionBuilder<Builder> {
    setPermissionListener(object : PermissionListener {
        override fun onPermissionGranted() {
            onGranted()
        }

        override fun onPermissionDenied(deniedPermissions: MutableList<String>?) {
            onDenied(deniedPermissions?.toList() ?: emptyList())
        }

    })
    return this
}