package com.dh.myapplication.core


import android.os.Build

val permissionsList = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
    listOf(  android.Manifest.permission.ACCESS_FINE_LOCATION,
        android.Manifest.permission.ACCESS_COARSE_LOCATION)
} else {
    listOf(  android.Manifest.permission.ACCESS_FINE_LOCATION,
        android.Manifest.permission.ACCESS_COARSE_LOCATION)
}
