package com.paysera.currencyexchanger.di.modules

import com.paysera.currencyexchanger.view.activities.main.MainActivity
import com.paysera.currencyexchanger.di.viewModelsInjections.ViewModelModule
import dagger.Module
import dagger.android.ContributesAndroidInjector


@Module
abstract class ActivityInjectorsModule {


    @ContributesAndroidInjector(modules = [ViewModelModule::class, FragmentBuildersModule::class])

    abstract fun mainActivityInjector(): MainActivity


}