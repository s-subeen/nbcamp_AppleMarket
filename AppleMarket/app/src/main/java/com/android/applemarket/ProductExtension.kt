package com.android.applemarket

import java.text.DecimalFormat

object ProductExtension {
    fun Int.decimalFormat(): String {
        val dec = DecimalFormat("#,###")
        return dec.format(this)
    }
}