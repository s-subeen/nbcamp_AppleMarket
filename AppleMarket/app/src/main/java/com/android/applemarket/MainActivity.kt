package com.android.applemarket

import android.app.AlertDialog
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Intent
import android.media.AudioAttributes
import android.media.RingtoneManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.ArrayAdapter
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.NotificationCompat
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.applemarket.DetailActivity.Companion.EXTRA_PREFERENCE_STATUS
import com.android.applemarket.DetailActivity.Companion.EXTRA_PRODUCT_ID
import com.android.applemarket.ProductManager.getIndexProductItem
import com.android.applemarket.ProductManager.loadList
import com.android.applemarket.ProductManager.removeProductItem
import com.android.applemarket.ProductManager.setPreferenceStatus
import com.android.applemarket.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    companion object {
        const val EXTRA_PRODUCT_ENTITY = "extra_product_entity"
        const val NOTIFICATION_CHANNEL_ID = "one-channel"
        const val NOTIFICATION_ID = 11
    }

    private val binding: ActivityMainBinding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }

    private lateinit var productAdapter: ProductAdapter

    private lateinit var activityResultLauncher: ActivityResultLauncher<Intent>

    private val onBackPressedCallback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            showBackPressedAlertDialog()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        initView()
        setActivityResultLauncher()
        this.onBackPressedDispatcher.addCallback(this, onBackPressedCallback)
    }

    private fun initView() {
        initRecyclerView()
        setSpinnerLocation()
        with(binding) {
            ivNotification.setOnClickListener {
                showNotification()
            }
            floatingButton.setOnClickListener {
                binding.productRecyclerView.smoothScrollToPosition(0) // 최상단 이동
            }
        }
    }

    private fun setActivityResultLauncher() {
        /*
        registerForActivityResult 함수를 사용해 ActivityLauncher 생성
        전달 받은 데이터 (상품 아이디, 좋아요 상태)
         */
        activityResultLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) {
            if (it.resultCode == RESULT_OK) {
                val productId = it.data?.getIntExtra(EXTRA_PRODUCT_ID, -1) ?: -1
                val status = it.data?.getBooleanExtra(EXTRA_PREFERENCE_STATUS, false)

                if (productId < 0 || status == null) {
                    return@registerForActivityResult
                }

                setPreferenceStatus(
                    productId,
                    status
                )

                val position = getIndexProductItem(productId) // product id로 위치 가져 오기
                if (position >= 0) {
                    productAdapter.notifyItemChanged(position) // RecyclerView update
                }
            }
        }
    }

    private fun initRecyclerView() { // RecyclerView 초기화
        val items = loadList()
        productAdapter = ProductAdapter(applicationContext, items)

        binding.productRecyclerView.run {
            layoutManager = LinearLayoutManager(this@MainActivity)
            addItemDecoration(
                DividerItemDecoration(
                    this@MainActivity,
                    LinearLayoutManager.VERTICAL
                )
            )
            this.adapter = productAdapter
            addOnScrollListener(createScrollListener())
        }

        productAdapter.itemClick = object : ProductAdapter.ItemClick {
            override fun onItemClick(view: View, position: Int) {
                val intent = Intent(this@MainActivity, DetailActivity::class.java)
                val data = items[position]
                intent.putExtra(EXTRA_PRODUCT_ENTITY, data)
                activityResultLauncher.launch(intent)
            }

            override fun onItemLongLick(view: View, position: Int) {
                showAlertDialog(
                    getString(R.string.dialog_remove_title),
                    getString(R.string.dialog_remove_message),
                    R.drawable.img_main_chat_16dp,
                    getString(R.string.dialog_button_positive),
                    {
                        if (removeProductItem(position)) { // 아이템 삭제가 완료 됐을 때
                            productAdapter.notifyItemRangeRemoved(position, items.size) // RecyclerView update
                        }
                    },
                    getString(R.string.dialog_button_negative)
                )
            }
        }
    }

    private fun createScrollListener(): RecyclerView.OnScrollListener {
        return object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                with(binding.floatingButton) {
                    if (!binding.productRecyclerView.canScrollVertically(-1)) { // 최상단 일 때
                        animate().alpha(0f).duration = 200
                        visibility = GONE
                    } else {
                        animate().alpha(1f).duration = 200
                        visibility = VISIBLE
                    }
                }
            }
        }
    }

    private fun showBackPressedAlertDialog() { // 뒤로 가기
        showAlertDialog(
            getString(R.string.dialog_title),
            getString(R.string.dialog_message),
            R.drawable.img_main_chat_16dp,
            getString(R.string.dialog_button_positive),
            { finish() },
            getString(R.string.dialog_button_negative)
        )
    }

    private fun showAlertDialog( // Dialog
        title: String,
        message: String,
        iconResId: Int,
        positiveButtonText: String,
        positiveAction: () -> Unit,
        negativeButtonText: String,
        negativeAction: (() -> Unit)? = null
    ) {
        AlertDialog.Builder(this@MainActivity).apply {
            setTitle(title)
            setMessage(message)
            setIcon(iconResId)
            setPositiveButton(positiveButtonText) { _, _ -> positiveAction.invoke() }
            setNegativeButton(negativeButtonText, null)
            negativeAction?.let { setNegativeButton(negativeButtonText) { _, _ -> it.invoke() } }
        }.show()
    }

    private fun setSpinnerLocation() { // 위치 스피너
        binding.spinnerLocation.adapter = ArrayAdapter(
            this@MainActivity,
            android.R.layout.simple_spinner_dropdown_item,
            listOf(
                getString(R.string.location_0)
            )
        )
    }

    private fun showNotification() {
        createNotificationChannel()

        val builder = NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID).apply {
            setSmallIcon(R.mipmap.ic_launcher)
            setWhen(System.currentTimeMillis())
            setContentTitle(getString(R.string.notification_title))
            setContentText(getString(R.string.notification_message))
        }

        val manager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        manager.notify(NOTIFICATION_ID, builder.build())
    }

    private fun createNotificationChannel() { // 알림 설정
        val channel = NotificationChannel(
            NOTIFICATION_CHANNEL_ID,
            getString(R.string.notification_channel_name),
            NotificationManager.IMPORTANCE_DEFAULT
        ).apply {
            description = getString(R.string.notification_channel_description)
            enableVibration(true)
        }

        val uri: Uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val audioAttributes = AudioAttributes.Builder()
            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
            .setUsage(AudioAttributes.USAGE_ALARM)
            .build()
        channel.setSound(uri, audioAttributes)

        val manager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        manager.createNotificationChannel(channel)
    }
}