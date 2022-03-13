package com.paysera.currencyexchanger.view.activities.main

import android.os.Bundle
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.paysera.currencyexchanger.R
import com.paysera.currencyexchanger.databinding.ActivityMainBinding
import com.paysera.currencyexchanger.di.viewModelsInjections.InjectionViewModelProvider
import com.paysera.currencyexchanger.model.Currency
import com.paysera.currencyexchanger.util.CurrencyDetail
import com.paysera.currencyexchanger.util.materialPickerDialog
import com.paysera.currencyexchanger.view.adapters.UserBalanceAdapter
import com.paysera.currencyexchanger.view.base.BaseActivity
import com.paysera.currencyexchanger.viewModel.activities.main.MainActivityViewModel
import io.reactivex.disposables.CompositeDisposable
import javax.inject.Inject

class MainActivity : BaseActivity<ActivityMainBinding, MainActivityViewModel>() {

    @Inject
    lateinit var mViewModelFactoryActivity: InjectionViewModelProvider<MainActivityViewModel>

    override fun getLayoutId() = R.layout.activity_main
    private var disposable = CompositeDisposable()
    private var myAdapter: UserBalanceAdapter? = null
    private var sellState = CurrencyDetail.EUR
    private var receiveState = CurrencyDetail.USD

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initUI()
        observeData()

    }


    private fun initUI() {
        viewModel = mViewModelFactoryActivity.get(this, MainActivityViewModel::class)
        initBalanceRv()
        initCurrencyPickers()


    }

    private fun initCurrencyPickers() {
        binding.mainSellPicker.apply {
            setOnClickListener {
                showPickerDialog(true, this)
            }
        }
        binding.mainReceivePicker.apply {
            setOnClickListener {
                showPickerDialog(false, this)
            }
        }

    }

    private fun showPickerDialog(isSelling: Boolean, textView: TextView) {
        val list = viewModel?.getCurrencyList().orEmpty()

        materialPickerDialog(
            this,
            list,
            if (isSelling) getString(R.string.sell_currency_txt) else getString(R.string.buy_currency_txt)
        ) { txt, dialog ->
            if (isSelling) sellState = CurrencyDetail.valueOf(txt)
            else receiveState = CurrencyDetail.valueOf(txt)
            textView.text = txt
            dialog.dismiss()
            if (sellState == receiveState)
                Toast.makeText(this, getString(R.string.same_currency), Toast.LENGTH_LONG).show()
        }.show()


    }

    private fun initBalanceRv() {
        binding.mainBalanceRv.apply {

            myAdapter = UserBalanceAdapter()
            layoutManager = LinearLayoutManager(this.context, LinearLayoutManager.HORIZONTAL, false)
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