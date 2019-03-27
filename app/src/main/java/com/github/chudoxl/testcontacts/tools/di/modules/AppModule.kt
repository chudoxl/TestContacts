package com.github.chudoxl.testcontacts.tools.di.modules

import android.content.Context
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class AppModule(private val applicationContext: Context){
    @Provides
    @Singleton
    internal fun providesAppContext(): Context {
        return applicationContext
    }
}