package com.example.gandline

fun String.decodeTheHex(): ByteArray = chunked(2).map { it.toInt(16).toByte() }.toByteArray()

fun ByteArray.toHexString() = joinToString("") { "%02x".format(it) }

fun String.clear(): String {
    val resultString = StringBuilder()
    val lastIndex = this.indexOf(this.last())
    this.forEach {
        if (this.indexOf(it) == lastIndex) return resultString.toString()
        resultString.append(it)
    }
    return resultString.toString()
}