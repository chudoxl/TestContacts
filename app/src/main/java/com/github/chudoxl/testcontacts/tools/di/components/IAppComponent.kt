package com.github.chudoxl.testcontacts.tools.di.components

import com.github.chudoxl.testcontacts.home.HomePresenter
import com.github.chudoxl.testcontacts.tools.di.modules.AppModule
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [AppModule::class])
interface IAppComponent {
    //fun plus(userModule: UserModule): IUserComponent
    fun inject(p: HomePresenter)
}