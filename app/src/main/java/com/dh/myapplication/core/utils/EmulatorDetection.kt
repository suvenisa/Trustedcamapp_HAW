package com.dh.myapplication.core.utils
import android.content.Context
import android.os.Build


fun isEmulator(context: Context): Boolean {
    // Check if the Build brand and device are generic (common in emulators)
    return (Build.BRAND.startsWith("generic") && Build.DEVICE.startsWith("generic"))
            // Check if the Build fingerprint starts with "generic" (emulators)
            || Build.FINGERPRINT.startsWith("generic")
            // Check if the Build fingerprint starts with "unknown" (emulators)
            || Build.FINGERPRINT.startsWith("unknown")
            // Check if the hardware contains "goldfish" or "ranchu" (emulator hardware)
            || Build.HARDWARE.contains("goldfish")
            || Build.HARDWARE.contains("ranchu")
            // Check if the model contains common emulator identifiers
            || Build.MODEL.contains("google_sdk")
            || Build.MODEL.contains("Emulator")
            || Build.MODEL.contains("Android SDK built for x86")
            // Check if the manufacturer or product contains common emulator identifiers
            || Build.MANUFACTURER.contains("Genymotion")
            || Build.PRODUCT.contains("sdk_google")
            || Build.PRODUCT.contains("google_sdk")
            || Build.PRODUCT.contains("sdk")
            || Build.PRODUCT.contains("sdk_x86")
            || Build.PRODUCT.contains("vbox86p")
            || Build.PRODUCT.contains("emulator")
            || Build.PRODUCT.contains("simulator")
}
