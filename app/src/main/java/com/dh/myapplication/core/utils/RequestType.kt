package com.dh.myapplication.core.utils

sealed class RequestType() {
    object Location : RequestType()
    object Bio : RequestType()

}
