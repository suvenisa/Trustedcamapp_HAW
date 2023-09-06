package com.dh.myapplication.core

class BinaryConverter {

    companion object {
        private const val TAG = "BinaryConverter"

    }

    fun stringToBinary(input: String): String {
        val stringBuilder = StringBuilder()
        for (char in input) {
            val binary = Integer.toBinaryString(char.code)
            stringBuilder.append(binary.padStart(8, '0'))
        }
        return stringBuilder.toString()
    }

    // function to convert binary to a string
    fun binaryToString(input: String): String {
        val stringBuilder = StringBuilder()
        val binaryLength = input.length
        var i = 0
        while (i < binaryLength) {
            val binary = input.substring(i, i + 8)
            val char = binary.toInt(2).toChar()
            stringBuilder.append(char)
            i += 8
        }
        return stringBuilder.toString()
    }

}