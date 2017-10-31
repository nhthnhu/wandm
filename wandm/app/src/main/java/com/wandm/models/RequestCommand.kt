package com.wandm.models

abstract class RequestCommand<in T>(protected val listener: RequestListener<T>) {
    fun execute() {
        listener.onStart()
        request()
    }

    protected abstract fun request()
}
