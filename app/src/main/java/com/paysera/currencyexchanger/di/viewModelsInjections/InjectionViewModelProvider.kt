package com.paysera.currencyexchanger.di.viewModelsInjections


import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import javax.inject.Inject
import kotlin.reflect.KClass

class InjectionViewModelProvider<VM : ViewModel> @Inject constructor(var viewModel: VM) {

    @Suppress("UNCHECKED_CAST")
    val viewModelFactory = object :ViewModelProvider.Factory{
        override fun <T : ViewModel> create(modelClass: Class<T>) = viewModel as T

    }

    fun <ACTIVITY : FragmentActivity> get(activity: ACTIVITY, viewModelClass: KClass<VM>) =
            ViewModelProvider(activity, viewModelFactory).get(viewModelClass.java)

    fun <FRAGMENT : Fragment> get(fragment: FRAGMENT, viewModelClass: KClass<VM>) =
        ViewModelProvider(fragment, viewModelFactory).get(viewModelClass.java)
}

