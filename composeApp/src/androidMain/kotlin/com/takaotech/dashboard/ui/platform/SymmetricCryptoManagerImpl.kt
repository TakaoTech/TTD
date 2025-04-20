package com.takaotech.dashboard.ui.platform

import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import co.touchlab.kermit.Logger
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.charset.StandardCharsets
import java.security.KeyStore
import java.security.KeyStore.SecretKeyEntry
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi

// https://github.com/ClarkStoro/AndroidEncryptionExamples/blob/main/app/src/main/java/com/clarkstoro/androidencryptionexamples/utils/SymmetricCryptoManager.kt
@OptIn(ExperimentalEncodingApi::class)
class SymmetricCryptoManagerImpl : SymmetricCryptoManager, KoinComponent {
    private val logger by inject<Logger>()

    companion object {
        private const val KEYSTORE = "AndroidKeyStore"

        private const val ALGORITHM = KeyProperties.KEY_ALGORITHM_AES

        private const val GCM_BLOCK_MODE = KeyProperties.BLOCK_MODE_GCM
        private const val GCM_PADDING = KeyProperties.ENCRYPTION_PADDING_NONE
        private const val GCM_TAG_LENGTH = 128

        private const val CURRENT_BLOCK_MODE = GCM_BLOCK_MODE
        private const val CURRENT_PADDING = GCM_PADDING

        private const val TRANSFORMATION = "$ALGORITHM/$CURRENT_BLOCK_MODE/$CURRENT_PADDING"

        //        private const val ALIAS_KEY = "ENCRYPTION_ALIAS_KEY"
        private const val KEY_SIZE = 256

        private const val APPEND_SEPARATOR = "|||"
    }

    private val keystore =
        KeyStore.getInstance(KEYSTORE).apply {
            load(null)
        }

    private fun getEncryptCipher(aliasKey: String): Cipher =
        Cipher.getInstance(TRANSFORMATION).apply {
            init(Cipher.ENCRYPT_MODE, getKey(aliasKey))
        }

    private fun getDecryptCipherForIv(aliasKey: String, initializationVector: ByteArray): Cipher =
        Cipher.getInstance(TRANSFORMATION).apply {
            when (CURRENT_BLOCK_MODE) {
                GCM_BLOCK_MODE -> {
                    val gcmParameterSpec = GCMParameterSpec(GCM_TAG_LENGTH, initializationVector)
                    init(Cipher.DECRYPT_MODE, getKey(aliasKey), gcmParameterSpec)
                }

                else -> {
                    throw Exception()
                }
            }
        }

    private fun getKey(aliasKey: String): SecretKey = getValidKeyOrNull(aliasKey) ?: generateKey(aliasKey)

    private fun getValidKeyOrNull(aliasKey: String): SecretKey? =
        try {
            val existingKey = keystore.getEntry(aliasKey, null) as? SecretKeyEntry
            existingKey?.secretKey?.also { sk ->
                Cipher.getInstance(TRANSFORMATION).apply {
                    init(Cipher.ENCRYPT_MODE, sk)
                }
            }
        } catch (e: Exception) {
            null
        }

    private fun isKeyValid(aliasKey: String): Boolean = getValidKeyOrNull(aliasKey) != null

    private fun generateKey(aliasKey: String): SecretKey =
        KeyGenerator
            .getInstance(ALGORITHM)
            .apply {
                init(
                    KeyGenParameterSpec
                        .Builder(
                            aliasKey,
                            KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT,
                        ).setKeySize(KEY_SIZE)
                        .setBlockModes(CURRENT_BLOCK_MODE)
                        .setEncryptionPaddings(CURRENT_PADDING)
                        .build(),
                )
            }.generateKey()

    fun encryptStringAppendMode(key: String, plainText: String): String? {
        return try {
            val encryptCipher = getEncryptCipher(key)
            val cipherText = encryptCipher.doFinal(plainText.toByteArray())
            val cipherTextBase64 = Base64.encode(cipherText)
            val ivBase64 = Base64.encode(encryptCipher.iv)

            return "$ivBase64$APPEND_SEPARATOR$cipherTextBase64"
        } catch (e: Exception) {
            logger.e(e) { "Error: failed to encrypt string - Append Mode" }
            null
        }
    }

    fun decryptStringAppendMode(key: String, strToDecode: String): String? {
        return try {
            val ivAndCipherTextSplit = strToDecode.split(APPEND_SEPARATOR)
            val iv = Base64.decode(ivAndCipherTextSplit[0])
            val cipherText = Base64.decode(ivAndCipherTextSplit[1])

            val plainText = getDecryptCipherForIv(aliasKey = key, initializationVector = iv).doFinal(cipherText)

            return String(plainText, StandardCharsets.UTF_8)
        } catch (e: Exception) {
            logger.e(e) { "Error: failed to decrypt string - Append Mode" }
            null
        }
    }

    fun encryptToStringByteArrayMode(key: String, bytes: ByteArray): String? =
        try {
            val encryptCipher = getEncryptCipher(key)
            val cipherText = encryptCipher.doFinal(bytes)
            val outputStream = ByteArrayOutputStream()
            outputStream.use {
                it.write(encryptCipher.iv.size)
                it.write(encryptCipher.iv)
                it.write(cipherText.size)
                it.write(cipherText)
            }

            Base64.encode(outputStream.toByteArray())
        } catch (e: Exception) {
            logger.e(e) { "Error: failed to encrypt string - Byte Array Mode " }
            null
        }

    @OptIn(ExperimentalEncodingApi::class)
    override fun encryptFromByteArrayToByteArray(key: String, bytes: ByteArray): ByteArray =
        try {
            val encryptCipher = getEncryptCipher(key)
            val cipherText = encryptCipher.doFinal(bytes)
            val outputStream = ByteArrayOutputStream()
            outputStream.use {
                it.write(encryptCipher.iv.size)
                it.write(encryptCipher.iv)
                cipherText.size.also { size ->
                    val buffer = ByteBuffer.allocate(4)
                    buffer.order(ByteOrder.LITTLE_ENDIAN)
                    buffer.putInt(size)
                    it.write(buffer.array())
                }
                it.write(cipherText)
            }
//            Base64.encodeToString(outputStream.toByteArray(), Base64.DEFAULT)
            kotlin.io.encoding.Base64
                .encodeToByteArray(outputStream.toByteArray())
        } catch (e: Exception) {
            logger.e(e) { "Error: failed to encrypt string - Byte Array Mode " }
            throw e
//            null
        }

    fun decryptFromStringByteArrayMode(key: String, stringToDecode: String): ByteArray? =
        try {
            val decodedString = Base64.decode(stringToDecode)
            val inputStream = ByteArrayInputStream(decodedString)
            inputStream.use {
                val ivSize = it.read()
                val iv = ByteArray(ivSize)
                it.read(iv)

                val cipherTextBytesSize = it.read()
                val cipherTextBytes = ByteArray(cipherTextBytesSize)
                it.read(cipherTextBytes)

                getDecryptCipherForIv(aliasKey = key, initializationVector = iv).doFinal(cipherTextBytes)
            }
        } catch (e: Exception) {
            logger.e(e) { "Error: failed to decrypt string - Byte Array Mode " }
            null
        }

    override fun decryptFromByteArrayToByteArray(key: String, bytes: ByteArray): ByteArray =
        try {
            val inputStream = ByteArrayInputStream(bytes)
            inputStream.use {
                val ivSize = it.read()
                val iv = ByteArray(ivSize)
                it.read(iv)

                val cipherTextBytesSize =
                    ByteArray(4).let { siz ->
                        it.read(siz)
                        ByteBuffer
                            .wrap(siz)
                            .also {
                                it.order(ByteOrder.LITTLE_ENDIAN)
                            }.getInt()
                    }

                val cipherTextBytes = ByteArray(cipherTextBytesSize)
                it.read(cipherTextBytes)

                getDecryptCipherForIv(aliasKey = key, initializationVector = iv).doFinal(cipherTextBytes)
            }
        } catch (e: Exception) {
            logger.e(e) { "Error: failed to decrypt string - Byte Array Mode " }
            throw e
//            null
        }
}
