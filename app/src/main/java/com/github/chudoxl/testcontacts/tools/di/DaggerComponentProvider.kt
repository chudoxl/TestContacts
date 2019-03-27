package com.github.chudoxl.testcontacts.tools.di

import android.app.Activity
import androidx.fragment.app.Fragment
import com.github.chudoxl.testcontacts.tools.di.components.IAppComponent

/**
 * This exists so things get less coupled from the application class. Thanks to this interface,
 * our application class doesn't need to be open just so our test application class can extend it.
 */
interface DaggerComponentProvider {

    val appComponent: IAppComponent
}

/**
 * This extensions exist to makes things beautiful in the Activity
 */
val Activity.injector get() = (application as DaggerComponentProvider).appComponent

val Fragment.injector get() = (activity?.application as DaggerComponentProvider).appComponent