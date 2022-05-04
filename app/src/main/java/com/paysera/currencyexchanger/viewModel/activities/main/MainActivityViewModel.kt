package com.paysera.currencyexchanger.viewModel.activities.main

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.paysera.currencyexchanger.BuildConfig
import com.paysera.currencyexchanger.di.data.appManger.DataManager
import com.paysera.currencyexchanger.di.data.database.entity.TransactionsEntity
import com.paysera.currencyexchanger.di.data.database.entity.WalletEntity
import com.paysera.currencyexchanger.model.Currency
import com.paysera.currencyexchanger.util.CurrencyDetail
import com.paysera.currencyexchanger.view.base.BaseViewModel
import io.reactivex.Observable.interval
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.processors.BehaviorProcessor
import io.reactivex.schedulers.Schedulers
import java.util.*
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class MainActivityViewModel @Inject constructor(
    dataManager: DataManager,
    compositeDisposable: CompositeDisposable
) : BaseViewModel(dataManager, compositeDisposable) {

    val currencyLiveData = MutableLiveData<Currency>()
    val updatedWalletLiveData =
        MutableLiveData<Triple<WalletEntity, WalletEntity, List<WalletEntity>>>()
    var databaseUpdateProcessor = BehaviorProcessor.create<List<WalletEntity>>()

    init {
        getCurrency()
        loopInterval()
        initDatabase()
    }


    private fun loopInterval() {

        disposable[disposableInterval]?.dispose()
        disposable[disposableInterval] =
            interval(
                5,
                TimeUnit.SECONDS
            ).observeOn(AndroidSchedulers.mainThread()) // we can use Work manger too but we dont need its services so interval will do the job
                .subscribe(
                    this::getCurrency
                ) {
                    Log.e("TAG", "getAPI: ${it.message} ")
                }


        addDisposable(disposable[disposableInterval])
    }

    private fun addRateToCurrencyDetail(currency: Currency?) {
        currency?.rates?.let {
            CurrencyDetail.USD.CurrencyRate = it.usd
            CurrencyDetail.BGN.CurrencyRate = it.bgn
            CurrencyDetail.EUR.CurrencyRate = it.eur
        }

    }

    private fun initDatabase() {
        disposable[disposableInitDatabase]?.dispose()
        disposable[disposableInitDatabase] = mDataManager.databaseManager.WalletDao().all()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread()).subscribe({

                if (it.isEmpty())
                    firstTimeWalletToDatabase()

            }, {
                Log.e("TAG", "getAPI: ${it.message}")
                errorLiveData.postValue(it)
            })
        addDisposable(disposable[disposableInitDatabase])
    }

    private fun firstTimeWalletToDatabase() { // first Time User Here and we should give user 1000 EUR

        disposable[disposableFirstTimeWallet]?.dispose()
        disposable[disposableFirstTimeWallet] = mDataManager.databaseManager.WalletDao().insertAll(
            firstTimeInitDatabase()
        ).flatMap {
            mDataManager.databaseManager.WalletDao().all()

        }.subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread()).subscribe({

                databaseUpdateProcessor.onNext(it)

            }, {
                Log.e("TAG", "db error: ${it.message}")
                errorLiveData.postValue(it)
            })
        addDisposable(disposable[disposableFirstTimeWallet])


    }

    private fun firstTimeInitDatabase(): ArrayList<WalletEntity> {
        val list = ArrayList<WalletEntity>()
        CurrencyDetail.values().forEach {
            list.add(
                WalletEntity(
                    id = it.id,
                    amount = it.entryPoint,
                    symbolName = it.symbolName
                )
            )
        }
        return list
    }

    fun getBalanceList(onListReceives: (list: List<WalletEntity>) -> Unit) {
        disposable[disposableGetBalanceList]?.dispose()
        disposable[disposableGetBalanceList] = mDataManager.databaseManager.WalletDao().all()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread()).subscribe({
                onListReceives.invoke(it)
            }, {
                Log.e("TAG", "db error: ${it.message}")
                errorLiveData.postValue(it)
            })
        addDisposable(disposable[disposableGetBalanceList])

    }

    fun getCurrencyList(): List<String> {
        val list = ArrayList<String>()
        CurrencyDetail.values().forEach {
            list.add(it.symbolName)
        }
        return list
    }

    fun findWalletItemsWithName(
        sellNameID: String,
        receivesNameID: String,
        onWalletReceives: (pair: Pair<WalletEntity, WalletEntity>) -> Unit
    ) {
        disposable[disposableFindWalletItems]?.dispose()
        disposable[disposableFindWalletItems] =
            Single.zip(
                mDataManager.databaseManager.WalletDao().findByName(sellNameID),
                mDataManager.databaseManager.WalletDao().findByName(receivesNameID)
            ) { sell: WalletEntity, receives: WalletEntity ->
                Pair(sell, receives)
            }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread()).subscribe({
                    onWalletReceives.invoke(it)
                }, {
                    Log.e("TAG", "db error: ${it.message}")
                    errorLiveData.postValue(it)
                })
        addDisposable(disposable[disposableFindWalletItems])
    }

    fun updateDatabaseAfterTransaction(sell: WalletEntity, receives: WalletEntity) {

        disposable[disposableUpdateDatabase]?.dispose()
        disposable[disposableUpdateDatabase] =
            Single.zip(
                mDataManager.databaseManager.WalletDao().update(sell),
                mDataManager.databaseManager.WalletDao().update(receives),
                mDataManager.databaseManager.WalletDao().all()
            ) { sellUnit: Unit, receivesUnit: Unit, list: List<WalletEntity> ->
                Triple(sell, receives, list)
            }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread()).subscribe({
                    updatedWalletLiveData.postValue(it) // i could send only list , its only thing we need but i like Triple :))
                }, {
                    Log.e("TAG", "db error: ${it.message}")
                    errorLiveData.postValue(it)
                })
        addDisposable(disposable[disposableUpdateDatabase])
    }

    fun getTransaction(onEnd: (transaction: TransactionsEntity) -> Unit) {

        disposable[disposableGetTransaction]?.dispose()
        disposable[disposableGetTransaction] = mDataManager.databaseManager.TransactionDao().all()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread()).subscribe({
                if (it.isEmpty())
                    initTransactionDatabase(onEnd)
                else onEnd.invoke(it.first())

            }, {
                Log.e("TAG", "getAPI: ${it.message}")
                errorLiveData.postValue(it)
            })
        addDisposable(disposable[disposableGetTransaction])
    }

    private fun initTransactionDatabase(onEnd: (transaction: TransactionsEntity) -> Unit) {
        disposable[disposableInitTransaction]?.dispose()
        disposable[disposableInitTransaction] = mDataManager.databaseManager.TransactionDao().insert(
            TransactionsEntity(TransactionID, TransactionAmount, TransactionCount)
        ).flatMap {
            mDataManager.databaseManager.TransactionDao().findValueById(TransactionID)

        }.subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread()).subscribe({
                onEnd.invoke(it)

            }, {
                Log.e("TAG", "db error: ${it.message}")
                errorLiveData.postValue(it)
            })
        addDisposable(disposable[disposableInitTransaction])

    }

    fun updateTransactionDatabase(transaction: TransactionsEntity, onEnd: () -> Unit) {

        disposable[disposableUpdateTransaction]?.dispose()
        disposable[disposableUpdateTransaction] = mDataManager.databaseManager.TransactionDao().update(transaction)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread()).subscribe({
                onEnd.invoke()

            }, {
                Log.e("TAG", "db error: ${it.message}")
                errorLiveData.postValue(it)
            })
        addDisposable(disposable[disposableUpdateTransaction])
    }

    private fun getCurrency(long: Long = 0) {
        disposable[disposableGetCurrency]?.dispose()
        disposable[disposableGetCurrency] =
            mDataManager.networkManager.getCurrencyRouter()
                .getCurrency(BuildConfig.apiKey, SYMBOL_REQUEST)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    if (it.isSuccessful) {
                        addRateToCurrencyDetail(it.body())
                        currencyLiveData.postValue(it.body())

                    }

                }, {
                    Log.e("TAG", "getAPI:$long ${it.message}")
                    errorLiveData.postValue(it)
                })
        addDisposable(disposable[disposableGetCurrency])
    }

    fun getBalanceAndTransactionForSubmit(
        symbol: String,
        onProcessFinish: (Pair<WalletEntity, List<TransactionsEntity>>) -> Unit
    ) {
        disposable[disposableGetBalanceAndTransaction]?.dispose()
        disposable[disposableGetBalanceAndTransaction] = Single.zip(mDataManager.databaseManager.WalletDao().findByName(symbol),
            mDataManager.databaseManager.TransactionDao().all()
        ) { wallet, transaction ->
            Pair(wallet, transaction)
        }.subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread()).subscribe({
                onProcessFinish.invoke(it)
            }, {
                Log.e("TAG", "db error: ${it.message}")
                errorLiveData.postValue(it)
            })
        addDisposable(disposable[disposableGetBalanceAndTransaction])
    }


    companion object {
        const val TransactionID =
            100  // i could use enum  but want to try every thing in this test project
        const val TransactionAmount = 0.0
        const val TransactionCount = 0
        const val CommissionFeePercentage = 0.7
        const val SYMBOL_REQUEST =
            "USD,BGN,JPY,EUR" // super weird api ,, wont work on list or array i had to use it like this
        const val disposableInterval = 1
        const val disposableInitDatabase = 2
        const val disposableFirstTimeWallet = 3
        const val disposableGetBalanceList = 4
        const val disposableFindWalletItems = 5
        const val disposableUpdateDatabase = 6
        const val disposableGetTransaction = 7
        const val disposableInitTransaction = 8
        const val disposableUpdateTransaction = 9
        const val disposableGetCurrency= 10
        const val disposableGetBalanceAndTransaction= 11
    }

}