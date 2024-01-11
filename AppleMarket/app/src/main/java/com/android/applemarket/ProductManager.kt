package com.android.applemarket

import android.content.Context
import java.io.BufferedReader
import java.io.InputStreamReader
import java.lang.IndexOutOfBoundsException

object ProductManager {
    private val items: MutableList<ProductEntity> = arrayListOf()

    fun Context.loadList(): MutableList<ProductEntity> {
        items.clear()

        val assetManager = assets
        val inputStream = assetManager.open("dummy_data.tsv")
        val bufferedReader = BufferedReader(InputStreamReader(inputStream))
        bufferedReader.forEachLine {
            val tokens = it.split("\t")
            val resource = resources.getIdentifier(tokens[1], "drawable", packageName)
            val post = ProductEntity(
                tokens[0].toInt(),
                resource,
                tokens[2],
                tokens[3].replace("\\n", "\n").replace(" + ", "").replace("\"", ""),
                tokens[4],
                tokens[5].toInt(),
                tokens[6],
                tokens[7].toInt(),
                tokens[8].toInt()
            )
            items.add(post)
        }
        return items
    }

    fun removeProductItem(position: Int): Boolean = // 해당 위치의 아이템 삭제
        try {
            items.removeAt(position)
            true
        } catch (e: IndexOutOfBoundsException) {
            false
        }

    fun setPreferenceStatus(productId: Int, status: Boolean) { // 좋아요 상태에 따른 값 수정
        val position = getIndexProductItem(productId)
        if (position >= 0) {
            val item = items[position]
            if (item.preferenceStatus != status) {
                item.preference += if (status) 1 else -1
                item.preferenceStatus = !item.preferenceStatus
            }
        }
    }

    // id로 위치 가져 오기
    fun getIndexProductItem(productId: Int): Int = items.indexOfFirst { it.id == productId }
}