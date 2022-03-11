package com.paysera.currencyexchanger.di.modules

import androidx.lifecycle.ViewModelProvider
import com.paysera.currencyexchanger.di.viewModelsInjections.ViewModelFactory
import dagger.Binds
import dagger.Module

@Module
abstract class ViewModelFactoryModule {

    @Binds
    abstract fun bindViewModelFactory(factory: ViewModelFactory): ViewModelProvider.Factory
}