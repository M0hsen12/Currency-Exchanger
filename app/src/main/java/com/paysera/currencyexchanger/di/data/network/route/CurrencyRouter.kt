package com.paysera.currencyexchanger.di.data.network.route

import com.paysera.currencyexchanger.model.Currency
import io.reactivex.Observable
import io.reactivex.Single
import retrofit2.Response
import retrofit2.http.Field
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface CurrencyRouter {

    @GET("v1/latest")
    fun getCurrency(
        @Query("access_key") apiKey: String,
        @Query("symbols") symbols: String
    ): Observable<Response<Currency>>

}
