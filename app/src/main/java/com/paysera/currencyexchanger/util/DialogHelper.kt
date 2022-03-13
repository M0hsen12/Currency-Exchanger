package com.paysera.currencyexchanger.util

import android.app.Dialog
import android.content.Context
import android.view.LayoutInflater
import android.view.Window
import androidx.annotation.StyleRes
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.paysera.currencyexchanger.R
import com.paysera.currencyexchanger.databinding.DialogPickerBinding
import com.paysera.currencyexchanger.databinding.DialogSimpleProgressBinding
import com.paysera.currencyexchanger.view.adapters.DialogPickerAdapter


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

fun materialPickerDialog(
    context: Context,
    list: List<String>,
    title:String,
    onItemClick: (name: String,dialog:Dialog) -> Unit
): Dialog {
    return Dialog(context, R.style.ThemeDialog_Dark).apply {
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setCancelable(false)
        val binder = DataBindingUtil.inflate<DialogPickerBinding>(
            LayoutInflater.from(context),
            R.layout.dialog_picker,
            null,
            false
        )
        binder.pickerTitle.text = title
        binder.pickerRv.apply rv@{

            layoutManager = LinearLayoutManager(context)
            adapter = DialogPickerAdapter(context, list) {
                onItemClick.invoke(it,this@apply)
            }
        }
        setContentView(binder.root)
    }
}
