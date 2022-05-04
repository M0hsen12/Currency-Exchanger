package com.paysera.currencyexchanger.view.activities.main

import android.content.Context
import android.net.ConnectivityManager
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
import com.paysera.currencyexchanger.di.data.database.entity.TransactionsEntity
import com.paysera.currencyexchanger.di.data.database.entity.WalletEntity
import com.paysera.currencyexchanger.di.viewModelsInjections.InjectionViewModelProvider
import com.paysera.currencyexchanger.model.Currency
import com.paysera.currencyexchanger.util.*
import com.paysera.currencyexchanger.view.adapters.UserBalanceAdapter
import com.paysera.currencyexchanger.view.base.BaseActivity
import com.paysera.currencyexchanger.viewModel.activities.main.MainActivityViewModel
import com.paysera.currencyexchanger.viewModel.activities.main.MainActivityViewModel.Companion.CommissionFeePercentage
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
    private var latestCurrency: Currency? = null
    private var userBalanceList = ArrayList<WalletEntity>()
    private var sellAmount = 0.0
    private var receivesAmount = 0.0
    private var commissionFeeAmount = 0.0
    private var transactionCount = 0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initUI()
        observeData()

    }


    private fun initUI() {
        viewModel = mViewModelFactoryActivity.get(this, MainActivityViewModel::class)
        checkForInternetConnection()
        initBalanceRv()
        initCurrencyPickers()
        initEditTexts()
        initSubmitBtn()


    }

    private fun checkForInternetConnection() {
        if (!isInternetAvailable(this))
            showConnectionDialog().show()
    }

    private fun showConnectionDialog() =
        materialDialog(
            context = this,
            cancelable = true,
            title = getString(R.string.titleConnectionError),
            msg = getString(R.string.msgConnectionError),
            onPositiveClicked = {
                it.dismiss()
            }
        )


    private fun initSubmitBtn() {
        binding.mainSubmitBtn.setOnClickListener {
            if (isInternetAvailable(this)){
                if (binding.mainSellEt.text.isEmpty()) {
                    showToast(getString(R.string.empty_et))
                    return@setOnClickListener
                }
                viewModel?.getBalanceAndTransactionForSubmit(sellState.symbolName) {
                    validateSubmitBtn(it)
                }
            } else showConnectionDialog().show()

        }

    }

    private fun validateSubmitBtn(it: Pair<WalletEntity, List<TransactionsEntity>>) {
        val currentSellValue = binding.mainSellEt.text.toString().toDouble()

        when {
            sellState == receiveState ->
                showToast(getString(R.string.same_currency))

            currentSellValue > (it.first.amount ?: 0 + getPossibleCommission(
                it.second,
                currentSellValue
            )) -> {
                showToast(getString(R.string.sell_amount_higher))
                return
            }

            else ->
                updateDatabaseAfterSubmit()


        }
    }

    private fun getPossibleCommission(
        list: List<TransactionsEntity>,
        currentSellValue: Double
    ): Double {

        return when {
            list.isEmpty() -> 0.0
            list.first().count ?: 0 <= 5 -> 0.0
            else -> getCommission(list.first().count ?: 0, currentSellValue)
        }

    }


    private fun updateDatabaseAfterSubmit() {
        viewModel?.findWalletItemsWithName(sellState.symbolName, receiveState.symbolName) {

            viewModel?.getTransaction { transition ->

                doTransactionStaff(transition)

                doWalletStaff(it)

            }
        }
    }

    private fun doWalletStaff(it: Pair<WalletEntity, WalletEntity>) {

        it.first.amount = it.first.amount?.minus(sellAmount + commissionFeeAmount)
        it.second.amount = it.second.amount?.plus(receivesAmount)

        updateWalletForDatabase(it.first, it.second)

    }

    private fun doTransactionStaff(transition: TransactionsEntity) {
        transition.count?.plus(1)?.let {
            transition.count = it
            transactionCount = it
        }
        getCommission(transition.count ?: 0, sellAmount).let {
            commissionFeeAmount = it
            transition.totalAmount = it
        }

        updateTransactionDatabase(transition)

    }

    private fun updateTransactionDatabase(transition: TransactionsEntity) {
        viewModel?.updateTransactionDatabase(transition) {
            Log.e("eee", "updateTransactionDatabase: transaction done")
        }
    }

    private fun getCommission(transitionCount: Int, sellAmount: Double): Double {
        return if (transitionCount <= 5)
            0.0
        else (CommissionFeePercentage / sellAmount) * 100
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


    }

    private fun getUserSellCurrencyAmount(): Double =
        userBalanceList.find { it.symbolName == sellState.symbolName }?.amount ?: 0.0

    private fun changeCurrency(amount: String) {
        if (sellState == receiveState) {
            showToast(getString(R.string.same_currency))
            return
        }

        if (amount.toDouble() < getUserSellCurrencyAmount()) {

            receivesAmount = amount.toDouble() * calculateTheCurrencyRate()
            sellAmount = amount.toDouble()
            binding.mainReceivesTv.text = "+${showOnlyTwoDigitOfDouble(receivesAmount)}"

        } else Toast.makeText(this, "amount Is Higher than Your Balance", Toast.LENGTH_SHORT).show()


    }

    private fun calculateTheCurrencyRate(): Double {

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

            clearEditTexts()

            textView.text = txt

            CurrencyDetail.valueOf(txt).let {
                if (isSelling) sellState = it else receiveState = it
            }

            dialog.dismiss()
        }.show()


    }

    private fun showToast(msg: String) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
    }

    private fun initBalanceRv() {
        binding.mainBalanceRv.apply {

            myAdapter = UserBalanceAdapter()
            layoutManager = LinearLayoutManager(this.context, LinearLayoutManager.HORIZONTAL, false)
            adapter = myAdapter
            viewModel?.getBalanceList {
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
                latestCurrency = it

            }
            this@MainActivity.disposable.add(databaseUpdateProcessor.subscribe {
                updateWalletListForUI(it)

            })
            updatedWalletLiveData.observe(this@MainActivity) {
                updateWalletListForUI(it.third)
                clearEditTexts()
                showSuccessDialog()
            }
            errorLiveData.observe(this@MainActivity) {
                Log.e("TAG", "observeData:${it.message} ")
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
            refreshAmounts()
        }.show()

    }

    private fun refreshAmounts() {

        commissionFeeAmount = 0.0
        sellAmount = 0.0
        receivesAmount = 0.0
    }

    private fun createSuccessMsg(): String =
        "You have Converted ${makeDoubleToDecimalFormat(sellAmount)} ${sellState.symbolName} to ${
            makeDoubleToDecimalFormat(receivesAmount)
        } ${receiveState.symbolName} . Commission fee ${
            if (commissionFeeAmount == 0.0) " for first 5 transaction is free and your count is $transactionCount " else
                makeDoubleToDecimalFormat(
                    commissionFeeAmount
                ).plus(" ") + sellState.symbolName
        } ."


    override fun onDestroy() {
        super.onDestroy()
        disposable.dispose()
    }
}