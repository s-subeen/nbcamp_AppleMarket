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
        holder.bind(items[position])

        with(holder.itemView) {
            setOnClickListener { itemClick?.onItemClick(it, position) }
            setOnLongClickListener {
                itemClick?.onItemLongLick(it, position)
                true
            }
        }
    }

    override fun getItemCount(): Int = items.size

    inner class Holder(binding: ItemProductRecyclerBinding) :
        RecyclerView.ViewHolder(binding.root) {
        private val productImageView = binding.ivProduct
        private val productName = binding.tvProductName
        private val productLocation = binding.tvProductLocation
        private val productPrice = binding.tvProductPrice
        private val productChatCount = binding.tvChatCount
        private val productPreferenceCount = binding.tvPreferenceCount
        private val preferenceImageView = binding.ivPreference

        fun bind(item: ProductEntity) {
            productImageView.setImageResource(item.resId)
            productName.text = item.name
            productLocation.text = item.location
            productPrice.text =
                item.price?.decimalFormat() + context.getString(R.string.text_won)
            productChatCount.text = item.chat.toString()
            productPreferenceCount.text = item.preference.toString()

            preferenceImageView.setImageResource(
                if (item.preferenceStatus) {
                    R.drawable.img_all_like
                } else {
                    R.drawable.img_all_emptylike
                }
            )
        }
    }
}