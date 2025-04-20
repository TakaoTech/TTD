package com.takaotech.dashboard.ui.platform

interface SymmetricCryptoManager {
    fun encryptFromByteArrayToByteArray(key: String, bytes: ByteArray): ByteArray
    fun decryptFromByteArrayToByteArray(key: String, bytes: ByteArray): ByteArray
}