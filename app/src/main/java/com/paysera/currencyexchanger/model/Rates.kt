package com.paysera.currencyexchanger.model

import com.google.gson.annotations.SerializedName

data class Rates(
    @SerializedName("JPY")
    val jpy: Double = 0.0,

    @SerializedName("EUR")
    val eur: Double = 0.0,

    @SerializedName("USD")
    val usd: Double = 0.0,

    @SerializedName("BGN")
    val bgn: Double = 0.0)