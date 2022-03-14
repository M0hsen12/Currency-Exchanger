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
import io.reactivex.Observable
import io.reactivex.Observable.interval
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.functions.Consumer
import io.reactivex.processors.BehaviorProcessor
import io.reactivex.schedulers.Schedulers
import java.util.*
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class MainActivityViewModel @Inject constructor(
    dataManager: DataManager,
    compositeDisposable: CompositeDisposable
) : BaseViewModel(dataManager, compositeDisposable) {

    private val TAG = "QQQ"
    val currencyLiveData = MutableLiveData<Currency>()
    val updatedWalletLiveData =
        MutableLiveData<Triple<WalletEntity, WalletEntity, List<WalletEntity>>>()
    var databaseUpdateProcessor = BehaviorProcessor.create<List<WalletEntity>>()

    init {

        getAPI()
        initDatabase()
    }


    private fun getAPI() {

        disposable[1]?.dispose()
        disposable[1] =
            interval(
                5,
                TimeUnit.SECONDS
            ).observeOn(AndroidSchedulers.mainThread()) // we can use Work manger too but we dont need its services so interval will do the job
                .subscribe(
                    this::apiCall
                ) {
                    Log.e(TAG, "getAPI: ${it.message} ")
                }


        addDisposable(disposable[1])
    }

    private fun apiCall(long: Long = 0) {
        Log.e("OOO", "apiCall: in a call")
        disposable[10]?.dispose()
        disposable[10] =
            mDataManager.networkManager.getCurrencyRouter()
                .getCurrency(BuildConfig.apiKey, "USD,BGN,JPY,EUR")
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())

                .subscribe({
                    if (it.isSuccessful) {
                        addRateToCurrencyDetail(it.body())
                        currencyLiveData.postValue(it.body())

                    }

                }, {
                    Log.e(TAG, "getAPI: ${it.message}")
                    errorLiveData.postValue(it)
                })
        addDisposable(disposable[10])
    }

    private fun addRateToCurrencyDetail(currency: Currency?) {
        currency?.rates?.let {
            CurrencyDetail.USD.CurrencyRate = it.usd
            CurrencyDetail.BGN.CurrencyRate = it.bgn
            CurrencyDetail.EUR.CurrencyRate = it.eur
        }

    }

    private fun initDatabase() {
        disposable[2]?.dispose()
        disposable[2] = mDataManager.databaseManager.WalletDao().all()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread()).subscribe({
                Log.e(TAG, "initDatabase: ${it.size}")
                if (it.isEmpty())
                    firstTimeWalletToDatabase()

            }, {
                Log.e(TAG, "getAPI: ${it.message}")
                errorLiveData.postValue(it)
            })
        addDisposable(disposable[2])
    }

    private fun firstTimeWalletToDatabase() { // first Time User Here and we should give user 1000 EUR

        disposable[3]?.dispose()
        disposable[3] = mDataManager.databaseManager.WalletDao().insertAll(
            firstTimeInitDatabase()
        ).flatMap {
            mDataManager.databaseManager.WalletDao().all()

        }.subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread()).subscribe({

                Log.e(TAG, "addWalletToDatabase: on procecor ${it.size}")
                databaseUpdateProcessor.onNext(it)

            }, {
                Log.e(TAG, "db error: ${it.message}")
                errorLiveData.postValue(it)
            })
        addDisposable(disposable[3])


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
        disposable[4]?.dispose()
        disposable[4] = mDataManager.databaseManager.WalletDao().all()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread()).subscribe({
                onListReceives.invoke(it)
            }, {
                Log.e(TAG, "db error: ${it.message}")
                errorLiveData.postValue(it)
            })
        addDisposable(disposable[4])

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
        disposable[5]?.dispose()
        disposable[5] =
            Single.zip(
                mDataManager.databaseManager.WalletDao().findByName(sellNameID),
                mDataManager.databaseManager.WalletDao().findByName(receivesNameID),
                { sell: WalletEntity, receives: WalletEntity ->
                    Pair(sell, receives)
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread()).subscribe({
                    onWalletReceives.invoke(it)
                }, {
                    Log.e(TAG, "db error: ${it.message}")
                    errorLiveData.postValue(it)
                })
        addDisposable(disposable[5])
    }

    fun updateDatabaseAfterTransaction(sell: WalletEntity, receives: WalletEntity) {

        disposable[6]?.dispose()
        disposable[6] =
            Single.zip(
                mDataManager.databaseManager.WalletDao().update(sell),
                mDataManager.databaseManager.WalletDao().update(receives),
                mDataManager.databaseManager.WalletDao().all(),
                { sellUnit: Unit, receivesUnit: Unit, list: List<WalletEntity> ->
                    Triple(sell, receives, list)
                }
            )
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread()).subscribe({
                    updatedWalletLiveData.postValue(it) // i could send only list , its only thing we need but i like Triple :))
                }, {
                    Log.e(TAG, "db error: ${it.message}")
                    errorLiveData.postValue(it)
                })
        addDisposable(disposable[6])
    }

    fun getTransaction(onEnd: (transaction: TransactionsEntity) -> Unit) {

        disposable[7]?.dispose()
        disposable[7] = mDataManager.databaseManager.TransactionDao().all()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread()).subscribe({
                Log.e(TAG, "initDatabase:transaction ${it.size}")
                if (it.isEmpty())
                    initTransactionDatabase(onEnd)
                else onEnd.invoke(it.first())

            }, {
                Log.e(TAG, "getAPI: ${it.message}")
                errorLiveData.postValue(it)
            })
        addDisposable(disposable[7])
    }

    private fun initTransactionDatabase(onEnd: (transaction: TransactionsEntity) -> Unit) {
        disposable[8]?.dispose()
        disposable[8] = mDataManager.databaseManager.TransactionDao().insert(
            TransactionsEntity(TransactionID, TransactionAmount, TransactionCount)
        ).flatMap {
            mDataManager.databaseManager.TransactionDao().findValueById(TransactionID)

        }.subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread()).subscribe({
                Log.e(TAG, "tranaction : on procecor $it ${it.id}")
                onEnd.invoke(it)

            }, {
                Log.e(TAG, "db error: ${it.message}")
                errorLiveData.postValue(it)
            })
        addDisposable(disposable[8])

    }

    fun updateTransactionDatabase(transaction: TransactionsEntity, onEnd: () -> Unit) {

        disposable[9]?.dispose()
        disposable[9] = mDataManager.databaseManager.TransactionDao().update(transaction)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread()).subscribe({
                Log.e(TAG, "tranasaction e: on  $it ")
                onEnd.invoke()

            }, {
                Log.e(TAG, "db error: ${it.message}")
                errorLiveData.postValue(it)
            })
        addDisposable(disposable[9])
    }


    companion object {
        const val TransactionID =
            100  // i could use enum like wallet but want to try every thing in this test project
        const val TransactionAmount = 0.0
        const val TransactionCount = 0
        const val CommissionFeePercentage = 0.7
    }

}