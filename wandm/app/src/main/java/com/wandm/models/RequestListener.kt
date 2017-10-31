package com.wandm.models

interface RequestListener<in T> {
    fun onStart() {}
    fun onComplete(data: T?)
}
