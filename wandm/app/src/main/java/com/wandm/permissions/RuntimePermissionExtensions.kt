package com.wandm.permissions

import android.app.Activity
import android.app.Fragment

fun Activity.askPermission(permission: String, callback: PermissionCallback) {
    if (PermissionHelper.hasPermission(this, permission))
        return
    PermissionHelper.askForPermission(this, permission, callback)
}

fun Fragment.askPermission(permission: String, callback: PermissionCallback) {
    if (PermissionHelper.hasPermission(this.activity, permission))
        return
    PermissionHelper.askForPermission(this.activity, permission, callback)
}

fun android.support.v4.app.Fragment.askPermission(permission: String, callback: PermissionCallback) {
    if (PermissionHelper.hasPermission(this.activity, permission))
        return
    PermissionHelper.askForPermission(this.activity, permission, callback)
}

fun Activity.askPermission(permissions: Array<String>, callback: PermissionCallback) {
    if (PermissionHelper.hasPermission(this, permissions))
        return
    PermissionHelper.askForPermission(this, permissions, callback)
}

fun Fragment.askPermission(permissions: Array<String>, callback: PermissionCallback) {
    if (PermissionHelper.hasPermission(this.activity, permissions))
        return
    PermissionHelper.askForPermission(this.activity, permissions, callback)
}

fun android.support.v4.app.Fragment.askPermission(permissions: Array<String>, callback: PermissionCallback) {
    if (PermissionHelper.hasPermission(this.activity, permissions))
        return
    PermissionHelper.askForPermission(this.activity, permissions, callback)
}

inline fun Activity.askPermission(permission: String, callback: PermissionCallback,
                                  has: () -> Unit) {
    if (PermissionHelper.hasPermission(this, permission)) {
        has()
        return
    }
    PermissionHelper.askForPermission(this, permission, callback)
}

inline fun Fragment.askPermission(permission: String, callback: PermissionCallback,
                                  has: () -> Unit) {
    if (PermissionHelper.hasPermission(this.activity, permission)) {
        has()
        return
    }
    PermissionHelper.askForPermission(this.activity, permission, callback)
}

inline fun android.support.v4.app.Fragment.askPermission(permission: String,
                                                         callback: PermissionCallback,
                                                         has: () -> Unit) {
    if (PermissionHelper.hasPermission(this.activity, permission)) {
        has()
        return
    }
    PermissionHelper.askForPermission(this.activity, permission, callback)
}

inline fun Activity.askPermission(permissions: Array<String>, callback: PermissionCallback,
                                  has: () -> Unit) {
    if (PermissionHelper.hasPermission(this, permissions)) {
        has()
        return
    }
    PermissionHelper.askForPermission(this, permissions, callback)
}

inline fun Fragment.askPermission(permissions: Array<String>, callback: PermissionCallback,
                                  has: () -> Unit) {
    if (PermissionHelper.hasPermission(this.activity, permissions)) {
        has()
        return
    }
    PermissionHelper.askForPermission(this.activity, permissions, callback)
}

inline fun android.support.v4.app.Fragment.askPermission(permissions: Array<String>,
                                                         callback: PermissionCallback,
                                                         has: () -> Unit) {
    if (PermissionHelper.hasPermission(this.activity, permissions)) {
        has()
        return
    }
    PermissionHelper.askForPermission(this.activity, permissions, callback)
}