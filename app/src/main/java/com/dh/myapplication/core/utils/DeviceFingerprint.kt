package com.dh.myapplication.core.utils
import android.content.Context
import android.os.Build

fun getFingerprint(context: Context): String {
    val fingerprint = StringBuilder()
   // fingerprint.append("BOARD: ${Build.BOARD}\n")
   // fingerprint.append("BOOTLOADER: ${Build.BOOTLOADER}\n")
   // fingerprint.append("BRAND: ${Build.BRAND}\n")
   // fingerprint.append("DEVICE: ${Build.DEVICE}\n")
 //   fingerprint.append("DISPLAY: ${Build.DISPLAY}\n")
   fingerprint.append("FINGERPRINT: ${Build.FINGERPRINT}\n")
 //   fingerprint.append("HARDWARE: ${Build.HARDWARE}\n")
  //  fingerprint.append("HOST: ${Build.HOST}\n")
  //  fingerprint.append("ID: ${Build.ID}\n")
  //  fingerprint.append("MANUFACTURER: ${Build.MANUFACTURER}\n")
   // fingerprint.append("MODEL: ${Build.MODEL}\n")
 //   fingerprint.append("PRODUCT: ${Build.PRODUCT}\n")
 //   fingerprint.append("SERIAL: ${Build.SERIAL}\n")
  //  fingerprint.append("TAGS: ${Build.TAGS}\n")
   // fingerprint.append("TYPE: ${Build.TYPE}\n")
   // fingerprint.append("USER: ${Build.USER}\n")
/*
    // Get camera sensor information
    val cameraManager = context.getSystemService(Context.CAMERA_SERVICE) as CameraManager
    val cameraIds = cameraManager.cameraIdList
    for (cameraId in cameraIds) {
        val characteristics = cameraManager.getCameraCharacteristics(cameraId)
        val facing = characteristics.get(CameraCharacteristics.LENS_FACING)
        fingerprint.append("CAMERA${cameraId}_LENS_FACING: ${facing}\n")
        val level = characteristics.get(CameraCharacteristics.INFO_SUPPORTED_HARDWARE_LEVEL)
        fingerprint.append("CAMERA${cameraId}_HARDWARE_LEVEL: ${level}\n")
        val manufacturer = characteristics.get(CameraCharacteristics.INFO_SUPPORTED_HARDWARE_LEVEL)
        fingerprint.append("CAMERA${cameraId}_MANUFACTURER: ${manufacturer}\n")
        val model = characteristics.get(CameraCharacteristics.INFO_SUPPORTED_HARDWARE_LEVEL)
        fingerprint.append("CAMERA${cameraId}_MODEL: ${model}\n")
    }

    // Get processor information
    val cpuInfo = getCpuInfo()
    val processorName = cpuInfo.firstOrNull { it.startsWith("Processor") }?.substringAfter(":")?.trim() ?: ""
    fingerprint.append("PROCESSOR: $processorName\n")
    fingerprint.append("SUPPORTED_ABIS: ${Build.SUPPORTED_ABIS.contentToString()}\n")

    return fingerprint.toString()
}

fun getCpuInfo(): List<String> {
    val output = mutableListOf<String>()
    try {
        val process = ProcessBuilder()
            .command("/system/bin/cat", "/proc/cpuinfo")
            .redirectErrorStream(true)
            .start()
        val reader = BufferedReader(InputStreamReader(process.inputStream))
        var line: String? = null
        while ({ line = reader.readLine(); line }() != null) {
            output.add(line ?: "")
        }
        reader.close()
        process.waitFor()
    } catch (e: Exception) {
        e.printStackTrace()
    }
    return output */
    return fingerprint.toString()
}
