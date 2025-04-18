package com.takaotech.dashboard.ui.platform

import co.touchlab.kermit.Logger
import kotlinx.cinterop.ExperimentalForeignApi
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi

@OptIn(ExperimentalEncodingApi::class)
class SymmetricCryptoManager2(
    private val aliasKey: String,
) : KoinComponent {
    private val logger by inject<Logger>()

    @OptIn(ExperimentalEncodingApi::class, ExperimentalForeignApi::class)
    fun encryptFromByteArrayToByteArray(bytes: ByteArray): ByteArray =
        try {
            // In a real implementation, this would use Apple's Security framework
            // to encrypt the data. For now, we'll just Base64 encode it.
            Base64.encodeToByteArray(bytes)
        } catch (e: Exception) {
            logger.e(e) { "Error: failed to encrypt bytes" }
            throw e
        }

    fun decryptFromByteArray(bytes: ByteArray): ByteArray =
        try {
            // In a real implementation, this would use Apple's Security framework
            // to decrypt the data. For now, we'll just Base64 decode it.
            Base64.decode(bytes)
        } catch (e: Exception) {
            logger.e(e) { "Error: failed to decrypt bytes" }
            throw e
        }
}