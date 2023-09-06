package com.dh.myapplication.core.utils

import android.content.Context
import java.io.File

object MagiskDetection {
    // Function to check for Magisk presence
    fun isMagiskDetected(context: Context): Boolean {
        // Check if any of the Magisk detection methods return true
        return checkForSuBinary() || checkForMagiskFiles() || checkForMagiskManager(context)
    }
    // Function to check for known su binary paths
    private fun checkForSuBinary(): Boolean {
        // List of known su binary paths
        val paths = arrayOf(
            "/sbin/su",
            "/system/bin/su",
            "/system/xbin/su",
            "/data/local/xbin/su",
            "/data/local/bin/su",
            "/system/sd/xbin/su",
            "/system/bin/failsafe/su",
            "/data/local/su",
            "/su/bin/su"
        )
        // Check if any of the su binary paths exist
        for (path in paths) {
            if (File(path).exists()) {
                return true
            }
        }
        // No su binary found, return false
        return false
    }
    // Function to check for known Magisk-related file paths
    private fun checkForMagiskFiles(): Boolean {
        // List of known Magisk-related file paths
        val paths = arrayOf(
            "/sbin/magisk",
            "/sbin/.magisk",
            "/magisk",
            "/.core/mirror/data/magisk",
            "/data/magisk",
            "/cache/magisk",
            "/data/adb/modules/magisk",
            "/data/magisk_live",
            "/data/magisk_debug"
        )
        // Check if any of the Magisk-related file paths exist
        for (path in paths) {
            if (File(path).exists()) {
                return true
            }
        }
        // No Magisk-related files found, return false
        return false
    }
    // Function to check if Magisk Manager app is installed
    private fun checkForMagiskManager(context: Context): Boolean {
        // Define the package name for Magisk Manager
        val packageName = "com.topjohnwu.magisk"
        // Get the package manager
        val packageManager = context.packageManager
        // Attempt to get the launch intent for Magisk Manager
        val intent = packageManager.getLaunchIntentForPackage(packageName)
        // If intent is not null, Magisk Manager is installed
        return intent != null
    }
}
