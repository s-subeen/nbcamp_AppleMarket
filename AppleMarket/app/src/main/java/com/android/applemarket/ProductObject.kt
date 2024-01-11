package com.android.applemarket

import java.text.DecimalFormat

object ProductObject {
    fun Int.decimalFormat(): String {
        val dec = DecimalFormat("#,###")
        return dec.format(this)
    }
}