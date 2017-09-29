package com.wandm.permissions

import android.app.Activity
import android.app.Fragment
import com.wandm.permissions.PermissionCallback
import com.wandm.permissions.RuntimePermissionHelper

fun Activity.askPermission(permission: String, callback: PermissionCallback) {
    if (RuntimePermissionHelper.hasPermission(this, permission))
        return
    RuntimePermissionHelper.askForPermission(this, permission, callback)
}

fun Fragment.askPermission(permission: String, callback: PermissionCallback) {
    if (RuntimePermissionHelper.hasPermission(this.activity, permission))
        return
    RuntimePermissionHelper.askForPermission(this.activity, permission, callback)
}

fun android.support.v4.app.Fragment.askPermission(permission: String, callback: PermissionCallback) {
    if (RuntimePermissionHelper.hasPermission(this.activity, permission))
        return
    RuntimePermissionHelper.askForPermission(this.activity, permission, callback)
}

fun Activity.askPermission(permissions: Array<String>, callback: PermissionCallback) {
    if (RuntimePermissionHelper.hasPermission(this, permissions))
        return
    RuntimePermissionHelper.askForPermission(this, permissions, callback)
}

fun Fragment.askPermission(permissions: Array<String>, callback: PermissionCallback) {
    if (RuntimePermissionHelper.hasPermission(this.activity, permissions))
        return
    RuntimePermissionHelper.askForPermission(this.activity, permissions, callback)
}

fun android.support.v4.app.Fragment.askPermission(permissions: Array<String>, callback: PermissionCallback) {
    if (RuntimePermissionHelper.hasPermission(this.activity, permissions))
        return
    RuntimePermissionHelper.askForPermission(this.activity, permissions, callback)
}

inline fun Activity.askPermission(permission: String, callback: PermissionCallback,
                                  has: () -> Unit) {
    if (RuntimePermissionHelper.hasPermission(this, permission)) {
        has()
        return
    }
    RuntimePermissionHelper.askForPermission(this, permission, callback)
}

inline fun Fragment.askPermission(permission: String, callback: PermissionCallback,
                                  has: () -> Unit) {
    if (RuntimePermissionHelper.hasPermission(this.activity, permission)) {
        has()
        return
    }
    RuntimePermissionHelper.askForPermission(this.activity, permission, callback)
}

inline fun android.support.v4.app.Fragment.askPermission(permission: String,
                                                         callback: PermissionCallback,
                                                         has: () -> Unit) {
    if (RuntimePermissionHelper.hasPermission(this.activity, permission)) {
        has()
        return
    }
    RuntimePermissionHelper.askForPermission(this.activity, permission, callback)
}

inline fun Activity.askPermission(permissions: Array<String>, callback: PermissionCallback,
                                  has: () -> Unit) {
    if (RuntimePermissionHelper.hasPermission(this, permissions)) {
        has()
        return
    }
    RuntimePermissionHelper.askForPermission(this, permissions, callback)
}

inline fun Fragment.askPermission(permissions: Array<String>, callback: PermissionCallback,
                                  has: () -> Unit) {
    if (RuntimePermissionHelper.hasPermission(this.activity, permissions)) {
        has()
        return
    }
    RuntimePermissionHelper.askForPermission(this.activity, permissions, callback)
}

inline fun android.support.v4.app.Fragment.askPermission(permissions: Array<String>,
                                                         callback: PermissionCallback,
                                                         has: () -> Unit) {
    if (RuntimePermissionHelper.hasPermission(this.activity, permissions)) {
        has()
        return
    }
    RuntimePermissionHelper.askForPermission(this.activity, permissions, callback)
}