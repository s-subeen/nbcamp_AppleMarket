package com.android.applemarket

import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.OnBackPressedCallback
import com.android.applemarket.Constants.EXTRA_PREFERENCE_STATUS
import com.android.applemarket.Constants.EXTRA_PRODUCT_ENTITY
import com.android.applemarket.Constants.EXTRA_PRODUCT_ID
import com.android.applemarket.ProductExtension.decimalFormat
import com.android.applemarket.databinding.ActivityDetailBinding
import com.google.android.material.snackbar.Snackbar

class DetailActivity : AppCompatActivity() {
    private val binding: ActivityDetailBinding by lazy {
        ActivityDetailBinding.inflate(layoutInflater)
    }

    private val productEntity: ProductEntity? by lazy {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent?.getParcelableExtra(
                EXTRA_PRODUCT_ENTITY, ProductEntity::class.java
            )
        } else {
            intent?.getParcelableExtra(
                EXTRA_PRODUCT_ENTITY
            )
        }
    }

    private val onBackPressedCallback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            setResult()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        initView()
        this.onBackPressedDispatcher.addCallback(this, onBackPressedCallback)
    }

    private fun initView() {
        setImageViewStatus()
        setProductEntity()
        with(binding) {
            ivBackwards.setOnClickListener { // 뒤로 가기 버튼
                setResult()
            }
            ivDetailPreference.setOnClickListener { // 좋아요 버튼
                it.isSelected = !it.isSelected
                showSnackBar(it.isSelected)
            }
        }
    }

    private fun setResult() { // DetailActivity -> MainActivity
        Intent().run {
            putExtra(EXTRA_PRODUCT_ID, productEntity?.id) // 상품 아이디
            putExtra(EXTRA_PREFERENCE_STATUS, binding.ivDetailPreference.isSelected) // 좋아요 버튼 상태
            setResult(RESULT_OK, this)
        } // run, with, let
        if (isFinishing.not()) finish()
    }


    private fun showSnackBar(selected: Boolean) {
        if (selected) {
            Snackbar.make(
                binding.detailLayout,
                getString(R.string.text_detail_snack_bar),
                Snackbar.LENGTH_SHORT
            ).show()
        }
    }

    private fun setImageViewStatus() { // 하트 이미지
        binding.ivDetailPreference.isSelected = productEntity?.preferenceStatus == true // 버튼 상태
    }

    private fun setProductEntity() { // 전달 받은 값 화면에 표시
        productEntity?.resId?.let { binding.ivDetailImage.setImageResource(it) }
        binding.tvDetailName.text = productEntity?.name
        binding.tvDetailExplain.text = productEntity?.explain
        binding.tvDetailSeller.text = productEntity?.seller
        binding.tvDatailPrice.text =
            productEntity?.price?.decimalFormat() + getString(R.string.text_won)
        binding.tvDetailLocation.text = productEntity?.location
    }

}
