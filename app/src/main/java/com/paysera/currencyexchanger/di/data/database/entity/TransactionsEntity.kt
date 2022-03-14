package com.paysera.currencyexchanger.di.data.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "Transactions")
class TransactionsEntity (
    @PrimaryKey
    @ColumnInfo(name = "id")
    var id: Int? = null,

    @ColumnInfo(name = "totalAmount")
    var totalAmount: Double? = null,

    @ColumnInfo(name = "count")
    var count: Int? = null)
