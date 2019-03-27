package com.github.chudoxl.testcontacts.moxy

import com.arellomobile.mvp.MvpPresenter
import com.arellomobile.mvp.MvpView
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable

abstract class BaseMvpPresenter<V : MvpView> : MvpPresenter<V>() {

    private val disposables = CompositeDisposable()

    protected fun disposeOnDestroy(subscription: Disposable) {
        disposables.add(subscription)
    }

    override fun onDestroy() {
        super.onDestroy()
        disposables.clear()
    }
}
