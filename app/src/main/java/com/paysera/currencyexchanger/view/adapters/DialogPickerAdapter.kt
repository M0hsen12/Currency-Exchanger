package com.paysera.currencyexchanger.view.adapters

import android.content.Context
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.res.ResourcesCompat
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.paysera.currencyexchanger.R
import com.paysera.currencyexchanger.databinding.ItemDialogPickerBinding

class DialogPickerAdapter(
    val context: Context,
    val list: List<String>,
    private val onItemClick: (name: String) -> Unit,
) :
    RecyclerView.Adapter<DialogPickerAdapter.ViewHolderItemSliderHeader>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderItemSliderHeader {
        val binder = DataBindingUtil.inflate<ItemDialogPickerBinding>(
            LayoutInflater.from(context),
            R.layout.item_dialog_picker,
            parent,
            false
        )
        return ViewHolderItemSliderHeader(binder.root, binder)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: ViewHolderItemSliderHeader, position: Int) {
        val child = list[position]
        holder.binder?.itemPickerTitle?.apply {

            text = child
            setOnClickListener {
                onItemClick.invoke(child)
            }
        }

    }


    inner class ViewHolderItemSliderHeader(itemView: View, val binder: ItemDialogPickerBinding?) :
        RecyclerView.ViewHolder(itemView)

}
