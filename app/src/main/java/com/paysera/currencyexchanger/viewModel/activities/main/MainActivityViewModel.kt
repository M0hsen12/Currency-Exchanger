package com.paysera.currencyexchanger.viewModel.activities.main

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.paysera.currencyexchanger.BuildConfig
import com.paysera.currencyexchanger.di.data.appManger.DataManager
import com.paysera.currencyexchanger.model.Currency
import com.paysera.currencyexchanger.view.base.BaseViewModel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

class MainActivityViewModel @Inject constructor(
    dataManager: DataManager,
    compositeDisposable: CompositeDisposable
) : BaseViewModel(dataManager, compositeDisposable) {

    private val TAG = "QQQ"
    val currencyLiveData = MutableLiveData<Currency>()


    init {

        getAPI()
    }

    private fun getAPI() {

        disposable[1]?.dispose()
        disposable[1] = mDataManager.networkManager.getCurrencyRouter().getCurrency(BuildConfig.apiKey,"USD,BGN,JPY,EUR")
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread()).subscribe({
                if (it.isSuccessful)
                    currencyLiveData.postValue(it.body())

            }, {
                Log.e(TAG, "getAPI: ${it.message}" )
                errorLiveData.postValue(it)
            })
        addDisposable(disposable[1])
    }



}