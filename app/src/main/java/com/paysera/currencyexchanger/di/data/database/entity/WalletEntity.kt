package com.paysera.currencyexchanger.di.data.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "wallet")
class WalletEntity(

    @PrimaryKey
    @ColumnInfo(name = "id")
    var id: Int? = null,

    @ColumnInfo(name = "amount")
    var amount: Double? = null,

    @ColumnInfo(name = "symbolName")
    var symbolName: String? = null
)