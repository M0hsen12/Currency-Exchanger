package com.paysera.currencyexchanger.di.data.appManger

import android.content.Context
import com.paysera.currencyexchanger.di.data.database.DatabaseManager
import com.paysera.currencyexchanger.di.data.network.NetworkManager


interface DataManager {

    val context: Context

    val networkManager: NetworkManager

    val databaseManager: DatabaseManager

    // we can add every thing we want in here like dataBase manager , download manager , account manger and ...

}
