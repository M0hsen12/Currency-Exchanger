package com.paysera.currencyexchanger.di.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.paysera.currencyexchanger.di.data.database.dao.WalletDao
import com.paysera.currencyexchanger.di.data.database.entity.WalletEntity


@Database(
    entities = [WalletEntity::class],
    version = 1,
    exportSchema = false
)
abstract class DatabaseManager : RoomDatabase() {
    abstract fun WalletDao(): WalletDao


}