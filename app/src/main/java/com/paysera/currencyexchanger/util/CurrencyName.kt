package com.paysera.currencyexchanger.util

enum class CurrencyDetail(val id:Int,val symbolName:String,val entryPoint:Double,var CurrencyRate:Double) {

    EUR(1,"EUR",1000.00,0.0),
    BGN(2,"BGN",0.00,0.0),
    USD(3,"USD",0.00,0.0)


}