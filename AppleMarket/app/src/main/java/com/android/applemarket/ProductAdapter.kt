package com.android.applemarket

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.android.applemarket.ProductExtension.decimalFormat
import com.android.applemarket.databinding.ItemProductRecyclerBinding

class ProductAdapter(
    private val context: Context,
    private var items: MutableList<ProductEntity>
) :
    RecyclerView.Adapter<ProductAdapter.Holder>() {

    interface ItemClick {
        fun onItemClick(view: View, position: Int)
        fun onItemLongLick(view: View, position: Int)
    }

    var itemClick: ItemClick? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductAdapter.Holder {
        val binding =
            ItemProductRecyclerBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return Holder(binding)
    }

    override fun onBindViewHolder(holder: ProductAdapter.Holder, position: Int) {
        val item = items[position]

        with(holder.itemView) {
            setOnClickListener { itemClick?.onItemClick(it, position) }
            setOnLongClickListener {
                itemClick?.onItemLongLick(it, position)
                true
            }
        }

        with(holder) {
            productImageView.setImageResource(item.resId)
            productName.text = item.name
            productLocation.text = item.location
            productPrice.text = item.price?.decimalFormat() + context.getString(R.string.text_won)
            productChatCount.text = item.chat.toString()
            productPreferenceCount.text = item.preference.toString()

            val preferenceImageRes = if (item.preferenceStatus) {
                R.drawable.img_all_like
            } else {
                R.drawable.img_all_emptylike
            }
            preferenceImageView.setImageResource(preferenceImageRes)
        }
    }

    override fun getItemCount(): Int = items.size

    inner class Holder(binding: ItemProductRecyclerBinding) :
        RecyclerView.ViewHolder(binding.root) {
        val productImageView = binding.ivProduct
        val productName = binding.tvProductName
        val productLocation = binding.tvProductLocation
        val productPrice = binding.tvProductPrice
        val productChatCount = binding.tvChatCount
        val productPreferenceCount = binding.tvPreferenceCount
        val preferenceImageView = binding.ivPreference
    }
}