package com.dh.myapplication.core.utils
import android.content.Context
import java.io.IOException

object FridaDetection {
    // Check if the Frida server process is running
    fun isFridaServerRunning(context: Context): Boolean {
        try {
            // Execute the 'ps' command using 'su' to list all processes
            val process = Runtime.getRuntime().exec(arrayOf("su", "-c", "ps"))
            // Read the output of the process
            val output = process.inputStream.bufferedReader().use { it.readText() }
            // Check if the output contains "frida-server" indicating that it's running
            return output.contains("frida-server")
        } catch (e: IOException) {
            // If an exception occurs, return false (Frida server not detected)
            return false
        }
    }
    // Check if either Frida server is running or a debugger is connected
    fun isFridaDetected(context: Context): Boolean {
        // Check if Frida server is running or debugger is connected
        return isFridaServerRunning(context) || isDebuggerConnected()
    }
    // Check if a debugger is connected to the app
    fun isDebuggerConnected(): Boolean {
        // Use Android's Debug class to check if debugger is connected
        return android.os.Debug.isDebuggerConnected()
    }
}



