package com.paysera.currencyexchanger.di.data.database.dao

import androidx.room.*
import com.paysera.currencyexchanger.di.data.database.entity.WalletEntity
import io.reactivex.Maybe
import io.reactivex.Single

@Dao
abstract class WalletDao {
    @Query("SELECT * FROM wallet")
    abstract fun all(): Single<List<WalletEntity>>

    @Query("SELECT * FROM wallet WHERE symbolName LIKE :name LIMIT 1")
    abstract fun findByName(name: String): WalletEntity

    @Query("SELECT * FROM wallet WHERE id LIKE :id LIMIT 1")
    abstract fun findByID(id: Int): WalletEntity

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insert(wallet: WalletEntity): Single<Long>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insertAll(wallets: ArrayList<WalletEntity>):Single<Unit>

    @Update
    abstract fun update(wallet: WalletEntity)



}