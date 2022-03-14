package com.paysera.currencyexchanger.di.data.database.dao

import androidx.room.*
import com.paysera.currencyexchanger.di.data.database.entity.TransactionsEntity
import com.paysera.currencyexchanger.di.data.database.entity.WalletEntity
import io.reactivex.Single

@Dao
abstract class TransactionDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insert(transaction: TransactionsEntity):  Single<Long>

    @Query("SELECT * FROM Transactions WHERE `id` = :id LIMIT 1")
    abstract fun findValueById(id: Int):  Single<TransactionsEntity>

    @Update
    abstract fun update(transaction: TransactionsEntity): Single<Unit>
}