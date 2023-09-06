package com.dh.myapplication.core

data class FlashlightStatus(
    val index : Int = 0,
    val status : Boolean = false
)


data class FlashItems(var index : Int ,val status: Boolean, val totalDuration: Int, val count: Int)


