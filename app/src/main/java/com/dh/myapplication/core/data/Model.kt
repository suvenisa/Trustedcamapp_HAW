package com.dh.myapplication.core.data

data class FlashConfig(
    val hash: String,
    val milliseconds: Int,
    var binary : List<Boolean>
)


data class FlashStatus(
    val currentFlashCount: Int = 0,
    val totalFlashCount: Int = 0,
    val currentSecond: Int = 0,
    val totalSeconds: Int = 0
)


data class DeviceState(
    val title: String,
    val description : String
)

data class UserAddress(
    var city : String = "",
    var state : String = "",
    var country : String = "",
    val lan : Double = 0.0,
    var lon: Double = 0.0,
    val time: String = "",
    val date: String = ""
)