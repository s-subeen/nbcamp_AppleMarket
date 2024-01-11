package com.android.applemarket

import android.content.Intent
import android.graphics.Paint
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.android.applemarket.MainActivity.Companion.EXTRA_PRODUCT_ENTITY
import com.android.applemarket.ProductManager.setPreferenceStatus
import com.android.applemarket.ProductObject.decimalFormat
import com.android.applemarket.databinding.ActivityDetailBinding
import com.google.android.material.snackbar.Snackbar

class DetailActivity : AppCompatActivity() {
    companion object {
        const val EXTRA_PRODUCT_ID = "extra_product_id"
        const val EXTRA_PREFERENCE_STATUS = "extra_preference_status"
    }

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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        initView()
    }

    private fun initView() {
        setImageViewStatus()
        setUnderLineText()
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

    private fun setUnderLineText() {
        binding.tvMannersTemperature.paintFlags = Paint.UNDERLINE_TEXT_FLAG // 텍스트 밑줄
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
