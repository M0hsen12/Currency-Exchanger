package com.paysera.currencyexchanger

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.ProcessLifecycleOwner
import com.paysera.currencyexchanger.di.component.DaggerAppComponent

import dagger.android.AndroidInjector
import dagger.android.support.DaggerApplication



class App :DaggerApplication(), LifecycleObserver {

    override fun onCreate() {
        super.onCreate()
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true)
        ProcessLifecycleOwner.get().lifecycle.addObserver(this)
    }


    override fun applicationInjector(): AndroidInjector<out DaggerApplication> {
        val component = DaggerAppComponent.builder().application(this.applicationContext).build()
        component.inject(this)
        return component
    }

}
