package com.paysera.currencyexchanger.view.activities.main

import android.os.Bundle
import android.text.Editable
import android.text.InputFilter
import android.text.TextWatcher
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.paysera.currencyexchanger.R
import com.paysera.currencyexchanger.databinding.ActivityMainBinding
import com.paysera.currencyexchanger.di.data.database.entity.WalletEntity
import com.paysera.currencyexchanger.di.viewModelsInjections.InjectionViewModelProvider
import com.paysera.currencyexchanger.model.Currency
import com.paysera.currencyexchanger.util.*
import com.paysera.currencyexchanger.view.adapters.UserBalanceAdapter
import com.paysera.currencyexchanger.view.base.BaseActivity
import com.paysera.currencyexchanger.viewModel.activities.main.MainActivityViewModel
import io.reactivex.disposables.CompositeDisposable
import java.util.ArrayList
import javax.inject.Inject

class MainActivity : BaseActivity<ActivityMainBinding, MainActivityViewModel>() {

    @Inject
    lateinit var mViewModelFactoryActivity: InjectionViewModelProvider<MainActivityViewModel>

    override fun getLayoutId() = R.layout.activity_main
    private var disposable = CompositeDisposable()
    private var myAdapter: UserBalanceAdapter? = null
    private var sellState = CurrencyDetail.EUR
    private var receiveState = CurrencyDetail.USD
    private var latestCurrency: Currency? = null
    private var userBalanceList = ArrayList<WalletEntity>()
    private var sellAmount = 0.0
    private var receivesAmount = 0.0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initUI()
        observeData()

    }


    private fun initUI() {
        viewModel = mViewModelFactoryActivity.get(this, MainActivityViewModel::class)
        initBalanceRv()
        initCurrencyPickers()
        initEditTexts()
        initSubmitBtn()


    }

    private fun initSubmitBtn() {
        binding.mainSubmitBtn.apply {
            setOnClickListener {
                if (sellState == receiveState || binding.mainSellEt.text.isEmpty()) {
                    sameStateToast()
                    return@setOnClickListener
                }
                updateDatabaseAfterSubmit()


            }

        }
    }

    private fun updateDatabaseAfterSubmit() {
        viewModel?.findWalletItemsWithName(sellState.symbolName, receiveState.symbolName) {
            Log.e(
                "eee",
                "updateDatabaseAfterSubmit:${it.first.symbolName} ,, ${it.first.amount} "
            )
            it.first.amount = it.first.amount?.minus(sellAmount)
            it.second.amount = it.second.amount?.plus(receivesAmount)
            Log.e("eee", "updateDatAFTER: ${it.first.symbolName} ,, ${it.first.amount} ")
            updateWalletForDatabase(it.first, it.second)


        }

    }

    private fun updateWalletForDatabase(sell: WalletEntity, receives: WalletEntity) {

        viewModel?.updateDatabaseAfterTransaction(sell, receives)

    }


    private fun initEditTexts() {
        binding.mainSellEt.apply {
            filters = (arrayOf<InputFilter>(DecimalDigitsInputFilter(4, 2)))

            addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                }

                override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                }

                override fun afterTextChanged(p0: Editable?) {
                    if (p0?.length ?: 0 == 0)
                        binding.mainReceivesTv.text = ""
                    else
                        changeCurrency(p0.toString())
                }

            })


        }

        binding.mainReceivesTv.apply {

        }


    }

    private fun changeCurrency(amount: String) {
        if (sellState == receiveState) {
            sameStateToast()
            return
        }
        val userCurrentSellAmount =
            userBalanceList.find { it.symbolName == sellState.symbolName }?.amount ?: 0.0
        if (amount.toDouble() < userCurrentSellAmount) {

            receivesAmount = amount.toDouble() * calculateTheCurrencyRate()
            sellAmount = amount.toDouble()
            binding.mainReceivesTv.text = "+${showOnlyTwoDigitOfDouble(receivesAmount)}"

        } else Toast.makeText(this, "amount Is Higher than Your Balance", Toast.LENGTH_SHORT).show()


    }

    private fun calculateTheCurrencyRate(): Double {
        Log.e(
            "TAG",
            "CalculateTheCurrencyRate:${sellState.CurrencyRate / receiveState.CurrencyRate} === ${sellState.CurrencyRate} === ${receiveState.CurrencyRate}  "
        )
        return receiveState.CurrencyRate / sellState.CurrencyRate

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

    private fun clearEditTexts() {
        binding.mainReceivesTv.text = ""
        binding.mainSellEt.text.clear()
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
            clearEditTexts()
            textView.text = txt
            dialog.dismiss()
            if (sellState == receiveState)
                sameStateToast()
        }.show()


    }

    private fun sameStateToast() {
        Toast.makeText(this, getString(R.string.same_currency), Toast.LENGTH_LONG).show()
    }

    private fun initBalanceRv() {
        binding.mainBalanceRv.apply {

            myAdapter = UserBalanceAdapter()
            layoutManager = LinearLayoutManager(this.context, LinearLayoutManager.HORIZONTAL, false)
            adapter = myAdapter
            viewModel?.getBalanceList {
                Log.e("QQQ", "initBalanceRv: ${it.size}")
                updateWalletListForUI(it)
            }
        }

    }

    private fun updateWalletListForUI(list: List<WalletEntity>) {
        userBalanceList = list as ArrayList<WalletEntity>
        myAdapter?.submitList(list)
    }

    private fun observeData() {

        viewModel?.apply {
            currencyLiveData.observe(this@MainActivity) {
                Log.e("WWW", "observeData: currency rate USD ${CurrencyDetail.USD.CurrencyRate}")
                latestCurrency = it

            }
            this@MainActivity.disposable.add(databaseUpdateProcessor.subscribe {
                Log.e("QQQ", "observeData: on main")
                userBalanceList = it as ArrayList<WalletEntity>
                myAdapter?.submitList(it)

            })
            updatedWalletLiveData.observe(this@MainActivity) {
                updateWalletListForUI(it.third)
                clearEditTexts()
                showSuccessDialog()
            }
        }


    }

    private fun showSuccessDialog() {

        materialDialog(
            context = this,
            cancelable = false,
            title = getString(R.string.succes_transaction),
            msg = createSuccessMsg()
        ) {
            it.dismiss()
        }.show()

    }

    private fun createSuccessMsg(): String =
        "You have Converted ${makeDoubleToDecimalFormat(sellAmount)} ${sellState.symbolName} to ${
            makeDoubleToDecimalFormat(receivesAmount)
        } ${receiveState.symbolName} . Commission free -0.70 ${sellState.symbolName}"


    override fun onDestroy() {
        super.onDestroy()
        disposable.dispose()
    }
}