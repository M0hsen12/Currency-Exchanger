package com.paysera.currencyexchanger.di.data.network

import com.paysera.currencyexchanger.di.data.network.route.CurrencyRouter
import com.github.pwittchen.reactivenetwork.library.rx2.Connectivity
import io.reactivex.subjects.BehaviorSubject
import retrofit2.Retrofit
import javax.inject.Inject


class NetworkManager @Inject constructor(
    var networkConnectivity: BehaviorSubject<Connectivity>,
    @NetworkModule.RestApi private val retrofitRestApi: Retrofit,
) {

    fun <T> create(tClass: Class<T>): T {
        return retrofitRestApi.create(tClass)
    }

    fun getCurrencyRouter(): CurrencyRouter {
        return retrofitRestApi.create(CurrencyRouter::class.java)
    }


}
