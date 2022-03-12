package com.paysera.currencyexchanger.model

import com.google.gson.annotations.SerializedName

data class Currency(
    @SerializedName("date")
    val date: String = "",

    @SerializedName("success")
    val success: Boolean = false,

    @SerializedName("rates")
    val rates: Rates,

    @SerializedName("timestamp")
    val timestamp: Int = 0,

    @SerializedName("base")
    val base: String = ""
)