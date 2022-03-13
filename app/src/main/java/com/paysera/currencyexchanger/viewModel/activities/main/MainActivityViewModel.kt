package com.paysera.currencyexchanger.viewModel.activities.main

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.paysera.currencyexchanger.BuildConfig
import com.paysera.currencyexchanger.di.data.appManger.DataManager
import com.paysera.currencyexchanger.di.data.database.entity.WalletEntity
import com.paysera.currencyexchanger.model.Currency
import com.paysera.currencyexchanger.util.CurrencyDetail
import com.paysera.currencyexchanger.view.base.BaseViewModel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.processors.BehaviorProcessor
import io.reactivex.schedulers.Schedulers
import java.util.ArrayList
import javax.inject.Inject

class MainActivityViewModel @Inject constructor(
    dataManager: DataManager,
    compositeDisposable: CompositeDisposable
) : BaseViewModel(dataManager, compositeDisposable) {

    private val TAG = "QQQ"
    val currencyLiveData = MutableLiveData<Currency>()
    var databaseUpdateProcessor = BehaviorProcessor.create<List<WalletEntity>>()

    init {

//        getAPI()
        initDatabase()
    }


    private fun getAPI() {

        disposable[1]?.dispose()
        disposable[1] = mDataManager.networkManager.getCurrencyRouter()
            .getCurrency(BuildConfig.apiKey, "USD,BGN,JPY,EUR")
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread()).subscribe({
                if (it.isSuccessful)
                    currencyLiveData.postValue(it.body())

            }, {
                Log.e(TAG, "getAPI: ${it.message}")
                errorLiveData.postValue(it)
            })
        addDisposable(disposable[1])
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


}