package com.paysera.currencyexchanger.di.data.network


import android.content.Context
import com.paysera.currencyexchanger.di.data.network.intercepter.HeaderInterceptor
import com.github.pwittchen.reactivenetwork.library.rx2.Connectivity
import com.paysera.currencyexchanger.BuildConfig
import dagger.Module
import dagger.Provides
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.BehaviorSubject
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Qualifier
import javax.inject.Singleton

@Module
class NetworkModule {


    @Provides
    @Singleton
    fun provideNetworkConnectivity(): BehaviorSubject<Connectivity> {
        return BehaviorSubject.create()
    }

    @Provides
    @Singleton
    fun provideHeaderInterceptor(
        context: Context
    ): HeaderInterceptor {
        return HeaderInterceptor(context)
    }

    @Provides
    @Singleton
    fun provideClient(headerInterceptor: HeaderInterceptor): OkHttpClient {
        val builder = OkHttpClient.Builder()
            .connectTimeout(40, TimeUnit.SECONDS)// Set connection timeout
            .readTimeout(40, TimeUnit.SECONDS)// Read timeout
            .writeTimeout(40, TimeUnit.SECONDS)// Write timeout
            .addInterceptor(headerInterceptor)
            .cache(null)


        return builder.build()

    }

    @RestApi
    @Provides
    @Singleton
    fun provideRestApi(
        context: Context,
        client: OkHttpClient
    ): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BuildConfig.baseUrl)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.createWithScheduler(Schedulers.io()))
            .build()
    }


    @Qualifier
    @Retention(AnnotationRetention.RUNTIME)
    annotation class RestApi

}
