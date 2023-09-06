package com.dh.myapplication.core.flash

import com.dh.myapplication.core.FlashItems

class FlashUtils {

    companion object {
        private const val TAG = "FlashUtils"
    }

    fun splitBinarySequence(sequence: List<Boolean>): List<FlashItems> {
        val result = mutableListOf<FlashItems>()
        var currentStatus = sequence.firstOrNull() ?: false
        var currentDuration = 0
        var totalCount = 0
        var index = 0
        for (element in sequence) {
            if (element == currentStatus) {
                currentDuration += 50
                totalCount++
            } else {
                result.add(FlashItems(index = index ,status = currentStatus, totalDuration = currentDuration, count = totalCount))
                index++
                currentStatus = element
                currentDuration = 50
                totalCount = 0
            }
        }


        return result
    }


}