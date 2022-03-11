package com.paysera.currencyexchanger.util

import android.app.Dialog
import android.content.Context
import android.view.LayoutInflater
import android.view.Window
import androidx.annotation.StyleRes
import androidx.databinding.DataBindingUtil
import com.paysera.currencyexchanger.R
import com.paysera.currencyexchanger.databinding.DialogSimpleProgressBinding


fun materialSimpleProgressDialog(
    context: Context,
    @StyleRes theme: Int = R.style.ThemeDialog_Dark
): Dialog {
    return Dialog(context, theme).apply {
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setCancelable(false)
        val binder = DataBindingUtil.inflate<DialogSimpleProgressBinding>(
            LayoutInflater.from(context),
            R.layout.dialog_simple_progress,
            null,
            false
        )
        setContentView(binder.root)
    }
}
