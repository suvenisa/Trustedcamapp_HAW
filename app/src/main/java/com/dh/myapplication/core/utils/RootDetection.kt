package com.dh.myapplication.core.utils


import java.io.File
import java.io.FileInputStream

object RootDetection {
    // Function to check if the device is rooted using various methods
    fun isDeviceRooted(): Boolean {
        // Check if any of the root detection methods return true
        return checkRootMethod1() || checkRootMethod2()
    }
    // Method 1: Check if build tags contain "test-keys"
    private fun checkRootMethod1(): Boolean {
        // Get the build tags of the device
        val buildTags = android.os.Build.TAGS
        // If buildTags is not null and contains "test-keys", device might be rooted
        return buildTags != null && buildTags.contains("test-keys")
    }
    // Method 2: Check for common root-related file locations
    private fun checkRootMethod2(): Boolean {
        var fileInputStream: FileInputStream? = null
        // Array of common root-related file locations
        val places = arrayOf("/sbin/su", "/system/bin/su", "/system/xbin/su", "/data/local/xbin/su", "/data/local/bin/su", "/system/sd/xbin/su", "/system/bin/failsafe/su", "/data/local/su")
        try {
            // Iterate through the list of file paths
            for (place in places) {
                val file = File(place)
                if (file.exists()) {
                    // If the file exists, device might be rooted
                    fileInputStream = FileInputStream(file)
                    return true
                }
            }
        } catch (e: Exception) {
            // Do nothing (catching exceptions to continue checking other paths)
        } finally {
            // Close the FileInputStream if not null
            try {
                fileInputStream?.close()
            } catch (e: Exception) {
                // Do nothing
            }
        }
        // No rooted-related files found, device is likely not rooted
        return false
    }

    //private fun checkRootMethod3(): Boolean {
       // val process = Runtime.getRuntime().exec(arrayOf("/system/xbin/which", "su"))
       // val bufferedReader = BufferedReader(InputStreamReader(process.inputStream))
       // return bufferedReader.readLine() != null
   // }
}

