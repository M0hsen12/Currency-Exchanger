package com.paysera.currencyexchanger.di.data.network.intercepter

import android.content.Context
import okhttp3.Credentials
import okhttp3.Interceptor
import okhttp3.Response


class HeaderInterceptor constructor(
    val context: Context
) : Interceptor {
    private var credentials: String = Credentials.basic("", "") // if we need to add something to header we do it here

    override fun intercept(chain: Interceptor.Chain): Response = chain.run {
        proceed(
            request()
                .newBuilder()
                .build()
        )
    }
}