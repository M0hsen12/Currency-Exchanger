package com.paysera.currencyexchanger.viewModel.activities.main

import androidx.lifecycle.MutableLiveData
import com.paysera.currencyexchanger.di.data.appManger.DataManager
import com.paysera.currencyexchanger.view.base.BaseViewModel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

class MainActivityViewModel @Inject constructor(
    dataManager: DataManager,
    compositeDisposable: CompositeDisposable
) : BaseViewModel(dataManager, compositeDisposable) {

    private val TAG = "RRR"
    val adsLiveData = MutableLiveData<String>()


    init {

        getAPI()
    }

    private fun getAPI() {



    }



}