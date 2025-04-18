package com.takaotech.dashboard.ui.platform

interface SymmetricCryptoManager {
    fun encryptFromByteArrayToByteArray(bytes: ByteArray): ByteArray
    fun decryptFromByteArrayToByteArray(bytes: ByteArray): ByteArray
}