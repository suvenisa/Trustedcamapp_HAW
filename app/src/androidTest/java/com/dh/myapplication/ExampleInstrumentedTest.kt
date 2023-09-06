package com.dh.myapplication

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.dh.myapplication.core.BinaryConverter
import com.dh.myapplication.core.QrCodeHandler
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
class ExampleInstrumentedTest {
    @Test
    fun useAppContext() {
        // Context of the app under test.
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        assertEquals("com.dh.myapplication", appContext.packageName)


    }

    @Test
    fun testStringAndBinaryEquality() {
        val input = "Hello, World!"
        val binaryConverter = BinaryConverter()

        val binary = binaryConverter.stringToBinary(input)
        val convertedString = binaryConverter.binaryToString(binary)

        assertEquals(input, convertedString)
    }

    @Test
    suspend fun testGenerateAndReadQrCode() {
        val randomString = "Hello, QR Code!"
        val binaryConverter = QrCodeHandler()

        val qrCodeBitmap = binaryConverter.generateQrCode(randomString)
        val decodedText = binaryConverter.QrcodeConverterTwo(qrCodeBitmap)

        assertEquals(randomString, decodedText)
    }




}