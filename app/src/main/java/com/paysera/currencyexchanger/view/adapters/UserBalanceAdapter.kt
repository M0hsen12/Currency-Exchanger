package com.paysera.currencyexchanger.view.adapters

import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import com.paysera.currencyexchanger.R
import com.paysera.currencyexchanger.di.data.database.entity.WalletEntity
import com.paysera.currencyexchanger.util.makeDoubleToDecimalFormat
import kotlinx.android.synthetic.main.item_user_balance.view.*

class UserBalanceAdapter() :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    val DIFF_CALLBACK = object : DiffUtil.ItemCallback<WalletEntity>() {

        override fun areItemsTheSame(oldItem: WalletEntity, newItem: WalletEntity): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: WalletEntity, newItem: WalletEntity): Boolean {
            return oldItem.symbolName == newItem.symbolName
        }

    }
    private val differ = AsyncListDiffer(this, DIFF_CALLBACK)


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        return HolderClass(
            LayoutInflater.from(parent.context).inflate(
                R.layout.item_user_balance,
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is HolderClass -> {
                holder.bind(differ.currentList.get(position))
            }
        }
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    fun submitList(list: List<WalletEntity>) {
        differ.submitList(list)
    }

    class HolderClass
    constructor(
        itemView: View
    ) : RecyclerView.ViewHolder(itemView) {

        fun bind(item: WalletEntity) = with(itemView) {

            itemView.item_user_balance_txt.text = "${makeDoubleToDecimalFormat(item.amount)} ${item.symbolName}"
        }
    }
}

