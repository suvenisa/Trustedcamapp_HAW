package com.dh.myapplication.ui

sealed class AppScreens(val route: String) {


    object DashboardTwo : AppScreens ("DashboardTwo")
    object Permission : AppScreens ("Permission")
    object BarCode : AppScreens ("BarCode")


}

