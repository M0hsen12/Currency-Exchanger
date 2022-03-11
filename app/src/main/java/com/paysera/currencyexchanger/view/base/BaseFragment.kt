package com.paysera.currencyexchanger.view.base

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import dagger.android.support.DaggerFragment

 // FOR FUTURE EXPANSION
abstract class BaseFragment<T : ViewDataBinding, V : BaseViewModel> : DaggerFragment(){

    lateinit var binding: T

    open var viewModel: V? = null


    @LayoutRes
    abstract fun getLayoutId(): Int

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, getLayoutId(), container, false)
        return binding.root
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        val fragments = childFragmentManager.fragments
        fragments.takeIf { it.isNotEmpty() }
            ?.let { it[it.size - 1]?.onActivityResult(requestCode, resultCode, data) }
    }


}