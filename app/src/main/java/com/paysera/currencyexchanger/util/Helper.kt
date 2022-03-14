package com.paysera.currencyexchanger.util

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.text.Editable
import android.text.SpannableString
import android.view.ContextThemeWrapper
import android.view.LayoutInflater
import android.view.View
import android.view.Window
import android.widget.Button
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.annotation.StyleRes
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.paysera.currencyexchanger.R
import com.paysera.currencyexchanger.databinding.DialogPickerBinding
import com.paysera.currencyexchanger.databinding.DialogSimpleProgressBinding
import com.paysera.currencyexchanger.view.adapters.DialogPickerAdapter
import java.text.DecimalFormat


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
    title: String,
    onItemClick: (name: String, dialog: Dialog) -> Unit
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
                onItemClick.invoke(it, this@apply)
            }
        }
        setContentView(binder.root)
    }
}


fun materialDialog(
    context: Context,
    cancelable: Boolean = false,
    title: String? = null,
    msg:String?=null,
    onPositiveClicked: ((dialog: DialogInterface) -> Unit)? = null
): AlertDialog.Builder {
    val builder = AlertDialog.Builder(context)
    builder.setTitle(title)
    builder.setMessage(msg)
    builder.setCancelable(cancelable)
    builder.setPositiveButton(R.string.done) { dialog, which ->
onPositiveClicked?.invoke(dialog )
    }
    return builder
}

fun makeDoubleToDecimalFormat(double: Double?):String = DecimalFormat("#0.00").format(double)
fun showOnlyTwoDigitOfDouble(double: Double?): String? = DecimalFormat("#.##").format(double)


