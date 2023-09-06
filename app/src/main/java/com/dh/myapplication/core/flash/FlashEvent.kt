package com.dh.myapplication.core.flash

import com.dh.myapplication.core.data.Flash

interface FlashEvent {
    fun onFlashEntry(flash: Flash)

    fun onFlashComplete()
}