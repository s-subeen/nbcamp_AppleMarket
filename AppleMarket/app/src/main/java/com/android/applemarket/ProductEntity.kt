package com.android.applemarket

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class ProductEntity(
    val id: Int,
    val resId: Int,
    val name: String,
    val explain: String,
    val seller: String,
    val price: Int,
    val location: String,
    val preference: Int, //
    val chat: Int,
    val preferenceStatus: Boolean = false //
) : Parcelable {
    fun copyWithPreference(preference: Int, preferenceStatus: Boolean): ProductEntity {
        return copy(preference = preference, preferenceStatus = preferenceStatus)
    }
}
