package com.paysera.currencyexchanger.di.viewModelsInjections

import androidx.lifecycle.ViewModel
import com.paysera.currencyexchanger.viewModel.activities.main.MainActivityViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
abstract class ViewModelModule {

    @Binds
    @IntoMap
    @ViewModelKey(MainActivityViewModel::class)
    internal abstract fun bindMyViewModel(viewModel: MainActivityViewModel): ViewModel
}