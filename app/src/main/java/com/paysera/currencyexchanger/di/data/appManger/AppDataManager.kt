package com.paysera.currencyexchanger.di.data.appManger

import android.content.Context
import com.paysera.currencyexchanger.di.data.database.DatabaseManager
import com.paysera.currencyexchanger.di.data.network.NetworkManager
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AppDataManager
@Inject
constructor(
    override val context: Context,
    override val networkManager: NetworkManager,
    override val databaseManager: DatabaseManager
    ) : DataManager


