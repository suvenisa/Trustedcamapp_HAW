package com.dh.myapplication.core

import android.graphics.Bitmap
import android.util.Log
import androidmads.library.qrgenearator.QRGContents
import androidmads.library.qrgenearator.QRGEncoder
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.common.InputImage
import com.google.zxing.BinaryBitmap
import com.google.zxing.LuminanceSource
import com.google.zxing.MultiFormatReader
import com.google.zxing.RGBLuminanceSource
import com.google.zxing.common.HybridBinarizer
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine


class QrCodeHandler {

    companion object {
        private const val TAG = "QrCodeHandler"
        private const val smallerDimension = 200
    }

    fun generateQrCode(randomString: String): Bitmap {
        // Initializing the QR Encoder with your value to be encoded, type you required and Dimension
        val qrgEncoder = QRGEncoder(randomString, null, QRGContents.Type.TEXT, smallerDimension)
      qrgEncoder.colorWhite = android.R.color.black
        // Initialize the `bitmap` variable
//        qrgEncoder.colorBlack = android.R.color.white

        // Return the `bitmap` variable
        return qrgEncoder.bitmap
    }


/*
    fun readQrCodeText(bitmap: Bitmap?): String {
        Log.d("readQrCodeText", "Starting to read QR code text")
        if (bitmap != null) {
            val width = bitmap.width
            val height = bitmap.height
            val pixels = IntArray(width * height)
            bitmap.getPixels(pixels, 0, width, 0, 0, width, height)


            val source = RGBLuminanceSource(width, height, pixels)


            val luminanceSource: RGBLuminanceSource = RGBLuminanceSource(width, height, pixels)
            val binaryBitmap = com.google.zxing.BinaryBitmap(HybridBinarizer(luminanceSource))
            val reader = MultiFormatReader()
            var result: com.google.zxing.Result? = null
            try {
                result = reader.decode(binaryBitmap)
            } catch (e: Exception) {
                Log.e("readQrCodeText", "QR code not found")
            }

            if (result != null) {
                Log.d("readQrCodeText", "Successfully read QR code text: ${result.text}")
                return result.text
            } else {
                Log.d("readQrCodeText", "No QR code found")
                return ""
            }
        } else {
            Log.d("readQrCodeText", "No QR code found")
            return ""
        }
    }
*/

    fun scanQRImage(bMap: Bitmap): String? {
        var contents: String? = null
        val intArray = IntArray(bMap.width * bMap.height)
        //copy pixel data from the Bitmap into the 'intArray' array
        bMap.getPixels(intArray, 0, bMap.width, 0, 0, bMap.width, bMap.height)
        val source: LuminanceSource = RGBLuminanceSource(bMap.width, bMap.height, intArray)
        val bitmap = BinaryBitmap(HybridBinarizer(source))
        val reader = MultiFormatReader()
        try {
            val result = reader.decode(bitmap)
            contents = result.text
            Log.d("QrTest", "QR code scanned successfully")
        } catch (e: Exception) {
            Log.e("QrTest", "Error decoding qr code", e)
        }
        return contents
    }


    suspend fun QrcodeConverterTwo(bMap: Bitmap): String? {
        return withContext(Dispatchers.IO) {
            try {
                val image = InputImage.fromBitmap(bMap, 0)
                val options = BarcodeScannerOptions.Builder()
                    .setBarcodeFormats(Barcode.FORMAT_ALL_FORMATS)
                    .build()
                val scanner = BarcodeScanning.getClient(options)

                suspendCoroutine<String?> { continuation ->
                    scanner.process(image)
                        .addOnSuccessListener { barcodes ->
                            val barcode = barcodes.firstOrNull()
                            val rawValue = barcode?.rawValue
                            rawValue?.let {
                                Log.d("Barcode", it)
                                continuation.resume(it)
                            } ?: continuation.resume(null)
                        }
                        .addOnFailureListener { exception ->
                            Log.e("QrcodeConverterTwo", "Error scanning QR code", exception)
                            continuation.resume(null)
                        }
                }

            } catch (e: Exception) {
                Log.e("QrcodeConverterTwo", "Error scanning QR code", e)
                null
            }
        }
    }


}