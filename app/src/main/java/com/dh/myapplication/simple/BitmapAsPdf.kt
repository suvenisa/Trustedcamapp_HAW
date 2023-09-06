package com.dh.myapplication.simple

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.PorterDuff
import android.graphics.PorterDuffXfermode
import android.graphics.pdf.PdfDocument
import android.util.Log
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

class BitmapAsPdf {

    companion object {
        private const val TAG = "saveBitmapAsPdf"
    }


    fun saveBitmapAsPdf(context: Context, bitmap: Bitmap, filename: String): Pair<String, Boolean> {
        // Create a new PdfDocument object
        val pdfDocument = PdfDocument()

        // Define the page dimensions of the PDF document based on the size of the Bitmap
        val pageInfo = PdfDocument.PageInfo.Builder(bitmap.width, bitmap.height, 1).create()

        // Create a new page in the PdfDocument
        val page = pdfDocument.startPage(pageInfo)

        // Draw the Bitmap on the page
        val canvas = page.canvas

        val inverse2: Bitmap = invertQRCodeBitmap2(bitmap)
        val fullBitmap = addLayerOnBlack(inverse2)
//        val temp = convertTransparentToBlack(inverse2)
//        val temp2 = convertTransparentToBlack(bitmap)
//
//        val (totalPixels, totalColors, colorsCountMap) = analyzeBitmap(bitmap)
//        val (totalPixels2, totalColors2, colorsCountMap2) = analyzeBitmap(inverse2)
//        val (totalPixels3, totalColors3, colorsCountMap3) = analyzeBitmap(fullBitmap)
//
//
//        var dd = 89
//        dd = 45



        canvas.drawBitmap(fullBitmap, 0f, 0f, null)


        // End the page
        pdfDocument.finishPage(page)

        // Get the cache directory
        val cacheDir = context.cacheDir

        // Save the PdfDocument as a PDF file in the cache folder
//        val pdfFile = File(cacheDir, "$filename.pdf")
        val pdfFile = File(context.externalCacheDir, "$filename.pdf")

        var isSuccess = false
        try {
            val fileOutputStream = FileOutputStream(pdfFile)
            pdfDocument.writeTo(fileOutputStream)
            pdfDocument.close()
            fileOutputStream.close()
            isSuccess = true
            // The PDF was successfully saved
        } catch (e: IOException) {
            e.printStackTrace()
            // Error while saving the PDF
        }


        Log.i(TAG, "saveBitmapAsPdf: pdfFile.absolutePath: ${pdfFile.absolutePath}")


        return Pair(pdfFile.absolutePath, isSuccess)
    }

    fun addLayerOnBlack(layerBitmap: Bitmap): Bitmap {
        val width = layerBitmap.width
        val height = layerBitmap.height

        // Create a new mutable bitmap with the same dimensions as the layer bitmap
        val resultBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)

        // Create a canvas to draw on the result bitmap
        val canvas = Canvas(resultBitmap)

        // Draw the black background
        canvas.drawColor(Color.BLACK)

        // Draw the layer bitmap on top
        val paint = Paint()
        paint.alpha = 255 // Make sure the layer is fully opaque
        canvas.drawBitmap(layerBitmap, 0f, 0f, paint)

        return resultBitmap
    }

    fun analyzeBitmap(bitmap: Bitmap): Triple<Int, Int, Map<String, Int>> {
        val width = bitmap.width
        val height = bitmap.height

        val totalPixels = width * height
        val uniqueColorsMap = mutableMapOf<Int, Int>()

        for (x in 0 until width) {
            for (y in 0 until height) {
                val pixelColor = bitmap.getPixel(x, y)
                val colorCount = uniqueColorsMap[pixelColor] ?: 0
                uniqueColorsMap[pixelColor] = colorCount + 1
            }
        }

        val totalColors = uniqueColorsMap.size
        val colorsCountMap = mutableMapOf<String, Int>()

        for ((colorInt, count) in uniqueColorsMap) {
            val hexColor = String.format("#%06X", 0xFFFFFF and colorInt)
            colorsCountMap[hexColor] = count
        }

        // Logging the result
        Log.d("BitmapAnalysis", "Total Pixels: $totalPixels")
        Log.d("BitmapAnalysis", "Total Unique Colors: $totalColors")
        for ((hexColor, count) in colorsCountMap) {
            Log.d("BitmapAnalysis", "Color: $hexColor, Count: $count")
        }

        return Triple(totalPixels, totalColors, colorsCountMap)
    }


    fun saveBitmapAsPdfWithBlackBackground(context: Context, bitmap: Bitmap, filename: String): Pair<String, Boolean> {
        // Create a new PdfDocument object
        val pdfDocument = PdfDocument()

        // Define the page dimensions of the PDF document based on the size of the Bitmap
        val pageInfo = PdfDocument.PageInfo.Builder(bitmap.width, bitmap.height, 1).create()

        // Create a new page in the PdfDocument
        val page = pdfDocument.startPage(pageInfo)

        // Create a new bitmap with a black background
        val blackBackgroundBitmap = Bitmap.createBitmap(bitmap.width, bitmap.height, Bitmap.Config.ARGB_8888)
        val blackCanvas = Canvas(blackBackgroundBitmap)
        //  blackCanvas.drawColor(Color.WHITE)

        // Calculate the offset to center the original bitmap on the black background
        val offsetX = (bitmap.width - bitmap.width) / 2f
        val offsetY = (bitmap.height - bitmap.height) / 2f

        // Create a Paint with the correct blending mode to replace transparent pixels with black
        val paint = Paint().apply {
            xfermode = PorterDuffXfermode(PorterDuff.Mode.DST_IN)
        }


        // Draw the composite bitmap (black background + original bitmap) on the PDF page
        val canvas = page.canvas
        //  canvas.drawBitmap(blackBackgroundBitmap, 0f, 0f, null)

        // Draw the original transparent bitmap on top of the black background

//        val inverse: Bitmap = invertQRCodeBitmap(bitmap)
        val inverse2: Bitmap = invertQRCodeBitmap2(bitmap)
        var ff = 90
        ff = 56


        blackCanvas.drawBitmap(inverse2, offsetX, offsetY, null)

        // End the page
        pdfDocument.finishPage(page)

        // Save the PdfDocument as a PDF file in the cache folder
//        val pdfFile = File(cacheDir, "$filename.pdf")
        val pdfFile = File(context.externalCacheDir, "$filename.pdf")

        var isSuccess = false
        try {
            val fileOutputStream = FileOutputStream(pdfFile)
            pdfDocument.writeTo(fileOutputStream)
            pdfDocument.close()
            fileOutputStream.close()
            isSuccess = true
            // The PDF was successfully saved
        } catch (e: IOException) {
            e.printStackTrace()
            // Error while saving the PDF
        }


        Log.i(TAG, "saveBitmapAsPdf: pdfFile.absolutePath: ${pdfFile.absolutePath}")


        return Pair(pdfFile.absolutePath, isSuccess)
    }

    fun convertToPureWhite(value: Int, threshold: Int): Int {
        return if (value > threshold) 255 else value
    }

    fun invertQRCodeBitmap(qrCodeBitmap: Bitmap, threshold: Int = 128): Bitmap {
        val width = qrCodeBitmap.width
        val height = qrCodeBitmap.height
        val invertedBitmap = Bitmap.createBitmap(width, height, qrCodeBitmap.config)

        for (x in 0 until width) {
            for (y in 0 until height) {
                val color = qrCodeBitmap.getPixel(x, y)

                // Check if the pixel is black
                val isBlackPixel = Color.red(color) == 0 && Color.green(color) == 0 && Color.blue(color) == 0

                if (isBlackPixel) {
                    // If it's a black pixel, keep it unchanged
                    invertedBitmap.setPixel(x, y, color)
                } else {
                    // Calculate the luminance of the pixel
                    val luminance = (0.299 * Color.red(color) + 0.587 * Color.green(color) + 0.114 * Color.blue(color)).toInt()

                    // Adjust the luminance to pure white if it's slightly white
                    val adjustedLuminance = convertToPureWhite(luminance, threshold)

                    // Set the pixel to black or white based on the adjusted luminance and threshold
                    val invertedColor = if (adjustedLuminance < threshold) {
                        // The pixel is dark, convert to black
                        Color.rgb(0, 0, 0)
                    } else {
                        // The pixel is light, convert to white
                        Color.rgb(adjustedLuminance, adjustedLuminance, adjustedLuminance)
                    }
                    invertedBitmap.setPixel(x, y, invertedColor)
                }
            }
        }

        return invertedBitmap
    }


    fun invertQRCodeBitmap2(bitmap: Bitmap): Bitmap {
        val width = bitmap.width
        val height = bitmap.height

        // Create a new mutable bitmap with the same dimensions as the reference bitmap
        val invertedBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)

        for (x in 0 until width) {
            for (y in 0 until height) {
                // Get the color of the pixel at (x, y) in the reference bitmap
                val pixelColor = bitmap.getPixel(x, y)

                // Invert the color by changing each color component
                val invertedColor = pixelColor xor 0x00ffffff // XOR with 0x00ffffff inverts the color

                // Set the inverted color to the corresponding pixel in the new bitmap
                invertedBitmap.setPixel(x, y, invertedColor)
            }
        }

        return invertedBitmap
    }


    fun convertTransparentToBlack(bitmap: Bitmap): Bitmap {
        val width = bitmap.width
        val height = bitmap.height

        // Create a new mutable bitmap with the same dimensions as the reference bitmap
        val newBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)

        // Create a canvas to draw on the new bitmap
        val canvas = Canvas(newBitmap)
        canvas.drawColor(Color.BLACK) // Set the background color to black

        // Loop through each pixel in the reference bitmap
        for (x in 0 until width) {
            for (y in 0 until height) {
                // Get the color of the pixel at (x, y) in the reference bitmap
                val pixelColor = bitmap.getPixel(x, y)

                // Check if the pixel is transparent (alpha value is 0)
                if (Color.alpha(pixelColor) == 0) {
                    // Set the pixel color to black if it is transparent
                    val paint = Paint()
                    paint.color = Color.BLACK
                    canvas.drawPoint(x.toFloat(), y.toFloat(), paint)
                } else {
                    // Set the pixel color to the original color if it is not transparent
                    canvas.drawPoint(x.toFloat(), y.toFloat(), Paint())
                }
            }
        }

        return newBitmap
    }


    fun convertTransparentToBlackAndCountTransparency(bitmap: Bitmap): Pair<Bitmap, Int> {
        val width = bitmap.width
        val height = bitmap.height

        // Create a new mutable bitmap with the same dimensions as the reference bitmap
        val newBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)

        // Create a canvas to draw on the new bitmap
        val canvas = Canvas(newBitmap)
        canvas.drawColor(Color.BLACK) // Set the background color to black

        var transparentPixelCount = 0

        // Loop through each pixel in the reference bitmap
        for (x in 0 until width) {
            for (y in 0 until height) {
                // Get the color of the pixel at (x, y) in the reference bitmap
                val pixelColor = bitmap.getPixel(x, y)

                // Check if the pixel is transparent (alpha value is 0)
                if (Color.alpha(pixelColor) == 0) {
                    // Set the pixel color to black if it is transparent
                    val paint = Paint()
                    paint.color = Color.BLACK
                    canvas.drawPoint(x.toFloat(), y.toFloat(), paint)
                    transparentPixelCount++
                } else {
                    // Set the pixel color to the original color if it is not transparent
                    canvas.drawPoint(x.toFloat(), y.toFloat(), Paint())
                }
            }
        }

        return Pair(newBitmap, transparentPixelCount)
    }
}