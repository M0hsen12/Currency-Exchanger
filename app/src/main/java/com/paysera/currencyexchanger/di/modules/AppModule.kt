package com.paysera.currencyexchanger.di.modules

import com.paysera.currencyexchanger.di.data.appManger.AppDataManager
import com.paysera.currencyexchanger.di.data.appManger.DataManager
import com.paysera.currencyexchanger.di.data.network.NetworkModule
import dagger.Module
import dagger.Provides
import io.reactivex.disposables.CompositeDisposable
import com.paysera.currencyexchanger.di.data.database.DatabaseModule
import javax.inject.Singleton


@Module(
    includes = [
        NetworkModule::class,
        DatabaseModule::class
    ]
)
open class AppModule {


    @Provides
    @Singleton
    fun provideDataManager(appDataManager: AppDataManager): DataManager {
        return appDataManager
    }

    @Provides
    fun provideCompositeDisposable(): CompositeDisposable {
        return CompositeDisposable()
    }


}