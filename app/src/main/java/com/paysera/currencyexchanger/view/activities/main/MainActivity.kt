package com.paysera.currencyexchanger.view.activities.main

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.paysera.currencyexchanger.R
import com.paysera.currencyexchanger.databinding.ActivityMainBinding
import com.paysera.currencyexchanger.di.viewModelsInjections.InjectionViewModelProvider
import com.paysera.currencyexchanger.view.base.BaseActivity
import com.paysera.currencyexchanger.viewModel.activities.main.MainActivityViewModel
import javax.inject.Inject

class MainActivity : BaseActivity<ActivityMainBinding, MainActivityViewModel>()  {

    @Inject
    lateinit var mViewModelFactoryActivity: InjectionViewModelProvider<MainActivityViewModel>

    override fun getLayoutId() = R.layout.activity_main

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }
}