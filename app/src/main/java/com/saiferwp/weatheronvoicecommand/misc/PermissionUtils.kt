package com.saiferwp.weatheronvoicecommand.misc


import android.content.Context
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment

object PermissionCode {
    const val RECORD_AUDIO_PERMISSION_CODE = 1
}

fun Fragment.checkPermissions(
    permissions: Array<String>,
    requestCode: Int,
    onGrantedListener: (() -> Unit)? = null
) {
    checkPermissions(
        requireContext(),
        permissions,
        onGrantedListener,
        onNotGranted = { notGrantedPermissions ->
            requestPermissions(notGrantedPermissions, requestCode)
        })
}

fun checkPermissions(
    context: Context,
    permissions: Array<String>,
    onGranted: (() -> Unit)? = null,
    onNotGranted: ((notGrantedPermissions: Array<String>) -> Unit)? = null
) {
    val notGrantedPermissions = permissions.filter { permission ->
        !isGranted(context, permission)
    }
    if (notGrantedPermissions.isEmpty()) {
        onGranted?.invoke()
    } else {
        onNotGranted?.invoke(notGrantedPermissions.toTypedArray())
    }
}

fun isGranted(context: Context, permission: String): Boolean {
    val status = ContextCompat.checkSelfPermission(context, permission)
    return isGranted(status)
}

fun isGranted(status: Int): Boolean {
    return status == PackageManager.PERMISSION_GRANTED
}

fun wereAllPermissionsGranted(grantResults: IntArray): Boolean {
    return grantResults.isNotEmpty()
            && grantResults.all { isGranted(it) }
}