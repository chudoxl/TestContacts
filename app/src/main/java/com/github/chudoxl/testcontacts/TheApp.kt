package com.github.chudoxl.testcontacts

import android.app.Application
import com.github.chudoxl.testcontacts.tools.di.DaggerComponentProvider
import com.github.chudoxl.testcontacts.tools.di.components.DaggerIAppComponent
import com.github.chudoxl.testcontacts.tools.di.components.IAppComponent
import com.github.chudoxl.testcontacts.tools.di.modules.AppModule
import timber.log.Timber

class TheApp : Application(), DaggerComponentProvider {

    override val appComponent: IAppComponent by lazy {
        DaggerIAppComponent.builder()
            .appModule(AppModule(this))
            .build()
    }

    override fun onCreate() {
        super.onCreate()
        setupTimber()
    }

    private fun setupTimber() {
        if (BuildConfig.DEBUG) Timber.plant(Timber.DebugTree())
    }
}