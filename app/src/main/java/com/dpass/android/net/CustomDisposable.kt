package com.dpass.android.net

import io.reactivex.observers.DisposableObserver

abstract class CustomDisposable<T> : DisposableObserver<T>() {

    abstract fun atFirst()

    abstract fun atSuccess(t: T)

    abstract fun atError(e: Throwable)

    override fun onComplete() {

    }

    override fun onNext(t: T) {
        atFirst()
        atSuccess(t)
    }

    override fun onError(e: Throwable) {
        atFirst()
        atError(e)
    }

}