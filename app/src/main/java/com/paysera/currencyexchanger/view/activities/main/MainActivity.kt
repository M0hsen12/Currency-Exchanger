package com.paysera.currencyexchanger.view.activities.main

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import com.paysera.currencyexchanger.R
import com.paysera.currencyexchanger.databinding.ActivityMainBinding
import com.paysera.currencyexchanger.di.data.database.entity.WalletEntity
import com.paysera.currencyexchanger.di.viewModelsInjections.InjectionViewModelProvider
import com.paysera.currencyexchanger.view.adapters.UserBalanceAdapter
import com.paysera.currencyexchanger.view.base.BaseActivity
import com.paysera.currencyexchanger.viewModel.activities.main.MainActivityViewModel
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import java.util.ArrayList
import javax.inject.Inject

class MainActivity : BaseActivity<ActivityMainBinding, MainActivityViewModel>() {

    @Inject
    lateinit var mViewModelFactoryActivity: InjectionViewModelProvider<MainActivityViewModel>

    override fun getLayoutId() = R.layout.activity_main
    private var disposable = CompositeDisposable()
    private var myAdapter: UserBalanceAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initUI()
        observeData()

    }


    private fun initUI() {
        viewModel = mViewModelFactoryActivity.get(this, MainActivityViewModel::class)
        initBalanceRv()


    }

    private fun initBalanceRv() {
        binding.mainBalanceRv.apply {

            myAdapter = UserBalanceAdapter()
            layoutManager = LinearLayoutManager(this.context,LinearLayoutManager.HORIZONTAL,false)
            adapter = myAdapter
            viewModel?.getBalanceList {
                Log.e("QQQ", "initBalanceRv: ${it.size}")
                myAdapter?.submitList(it)
            }
        }

    }


    private fun observeData() {
        viewModel?.currencyLiveData?.observe(this) {

        }
        viewModel?.apply {
            this@MainActivity.disposable.add(databaseUpdateProcessor.subscribe {
                Log.e("QQQ", "observeData: on main")
                myAdapter?.submitList(it)

            })

        }

    }


    override fun onDestroy() {
        super.onDestroy()
        disposable.dispose()
    }
}